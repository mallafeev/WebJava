package com.example.demo.cars.service;

import java.util.List;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.core.error.NotFoundException;
import com.example.demo.cars.model.CarEntity;
import com.example.demo.cars.repository.CarRepository;
import com.example.demo.packs.model.PackEntity;
import com.example.demo.packs.repository.PackRepository;

@Service
public class CarService {
    private final CarRepository repository;
    private final PackRepository packRepository;
    private final Logger log = LoggerFactory.getLogger(CarService.class);

    public CarService(CarRepository repository, PackRepository packRepository) {
        this.repository = repository;
        this.packRepository = packRepository;
    }

    @Transactional(readOnly = true)
    public List<CarEntity> getAll() {
        return StreamSupport.stream(repository.findAll().spliterator(), false).toList();
    }

    @Transactional(readOnly = true)
    public Page<CarEntity> getAll(int page, int size) {
        return repository.findAll(PageRequest.of(page, size, Sort.by("id")));
    }

    @Transactional(readOnly = true)
    public CarEntity get(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(CarEntity.class, id));
    }

    @Transactional(readOnly = true)
    public List<PackEntity> getCarPacks(long id) {
        return get(id).getCarPacks().stream()
                // .map(PackEntity::getPack)
                .toList();
    }

    @Transactional
    public CarEntity addCarPacks(long carId, long packId) {
        CarEntity fm = repository.findById(carId)
                .orElseThrow(() -> new NotFoundException(CarEntity.class, carId));
        PackEntity gn = packRepository.findById(packId)
                .orElseThrow(() -> new NotFoundException(PackEntity.class, packId));
        fm.addPack(gn);
        return repository.save(fm);
    }

    @Transactional
    public CarEntity create(CarEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity is null");
        }
        return repository.save(entity);
    }

    @Transactional
    public CarEntity update(Long id, CarEntity entity) {
        final CarEntity existsEntity = get(id);
        existsEntity.setName(entity.getName());
        existsEntity.setDescription(entity.getDescription());
        existsEntity.setBrand(entity.getBrand());
        existsEntity.setPrice(entity.getPrice());
        var packs = entity.getCarPacks();
        for (var pack : packs) {
            existsEntity.addPack(pack);
        }
        return repository.save(existsEntity);
    }

    @Transactional
    public CarEntity delete(Long id) {
        final CarEntity existsEntity = get(id);
        repository.delete(existsEntity);
        return existsEntity;
    }

}
