package com.example.demo.users.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.example.demo.users.model.UserEntity;

public interface UserRepository
        extends CrudRepository<UserEntity, Long>, PagingAndSortingRepository<UserEntity, Long> {
    Optional<UserEntity> findByLoginIgnoreCase(String login);

    @Query("SELECT CASE WHEN COUNT(uf) > 0 THEN true ELSE false END " +
            "FROM UserEntity u JOIN u.userCars uf " +
            "WHERE u.id = :userId AND uf.id = :carId")
    boolean existsByUserIdAndCarId(@Param("userId") Long userId, @Param("carId") Long carId);
}
