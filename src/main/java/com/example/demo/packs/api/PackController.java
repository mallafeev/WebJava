package com.example.demo.packs.api;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.Map;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.core.api.PageAttributesMapper;

import com.example.demo.core.configuration.Constants;
import com.example.demo.packs.model.PackEntity;
import com.example.demo.packs.service.PackService;

import jakarta.validation.Valid;

@Controller
@RequestMapping(PackController.URL)
public class PackController {
    public static final String URL = Constants.ADMIN_PREFIX + "/pack";
    private static final String PACK_VIEW = "pack";
    private static final String PACK_EDIT_VIEW = "pack-edit";
    private static final String PAGE_ATTRIBUTE = "page";
    private static final String PACK_ATTRIBUTE = "pack";

    private final PackService packService;
    private final ModelMapper modelMapper;

    public PackController(PackService packService, ModelMapper modelMapper) {
        this.packService = packService;
        this.modelMapper = modelMapper;
    }

    private PackDto toDto(PackEntity entity) {
        return modelMapper.map(entity, PackDto.class);
    }

    private PackEntity toEntity(PackDto dto) {
        return modelMapper.map(dto, PackEntity.class);
    }

    @GetMapping
    public String getAll(@RequestParam(name = PAGE_ATTRIBUTE, defaultValue = "0") int page, Model model) {
        final Map<String, Object> attributes = PageAttributesMapper.toAttributes(
                packService.getAll(page, Constants.DEFUALT_PAGE_SIZE), this::toDto);
        model.addAllAttributes(attributes);
        model.addAttribute(PAGE_ATTRIBUTE, page);
        return PACK_VIEW;
    }

    @GetMapping("/edit/")
    public String create(@RequestParam(name = PAGE_ATTRIBUTE, defaultValue = "0") int page, Model model) {
        model.addAttribute(PACK_ATTRIBUTE, new PackDto());
        model.addAttribute(PAGE_ATTRIBUTE, page);
        return PACK_EDIT_VIEW;
    }

    @PostMapping("/edit/")
    public String create(@RequestParam(name = PAGE_ATTRIBUTE, defaultValue = "0") int page,
            @ModelAttribute(name = PACK_ATTRIBUTE) @Valid PackDto pack,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(PAGE_ATTRIBUTE, page);
            return PACK_EDIT_VIEW;
        }
        redirectAttributes.addAttribute(PAGE_ATTRIBUTE, page);
        packService.create(toEntity(pack));
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
        model.addAttribute(PACK_ATTRIBUTE, toDto(packService.get(id)));
        model.addAttribute(PAGE_ATTRIBUTE, page);
        return PACK_EDIT_VIEW;
    }

    @PostMapping("/edit/{id}")
    public String update(
            @PathVariable(name = "id") Long id,
            @ModelAttribute(name = PACK_ATTRIBUTE) @Valid PackDto pack,
            @RequestParam(name = PAGE_ATTRIBUTE, defaultValue = "0") int page,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(PAGE_ATTRIBUTE, page);
            return PACK_EDIT_VIEW;
        }
        if (id <= 0) {
            throw new IllegalArgumentException();
        }
        redirectAttributes.addAttribute(PAGE_ATTRIBUTE, page);
        packService.update(id, toEntity(pack));
        return Constants.REDIRECT_VIEW + URL;
    }

    @PostMapping("/delete/{id}")
    public String delete(
            @PathVariable(name = "id") Long id,
            @RequestParam(name = PAGE_ATTRIBUTE, defaultValue = "0") int page,
            RedirectAttributes redirectAttributes) {
        redirectAttributes.addAttribute(PAGE_ATTRIBUTE, page);
        packService.delete(id);
        return Constants.REDIRECT_VIEW + URL;
    }
}
