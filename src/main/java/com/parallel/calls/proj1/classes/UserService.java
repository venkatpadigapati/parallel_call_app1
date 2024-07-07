package com.parallel.calls.proj1.classes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


@Service
public class UserService {
	
	int i = 1;
    private final String SERVICE_2_URL = "http://localhost:8082/users2/getUsersByIds";


	@Autowired
    private UserRepository userRepository;
	
	@Autowired
    private RestTemplate restTemplate;

	@Autowired
	private SimpleAsyncService simpleAsyncService;
	
    public List<AppUser> getAllUsers() {
        return userRepository.findAll();
    }

    public AppUser createUser(@RequestBody AppUser user) {
    	i = i+1;
    	user.setName(user.getName() + " " + i);
    	user.setEmail(user.getEmail() + " " + i);
        return userRepository.save(user);
    }
    
    public CompletableFuture<List<AppUser>> getUsersByIds(List<Long> ids) {
		
		  int batchSize = 5; 
		  List<List<Long>> batches = new ArrayList<>(); 
		  for (int i = 0; i < ids.size(); i += batchSize) { 
			  int end = Math.min(i + batchSize, ids.size()); 
			  batches.add(new ArrayList<>(ids.subList(i, end)));
		  }
		 

		  List<CompletableFuture<List<AppUser>>> futures = new ArrayList<>();

		
		  for (List<Long> batch : batches) { 
			  futures.add(simpleAsyncService.fetchAndSaveUserData(batch));
		  }
		 

        System.out.println("All tasks submitted at: " + System.currentTimeMillis());

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .flatMap(List::stream)
                        .collect(Collectors.toList()));
    }

}
