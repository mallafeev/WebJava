package com.example.demo.users.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.example.demo.core.configuration.Constants;
import com.example.demo.core.error.NotFoundException;
import com.example.demo.core.security.UserPrincipal;
import com.example.demo.cars.model.CarEntity;
import com.example.demo.cars.repository.CarRepository;
import com.example.demo.users.model.UserEntity;
import com.example.demo.users.model.UserRole;
import com.example.demo.users.repository.UserRepository;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository repository;
    private final CarRepository carRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository repository,
            PasswordEncoder passwordEncoder,
            CarRepository carRepository) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.carRepository = carRepository;
    }

    private void checkLogin(Long id, String login) {
        final Optional<UserEntity> existsUser = repository.findByLoginIgnoreCase(login);
        if (existsUser.isPresent() && !existsUser.get().getId().equals(id)) {
            throw new IllegalArgumentException(
                    String.format("User with login %s is already exists", login));
        }
    }

    @Transactional(readOnly = true)
    public List<UserEntity> getAll() {
        return StreamSupport.stream(repository.findAll().spliterator(), false).toList();
    }

    @Transactional(readOnly = true)
    public Page<UserEntity> getAll(int page, int size) {
        return repository.findAll(PageRequest.of(page, size, Sort.by("id")));
    }

    @Transactional(readOnly = true)
    public UserEntity get(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(UserEntity.class, id));
    }

    @Transactional(readOnly = true)
    public UserEntity getByLogin(String login) {
        return repository.findByLoginIgnoreCase(login)
                .orElseThrow(() -> new IllegalArgumentException("Invalid login"));
    }

    @Transactional
    public UserEntity addCar(long userId, Long carId) {
        UserEntity us = repository.findById(userId)
                .orElseThrow(() -> new NotFoundException(UserEntity.class, userId));

        CarEntity fm = carRepository.findById(carId)
                .orElseThrow(() -> new NotFoundException(CarEntity.class, carId));
        us.addCar(fm);
        return repository.save(us);
    }

    @Transactional
    public UserEntity deleteCar(long userId, Long carId) {
        UserEntity us = repository.findById(userId)
                .orElseThrow(() -> new NotFoundException(UserEntity.class, userId));

        CarEntity fm = carRepository.findById(carId)
                .orElseThrow(() -> new NotFoundException(CarEntity.class, carId));
        us.deleteCar(fm);
        return repository.save(us);
    }

    @Transactional
    public UserEntity create(UserEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity is null");
        }
        checkLogin(null, entity.getLogin());
        final String password = Optional.ofNullable(entity.getPassword()).orElse("");
        entity.setPassword(
                passwordEncoder.encode(
                        StringUtils.hasText(password.strip()) ? password : Constants.DEFAULT_PASSWORD));
        entity.setRole(Optional.ofNullable(entity.getRole()).orElse(UserRole.USER));
        repository.save(entity);
        return repository.save(entity);
    }

    @Transactional
    public UserEntity update(long id, UserEntity entity) {
        final UserEntity existsEntity = get(id);
        checkLogin(id, entity.getLogin());
        existsEntity.setLogin(entity.getLogin());
        repository.save(existsEntity);
        return existsEntity;
    }

    @Transactional
    public UserEntity delete(long id) {
        final UserEntity existsEntity = get(id);
        repository.delete(existsEntity);
        return existsEntity;
    }

    @Transactional(readOnly = true)
    public List<CarEntity> getUserCars(long id) {
        return get(id).getUserCars().stream()
                .toList();
    }

    @Transactional
    public List<CarEntity> deleteUserCars(long id, List<Long> carsIds) {
        final UserEntity existsUser = get(id);
        final List<CarEntity> cars = existsUser.getUserCars().stream()
                .filter(car -> carsIds.contains(car.getId()))
                .toList();
        cars.forEach(existsUser::deleteCar);
        repository.save(existsUser);
        return cars.stream()
                .toList();
    }

    @Transactional
    public List<CarEntity> deleteAllUserCars(long id) {
        final UserEntity existsUser = get(id);
        final List<CarEntity> cars = existsUser.getUserCars().stream()
                .toList();
        for (CarEntity carEntity : cars) {
            existsUser.getUserCars().remove(carEntity);
        }
        repository.save(existsUser);
        return cars.stream()
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final UserEntity existsUser = getByLogin(username);
        return new UserPrincipal(existsUser);
    }

    public Long getUserIdLong() {
        Authentication aut = SecurityContextHolder.getContext().getAuthentication();
        if (aut != null && aut.getPrincipal() instanceof UserDetails) {
            return ((UserPrincipal) aut.getPrincipal()).getId();
        }
        return null;
    }

    public boolean existByUserIdAndCarId(Long userId, Long carId) {
        return repository.existsByUserIdAndCarId(userId, carId);
    }
}
