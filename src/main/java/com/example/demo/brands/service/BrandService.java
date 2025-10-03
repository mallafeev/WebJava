package com.example.demo.brands.service;

import java.util.List;
import java.util.stream.StreamSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.core.error.NotFoundException;
import com.example.demo.brands.model.BrandEntity;
import com.example.demo.brands.repository.BrandRepository;

import jakarta.persistence.EntityManager;

@Service
public class BrandService {
    private final BrandRepository repository;
    private final EntityManager entityManager;

    public BrandService(BrandRepository repository, EntityManager entityManager) {
        this.repository = repository;
        this.entityManager = entityManager;
    }

    private void checkName(String name) {
        if (repository.findByNameIgnoreCase(name).isPresent()) {
            throw new IllegalArgumentException(
                    String.format("Brand with name %s is already exists", name));
        }
    }

    @Transactional(readOnly = true)
    public Page<BrandEntity> getAll(int page, int size) {
        return repository.findAll(PageRequest.of(page, size, Sort.by("id")));
    }

    @Transactional(readOnly = true)
    public List<BrandEntity> getAll() {
        return StreamSupport.stream(repository.findAll().spliterator(), false).toList();
    }

    @Transactional(readOnly = true)
    public BrandEntity get(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(BrandEntity.class, id));
    }

    @Transactional
    public BrandEntity create(BrandEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity is null");
        }
        checkName(entity.getName());
        return repository.save(entity);
    }

    @Transactional
    public BrandEntity update(Long id, BrandEntity entity) {
        final BrandEntity existsEntity = get(id);
        checkName(entity.getName());
        existsEntity.setName(entity.getName());
        return repository.save(existsEntity);
    }

    @Transactional
    public BrandEntity delete(Long id) {
        final BrandEntity existsEntity = get(id);
        entityManager.createQuery("DELETE FROM CarEntity f WHERE f.brand = :brand")
                .setParameter("brand", existsEntity)
                .executeUpdate();
        repository.delete(existsEntity);
        return existsEntity;
    }
}
