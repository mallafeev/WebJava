package com.example.demo.cars.api;

import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.demo.core.configuration.Constants;
import com.example.demo.users.service.UserService;
import com.example.demo.core.api.PageAttributesMapper;
import com.example.demo.cars.model.CarEntity;
import com.example.demo.cars.service.CarService;
import com.example.demo.packs.model.PackEntity;

@Controller
@RequestMapping(CarListController.URL)
public class CarListController {
    public static final String URL = "/list";
    private static final String CAR_VIEW = "car";
    private static final String PAGE_ATTRIBUTE = "page";

    private final CarService carService;
    private final UserService userService;

    public CarListController(CarService car,
            UserService user) {
        carService = car;
        userService = user;
    }

    private CarDtoDesire toDto(CarEntity entity) {
        final Long userId = userService.getUserIdLong();
        final CarDtoDesire dto = new CarDtoDesire();
        dto.setId(entity.getId());
        dto.setPacks(entity.getCarPacks().stream().map(PackEntity::getId).toList());
        dto.setDescription(entity.getDescription());
        dto.setName(entity.getName());
        dto.setPrice(entity.getPrice());
        dto.setBrandId(entity.getBrand().getId());
        boolean fg = userService.existByUserIdAndCarId(userId, entity.getId());
        dto.setIsDesired(fg);
        return dto;
    }

    @GetMapping("/car")
    public String getAll(@RequestParam(name = PAGE_ATTRIBUTE, defaultValue = "0") int page,
            Model model) {
        final Map<String, Object> attrib = PageAttributesMapper.toAttributes(
                carService.getAll(page, Constants.DEFUALT_PAGE_SIZE), this::toDto);
        model.addAllAttributes(attrib);
        model.addAttribute(PAGE_ATTRIBUTE, page);
        return CAR_VIEW;
    }
}
