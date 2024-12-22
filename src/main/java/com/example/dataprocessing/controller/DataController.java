package com.example.dataprocessing.controller;


import java.util.concurrent.CompletableFuture;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.dataprocessing.model.entity.DataEntity;
import com.example.dataprocessing.repository.DataRepository;
import com.example.dataprocessing.service.DataService;

@RestController
@RequestMapping("/api/data")
public class DataController {
    private final DataService dataService;
    private final DataRepository dataRepository;

    public DataController(DataService dataService, DataRepository dataRepository) {
        this.dataService = dataService;
        this.dataRepository = dataRepository;
    }

    @GetMapping("/{id}")
    public CompletableFuture<DataEntity> getData(@PathVariable Long id) {
        String cacheKey = "data:" + id;
        return dataService.processDataAsync(cacheKey, () -> 
            dataRepository.findById(id).orElseThrow(() -> 
                new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @PostMapping
    public CompletableFuture<DataEntity> createData(@RequestBody DataEntity data) {
        return CompletableFuture.supplyAsync(() -> {
            DataEntity saved = dataRepository.save(data);
            dataService.invalidateCache("data:" + saved.getId());
            return saved;
        });
    }
}