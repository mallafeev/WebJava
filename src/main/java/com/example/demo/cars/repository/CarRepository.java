package com.example.demo.cars.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.example.demo.cars.model.CarEntity;
import com.example.demo.packs.model.PackEntity;

import jakarta.transaction.Transactional;

public interface CarRepository extends CrudRepository<CarEntity, Long>, PagingAndSortingRepository<CarEntity, Long> {
    Optional<CarEntity> findByNameIgnoreCase(String name);

    @Query("SELECT f FROM CarEntity f JOIN f.carPacks g WHERE g.id = :packId")
    List<CarEntity> findAllByPackId(@Param("packId") long packId);

    List<CarEntity> findAllByBrandId(Long brandId);

    @Modifying
    @Transactional
    @Query("DELETE FROM CarEntity f WHERE :pack MEMBER OF f.carPacks")
    void deleteAllByPack(@Param("pack") PackEntity pack);

}
