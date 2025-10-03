package com.example.demo.users.api;

import java.util.ArrayList;
import java.util.List;

import com.example.demo.cars.api.CarDto;

public class UserProfileDto {
    private List<CarDto> cars = new ArrayList<>();

    public UserProfileDto() {
    }

    public UserProfileDto(List<CarDto> cars) {
        this.cars = cars;
    }

    public List<CarDto> getCars() {
        return cars;
    }

    public void setSubscriptions(List<CarDto> cars) {
        this.cars = cars;
    }
}
