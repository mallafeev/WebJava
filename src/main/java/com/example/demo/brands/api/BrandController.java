package com.example.demo.brands.api;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.demo.core.api.PageAttributesMapper;
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
import com.example.demo.core.configuration.Constants;
import com.example.demo.brands.model.BrandEntity;
import com.example.demo.brands.service.BrandService;

import jakarta.validation.Valid;

@Controller
@RequestMapping(BrandController.URL)
public class BrandController {
    public static final String URL = Constants.ADMIN_PREFIX + "/brand";
    private static final String BRAND_VIEW = "brand";
    private static final String BRAND_EDIT_VIEW = "brand-edit";
    private static final String PAGE_ATTRIBUTE = "page";
    private static final String BRAND_ATTRIBUTE = "brand";

    private final BrandService brandService;
    private final ModelMapper modelMapper;

    public BrandController(BrandService brandService, ModelMapper modelMapper) {
        this.brandService = brandService;
        this.modelMapper = modelMapper;
    }

    private BrandDto toDto(BrandEntity entity) {
        return modelMapper.map(entity, BrandDto.class);
    }

    private BrandEntity toEntity(BrandDto dto) {
        return modelMapper.map(dto, BrandEntity.class);
    }

    @GetMapping
    public String getAll(@RequestParam(name = PAGE_ATTRIBUTE, defaultValue = "0") int page,
            Model model) {
        final Map<String, Object> attributes = PageAttributesMapper.toAttributes(
                brandService.getAll(page, Constants.DEFUALT_PAGE_SIZE), this::toDto);
        model.addAllAttributes(attributes);
        model.addAttribute(PAGE_ATTRIBUTE, page);
        return BRAND_VIEW;
    }

    @GetMapping("/edit/")
    public String create(@RequestParam(name = PAGE_ATTRIBUTE, defaultValue = "0") int page, Model model) {
        model.addAttribute(BRAND_ATTRIBUTE, new BrandDto());
        model.addAttribute(PAGE_ATTRIBUTE, page);
        return BRAND_EDIT_VIEW;
    }

    @PostMapping("/edit/")
    public String create(@RequestParam(name = PAGE_ATTRIBUTE, defaultValue = "0") int page,
            @ModelAttribute(name = BRAND_ATTRIBUTE) @Valid BrandDto brand,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(PAGE_ATTRIBUTE, page);
            return BRAND_EDIT_VIEW;
        }
        redirectAttributes.addAttribute(PAGE_ATTRIBUTE, page);
        brandService.create(toEntity(brand));
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
        model.addAttribute(BRAND_ATTRIBUTE, toDto(brandService.get(id)));
        model.addAttribute(PAGE_ATTRIBUTE, page);
        return BRAND_EDIT_VIEW;
    }

    @PostMapping("/edit/{id}")
    public String update(
            @PathVariable(name = "id") Long id,
            @RequestParam(name = PAGE_ATTRIBUTE, defaultValue = "0") int page,
            @ModelAttribute(name = BRAND_ATTRIBUTE) @Valid BrandDto brand,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(PAGE_ATTRIBUTE, page);
            return BRAND_EDIT_VIEW;
        }
        if (id <= 0) {
            throw new IllegalArgumentException();
        }
        redirectAttributes.addAttribute(PAGE_ATTRIBUTE, page);
        brandService.update(id, toEntity(brand));
        return Constants.REDIRECT_VIEW + URL;
    }

    @PostMapping("/delete/{id}")
    public String delete(
            @PathVariable(name = "id") Long id,
            @RequestParam(name = PAGE_ATTRIBUTE, defaultValue = "0") int page,
            RedirectAttributes redirectAttributes) {
        redirectAttributes.addAttribute(PAGE_ATTRIBUTE, page);
        brandService.delete(id);
        return Constants.REDIRECT_VIEW + URL;
    }
}
