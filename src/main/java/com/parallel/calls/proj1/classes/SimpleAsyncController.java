package com.parallel.calls.proj1.classes;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SimpleAsyncController {

    @Autowired
    private SimpleAsyncService simpleAsyncService;

    @GetMapping("/testAsync")
    public String testAsync() {
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            futures.add(simpleAsyncService.asyncMethod(i));
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        return "All async tasks completed";
    }
}

