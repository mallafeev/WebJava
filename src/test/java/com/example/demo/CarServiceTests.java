package com.example.demo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import com.example.demo.cars.service.*;
import com.example.demo.cars.model.*;
import com.example.demo.brands.model.*;
import com.example.demo.brands.service.*;

import com.example.demo.core.error.NotFoundException;

@SpringBootTest
class CarServiceTests {

    @Autowired
    private CarService carService;
    @Autowired
    private BrandService brandService;
    private final BrandEntity brand1;

    public CarServiceTests() {
        brand1 = brandService.create(new BrandEntity("aboba"));
    }

    @BeforeEach
    void createData() {
        // Создаем тестовые данные перед каждым тестом
        carService.create(new CarEntity("Toyota", "Camry", brand1, 25000));
        carService.create(new CarEntity("Honda", "Civic", brand1, 22000));
    }

    @AfterEach
    void removeData() {
        // Удаляем тестовые данные после каждого теста
        carService.getAll().forEach(car -> carService.delete(car.getId()));
    }

    @Test
    void getTest() {
        // Тест получения существующей машины
        CarEntity car = carService.getAll().get(0);
        Assertions.assertEquals(car, carService.get(car.getId()));
    }

    @Test
    void createTest() {
        // Тест создания новой машины
        CarEntity newCar = new CarEntity("BMW", "X5", brand1, 60000);
        carService.create(newCar);
        Assertions.assertTrue(carService.getAll().contains(newCar));
    }

    @Test
    void updateTest() {
        // Тест обновления данных о машине
        CarEntity car = carService.getAll().get(0);
        car.setDescription("Test Description");
        carService.update(car.getId(), car);
        CarEntity updatedCar = carService.get(car.getId());
        Assertions.assertEquals("Test Description", updatedCar.getDescription());
    }

    @Test
    void deleteTest() {
        // Тест удаления машины
        CarEntity car = carService.getAll().get(0);
        carService.delete(car.getId());
        Assertions.assertFalse(carService.getAll().contains(car));
    }
}