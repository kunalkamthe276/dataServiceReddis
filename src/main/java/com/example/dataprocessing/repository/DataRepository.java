package com.example.dataprocessing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.dataprocessing.model.entity.DataEntity;

@Repository
public interface DataRepository extends JpaRepository<DataEntity, Long> {
    // Custom queries if needed
}