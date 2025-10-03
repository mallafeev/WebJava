package com.example.demo.packs.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.example.demo.packs.model.PackEntity;

public interface PackRepository extends CrudRepository<PackEntity, Long>, PagingAndSortingRepository<PackEntity, Long> {
    Optional<PackEntity> findByNameIgnoreCase(String name);
}
