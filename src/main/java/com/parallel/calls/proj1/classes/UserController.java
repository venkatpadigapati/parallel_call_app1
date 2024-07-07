package com.parallel.calls.proj1.classes;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<AppUser> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    public AppUser createUser(@RequestBody AppUser user) {
        return userService.createUser(user);
    }
    
    @PostMapping("/getUsersByIds")
    public List<AppUser> getUsersByIds(@RequestBody List<Long> ids) throws InterruptedException, ExecutionException {
        CompletableFuture<List<AppUser>> processingFuture = userService.getUsersByIds(ids);
        List<AppUser> result = processingFuture.get();  // This will block until all batches are processed
        System.out.println("All batches processed at: " + System.currentTimeMillis());
        return result;
    }
}

