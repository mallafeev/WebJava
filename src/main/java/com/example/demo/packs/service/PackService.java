package com.example.demo.packs.service;

import java.util.List;
import java.util.stream.StreamSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.core.error.NotFoundException;
import com.example.demo.packs.model.PackEntity;
import com.example.demo.packs.repository.PackRepository;

import com.example.demo.cars.repository.CarRepository;

@Service
public class PackService {
    private final PackRepository repository;
    private final CarRepository fmrepository;

    public PackService(PackRepository repository, CarRepository fmrepository) {
        this.repository = repository;
        this.fmrepository = fmrepository;
    }

    private void checkName(String name) {
        if (repository.findByNameIgnoreCase(name).isPresent()) {
            throw new IllegalArgumentException(
                    String.format("Pack with name %s is already exists", name));
        }
    }

    @Transactional(readOnly = true)
    public List<PackEntity> getAll() {
        return StreamSupport.stream(repository.findAll().spliterator(), false).toList();
    }

    @Transactional(readOnly = true)
    public Page<PackEntity> getAll(int page, int size) {
        return repository.findAll(PageRequest.of(page, size, Sort.by("id")));
    }

    @Transactional(readOnly = true)
    public List<PackEntity> getAllById(List<Long> listIds) {
        return StreamSupport.stream(repository.findAllById(listIds).spliterator(), false).toList();
    }

    @Transactional(readOnly = true)
    public PackEntity get(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(PackEntity.class, id));
    }

    @Transactional
    public PackEntity create(PackEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity is null");
        }
        checkName(entity.getName());
        return repository.save(entity);
    }

    @Transactional
    public PackEntity update(Long id, PackEntity entity) {
        final PackEntity existsEntity = get(id);
        checkName(entity.getName());
        existsEntity.setName(entity.getName());
        return repository.save(existsEntity);
    }

    @Transactional
    public PackEntity delete(Long id) {
        final PackEntity existsEntity = get(id);
        fmrepository.deleteAllByPack(existsEntity);
        repository.delete(existsEntity);
        return existsEntity;
    }
}
