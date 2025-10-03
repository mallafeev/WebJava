package com.example.demo.cars.api;

import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.core.api.PageAttributesMapper;
import com.example.demo.core.configuration.Constants;
import com.example.demo.cars.model.CarEntity;
import com.example.demo.cars.service.CarService;
import com.example.demo.packs.api.PackDto;
import com.example.demo.packs.model.PackEntity;
import com.example.demo.packs.service.PackService;
import com.example.demo.brands.api.BrandDto;
import com.example.demo.brands.model.BrandEntity;
import com.example.demo.brands.service.BrandService;
import com.example.demo.users.service.UserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping(CarController.URL)
public class CarController {
    public static final String URL = "/car";
    private static final String CAR_VIEW = "car";
    private static final String PAGE_ATTRIBUTE = "page";

    private static final String LIKE_VIEW = "car-desire";

    private final CarService carService;
    private final ModelMapper modelMapper;
    private final BrandService brandService;
    private final PackService packService;
    private final UserService userService;

    public CarController(CarService carService, ModelMapper modelMapper, PackService packService,
            BrandService brandService, UserService userService) {
        this.carService = carService;
        this.modelMapper = modelMapper;
        this.packService = packService;
        this.brandService = brandService;
        this.userService = userService;
    }

    private CarDto toDto(CarEntity entity) {
        var dto = new CarDto();
        dto.setId(entity.getId());
        dto.setPacks(entity.getCarPacks().stream().map(PackEntity::getId).toList());
        dto.setDescription(entity.getDescription());
        dto.setName(entity.getName());
        dto.setBrandId(entity.getBrand().getId());
        dto.setPrice(entity.getPrice());
        return dto;
    }

    @GetMapping
    public String getAll(
            @RequestParam(name = PAGE_ATTRIBUTE, defaultValue = "0") int page,
            Model model) {
        final Map<String, Object> attrib = PageAttributesMapper.toAttributes(
                carService.getAll(page, Constants.DEFUALT_PAGE_SIZE), this::toDto);
        model.addAllAttributes(attrib);
        model.addAttribute(PAGE_ATTRIBUTE, page);
        model.addAttribute(
                "items",
                carService.getAll().stream()
                        .map(this::toDto)
                        .toList());
        return CAR_VIEW;
    }

    @GetMapping("/desire")
    public String getDesires(@RequestParam(name = PAGE_ATTRIBUTE, defaultValue = "0") int page,
            Model model) {
        final Long id = userService.getUserIdLong();
        model.addAttribute(
                "items",
                userService.getUserCars(id).stream()
                        .map(this::toDto)
                        .toList());
        return LIKE_VIEW;
    }

    @PostMapping("/desire/{carId}")
    public String addDesire(@PathVariable(name = "carId") Long carId,
            @RequestParam(name = PAGE_ATTRIBUTE, defaultValue = "0") int page,
            RedirectAttributes redirectAttributes) {
        redirectAttributes.addAttribute(PAGE_ATTRIBUTE, page);
        final Long userid = userService.getUserIdLong();
        userService.addCar(userid, carId);
        return Constants.REDIRECT_VIEW + "/list/car";
    }

    @PostMapping("/disdesire1/{id}")
    public String deleteDesire1(
            @PathVariable(name = "id") Long id, @RequestParam(name = PAGE_ATTRIBUTE, defaultValue = "0") int page,
            RedirectAttributes redirectAttributes) {
        redirectAttributes.addAttribute(PAGE_ATTRIBUTE, page);
        final Long userid = userService.getUserIdLong();
        userService.deleteCar(userid, id);
        return Constants.REDIRECT_VIEW + "/list/car";
    }

    @PostMapping("/disdesire2/{id}")
    public String deleteDesire2(
            @PathVariable(name = "id") Long id, @RequestParam(name = PAGE_ATTRIBUTE, defaultValue = "0") int page,
            RedirectAttributes redirectAttributes) {
        redirectAttributes.addAttribute(PAGE_ATTRIBUTE, page);
        final Long userid = userService.getUserIdLong();
        userService.deleteCar(userid, id);
        return Constants.REDIRECT_VIEW + URL + "/desire";
    }
}
