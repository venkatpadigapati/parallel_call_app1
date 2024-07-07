package com.parallel.calls.proj1.classes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class SimpleAsyncService {
	
	private final String SERVICE_2_URL = "http://localhost:8082/users2/getUsersByIds";


	@Autowired
    private UserRepository userRepository;
	
	@Autowired
    private RestTemplate restTemplate;

    @Async("taskExecutor")
    public CompletableFuture<Void> asyncMethod(int batchId) {
        System.out.println("Starting async method for batch: " + batchId + " at " + System.currentTimeMillis() + " by thread: " + Thread.currentThread().getName());
        long processingTime = 5000; // 5 seconds
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < processingTime) {
            // Simulate work
        }
        System.out.println("Completed async method for batch: " + batchId + " at " + System.currentTimeMillis() + " by thread: " + Thread.currentThread().getName());
        return CompletableFuture.completedFuture(null);
    }
    
    @Async("taskExecutor")
    public CompletableFuture<List<AppUser>> getUsersByIds(List<Long> ids) {
        return fetchAndSaveUserData(ids);
    }
    
    @Async//("taskExecutor")
    public CompletableFuture<List<AppUser>> fetchAndSaveUserData(List<Long> batch) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(SERVICE_2_URL)
                .queryParam("ids", batch);

        System.out.println("Starting to process batch: " + batch + " at " + System.currentTimeMillis() + " by thread: " + Thread.currentThread().getName());
        try {
            AppUser[] users = restTemplate.postForObject(builder.toUriString(), batch, AppUser[].class);
            List<AppUser> listUsers = Arrays.asList(users);
            userRepository.saveAll(listUsers);
            System.out.println("Completed processing batch: " + batch + " at " + System.currentTimeMillis() + " by thread: " + Thread.currentThread().getName());
            return CompletableFuture.completedFuture(listUsers);
        } catch (Exception e) {
            System.out.println("Exception occurred while processing batch: " + batch);
            e.printStackTrace();
            return CompletableFuture.completedFuture(Collections.emptyList());
        }
    }
}
