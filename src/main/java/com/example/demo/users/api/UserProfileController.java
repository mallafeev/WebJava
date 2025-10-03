package com.example.demo.users.api;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.core.configuration.Constants;
import com.example.demo.core.security.UserPrincipal;
import com.example.demo.cars.api.CarDto;
import com.example.demo.cars.model.CarEntity;
import com.example.demo.users.service.UserService;

import jakarta.validation.Valid;

@Controller
public class UserProfileController {
    private static final String PROFILE_VIEW = "profile";

    private static final String PAGE_ATTRIBUTE = "page";
    private static final String PROFILE_ATTRIBUTE = "profile";

    private final UserService userService;
    private final ModelMapper modelMapper;

    public UserProfileController(
            UserService userService,
            ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    private CarDto tCarDto(CarEntity entity) {
        return modelMapper.map(entity, CarDto.class);
    }

    @GetMapping
    public String getProfile(
            @RequestParam(name = PAGE_ATTRIBUTE, defaultValue = "0") int page,
            Model model,
            @AuthenticationPrincipal UserPrincipal principal) {
        return PROFILE_VIEW;
    }

    @PostMapping
    public String saveProfile(
            @ModelAttribute(name = PROFILE_ATTRIBUTE) @Valid UserProfileDto profile,
            BindingResult bindResult,
            Model model,
            @AuthenticationPrincipal UserPrincipal principal) {
        if (bindResult.hasErrors()) {
            return PROFILE_VIEW;
        }
        return Constants.REDIRECT_VIEW + "/";
    }
}
