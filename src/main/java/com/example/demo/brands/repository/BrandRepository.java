package com.example.demo.brands.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import com.example.demo.brands.model.BrandEntity;

public interface BrandRepository
        extends CrudRepository<BrandEntity, Long>, PagingAndSortingRepository<BrandEntity, Long> {
    Optional<BrandEntity> findByNameIgnoreCase(String name);

}
