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
@RequestMapping(CarAdminController.URL)
public class CarAdminController {
    public static final String URL = Constants.ADMIN_PREFIX + "/car";
    private static final String CAR_VIEW = "car-admin";
    private static final String CAR_EDIT_VIEW = "car-edit";
    private static final String PAGE_ATTRIBUTE = "page";
    private static final String CAR_ATTRIBUTE = "car";

    private final CarService carService;
    private final BrandService brandService;
    private final PackService packService;
    private final ModelMapper modelMapper;

    public CarAdminController(CarService carService, ModelMapper modelMapper, PackService packService,
            BrandService brandService) {
        this.packService = packService;
        this.brandService = brandService;
        this.carService = carService;
        this.modelMapper = modelMapper;
    }

    private CarDto toDto(CarEntity entity) {
        var dto = new CarDto();
        dto.setId(entity.getId());
        dto.setPacks(entity.getCarPacks().stream().map(PackEntity::getId).toList());
        dto.setDescription(entity.getDescription());
        dto.setPrice(entity.getPrice());
        dto.setName(entity.getName());
        dto.setBrandId(entity.getBrand().getId());

        return dto;
    }

    private CarEntity toEntity(CarDto dto) {
        final CarEntity entity = modelMapper.map(dto, CarEntity.class);
        entity.setBrand(brandService.get(dto.getBrandId()));
        var packs = dto.getPacks();
        List<PackEntity> packsList = packService.getAllById(packs);
        for (var pack : packsList) {
            entity.addPack(pack);
        }
        return entity;
    }

    @GetMapping
    public String getAll(@RequestParam(name = PAGE_ATTRIBUTE, defaultValue = "0") int page, Model model) {
        final Map<String, Object> attributes = PageAttributesMapper.toAttributes(
                carService.getAll(page, Constants.DEFUALT_PAGE_SIZE), this::toDto);
        model.addAllAttributes(attributes);
        model.addAttribute(PAGE_ATTRIBUTE, page);
        return CAR_VIEW;
    }

    private BrandDto toBrandDto(BrandEntity entity) {
        return modelMapper.map(entity, BrandDto.class);
    }

    private PackDto toPackDto(PackEntity entity) {
        return modelMapper.map(entity, PackDto.class);
    }

    @GetMapping("/edit/")
    public String create(@RequestParam(name = PAGE_ATTRIBUTE, defaultValue = "0") int page, Model model) {
        model.addAttribute(CAR_ATTRIBUTE, new CarDto());
        model.addAttribute("brands", brandService.getAll().stream().map(this::toBrandDto).toList());
        model.addAttribute("packsCheck", packService.getAll().stream().map(this::toPackDto).toList());
        model.addAttribute(PAGE_ATTRIBUTE, page);
        return CAR_EDIT_VIEW;
    }

    @PostMapping("/edit/")
    public String create(
            @RequestParam(name = PAGE_ATTRIBUTE, defaultValue = "0") int page,
            @ModelAttribute(name = CAR_ATTRIBUTE) @Valid CarDto car,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(PAGE_ATTRIBUTE, page);
            return CAR_EDIT_VIEW;
        }
        carService.create(toEntity(car));
        redirectAttributes.addAttribute(PAGE_ATTRIBUTE, page);
        return Constants.REDIRECT_VIEW + URL;
    }

    @GetMapping("/edit/{id}")
    public String update(
            @PathVariable(name = "id") Long id,
            @RequestParam(name = PAGE_ATTRIBUTE, defaultValue = "0") int page,
            Model model) {
        if (id <= 0) {
            throw new IllegalArgumentException();
        }
        model.addAttribute(CAR_ATTRIBUTE, toDto(carService.get(id)));
        model.addAttribute("brands", brandService.getAll().stream().map(this::toBrandDto).toList());
        model.addAttribute("packsCheck", packService.getAll().stream().map(this::toPackDto).toList());
        model.addAttribute(PAGE_ATTRIBUTE, page);
        return CAR_EDIT_VIEW;
    }

    @PostMapping("/edit/{id}")
    public String update(
            @PathVariable(name = "id") Long id,
            @RequestParam(name = PAGE_ATTRIBUTE, defaultValue = "0") int page,
            @ModelAttribute(name = CAR_ATTRIBUTE) @Valid CarDto car,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(PAGE_ATTRIBUTE, page);
            return CAR_EDIT_VIEW;
        }
        if (id <= 0) {
            throw new IllegalArgumentException();
        }
        model.addAttribute("brands", brandService.getAll().stream().map(this::toBrandDto).toList());
        model.addAttribute("packsCheck", packService.getAll().stream().map(this::toPackDto).toList());
        redirectAttributes.addAttribute(PAGE_ATTRIBUTE, page);
        carService.update(id, toEntity(car));
        return Constants.REDIRECT_VIEW + URL;
    }

    @PostMapping("/delete/{id}")
    public String delete(
            @PathVariable(name = "id") Long id,
            @RequestParam(name = PAGE_ATTRIBUTE, defaultValue = "0") int page,
            RedirectAttributes redirectAttributes) {
        carService.delete(id);
        redirectAttributes.addAttribute(PAGE_ATTRIBUTE, page);
        return Constants.REDIRECT_VIEW + URL;
    }
}