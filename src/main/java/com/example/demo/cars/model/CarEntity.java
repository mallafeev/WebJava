package com.example.demo.cars.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.example.demo.core.model.BaseEntity;
import com.example.demo.packs.model.PackEntity;
import com.example.demo.brands.model.BrandEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.FetchType;

@Entity
@Table(name = "cars")
public class CarEntity extends BaseEntity {
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    @Min(value = 100)
    private Integer price;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "packs_cars", joinColumns = @JoinColumn(name = "car_id"), inverseJoinColumns = @JoinColumn(name = "pack_id"))
    private Set<PackEntity> carPacks = new HashSet<>();
    @ManyToOne
    @JoinColumn(name = "brandId", nullable = false)
    private BrandEntity brand;

    public CarEntity() {
    }

    public CarEntity(String name, String decs, BrandEntity brand, Integer price) {
        this.name = name;
        this.description = decs;
        this.brand = brand;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BrandEntity getBrand() {
        return brand;
    }

    public void setBrand(BrandEntity brand) {
        this.brand = brand;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String desc) {
        this.description = desc;
    }

    public Set<PackEntity> getCarPacks() {
        return carPacks;
    }

    public void addPack(PackEntity carPack) {
        carPacks.add(carPack);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, brand, price);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        CarEntity other = (CarEntity) obj;
        return Objects.equals(id, other.id)
                && Objects.equals(name, other.name)
                && Objects.equals(brand, other.brand)
                && Objects.equals(price, other.price)
                && Objects.equals(description, other.description);
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

}
