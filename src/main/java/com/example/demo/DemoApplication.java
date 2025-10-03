package com.example.demo;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.demo.core.configuration.Constants;
import com.example.demo.cars.model.CarEntity;
import com.example.demo.cars.service.CarService;
import com.example.demo.packs.model.PackEntity;
import com.example.demo.packs.service.PackService;
import com.example.demo.brands.model.BrandEntity;
import com.example.demo.brands.service.BrandService;
import com.example.demo.users.model.UserEntity;
import com.example.demo.users.model.UserRole;
import com.example.demo.users.service.UserService;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {
    private final Logger log = LoggerFactory.getLogger(DemoApplication.class);

    private final UserService userService;
    private final CarService carService;
    private final BrandService brandService;
    private final PackService packService;

    public DemoApplication(
            UserService userService,
            BrandService brandService,
            CarService carService,
            PackService packService) {
        this.userService = userService;
        this.carService = carService;
        this.packService = packService;
        this.brandService = brandService;
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        log.info("Create default brand values");
        final var brand1 = brandService.create(new BrandEntity("Ламборгини"));
        final var brand2 = brandService.create(new BrandEntity("Феррари"));
        final var brand3 = brandService.create(new BrandEntity("Тойота"));

        log.info("Create default pack values");
        final var pack1 = packService.create(new PackEntity("Дрифт"));
        final var pack2 = packService.create(new PackEntity("Про"));
        packService.create(new PackEntity("Базовый"));

        log.info("Create default user values");
        final var admin = new UserEntity("admin", "admin");
        admin.setRole(UserRole.ADMIN);
        userService.create(admin);

        final var user1 = userService.create(new UserEntity("user1", Constants.DEFAULT_PASSWORD));

        IntStream.range(2, 15)
                .forEach(value -> userService.create(
                        new UserEntity("user".concat(String.valueOf(value)), Constants.DEFAULT_PASSWORD)));

        IntStream.range(2, 15)
                .forEach(value -> brandService.create(
                        new BrandEntity("Toyota " + value)));

        IntStream.range(2, 15)
                .forEach(value -> packService.create(
                        new PackEntity("Drift " + value)));

        log.info("Create default car values");
        carService.create(new CarEntity("Автомобиль Ламба", "Крутое авто для парня", brand1, 5000));
        carService.create(new CarEntity("Автомобиль Ферра", "Супер мощное авто 90 л.с.", brand2, 6000));
        carService.create(new CarEntity("Супер Пушка", "Гоночный автомобиль", brand3, 2000));
        carService.create(new CarEntity("Дрифт Гонка", "Дрифт автомобиль", brand1, 1000));
        carService.create(new CarEntity("абоба", "абоба", brand1, 9999));
        carService.create(new CarEntity("абоба", "абоба", brand1, 9999));
        carService.create(new CarEntity("абоба", "абоба", brand1, 9999));
        carService.create(new CarEntity("абоба", "абоба", brand1, 9999));
        carService.create(new CarEntity("абоба", "абоба", brand1, 9999));
        carService.create(new CarEntity("абоба", "абоба", brand1, 9999));

        var cars = carService.getAll();
        for (CarEntity carEntity : cars) {
            carService.addCarPacks(carEntity.getId(), pack1.getId());
            carService.addCarPacks(carEntity.getId(), pack2.getId());
        }

        log.info("DONE");
    }
}
