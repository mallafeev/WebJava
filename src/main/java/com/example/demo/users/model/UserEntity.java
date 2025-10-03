package com.example.demo.users.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.example.demo.core.model.BaseEntity;
import com.example.demo.cars.model.CarEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class UserEntity extends BaseEntity {
    @Column(nullable = false, unique = true, length = 20)
    private String login;
    @Column(nullable = false, length = 60)
    private String password;
    private UserRole role;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_cars", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "car_id"))
    private Set<CarEntity> userCars = new HashSet<>();

    public UserEntity() {
    }

    public UserEntity(String login, String password) {
        this.login = login;
        this.password = password;
        this.role = UserRole.USER;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public Set<CarEntity> getUserCars() {
        return userCars;
    }

    public void addCar(CarEntity userCar) {
        userCars.add(userCar);
    }

    public void deleteCar(CarEntity userCar) {
        userCars.remove(userCar);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, login, password, role, userCars);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        UserEntity other = (UserEntity) obj;
        return Objects.equals(other.getId(), id)
                && Objects.equals(other.getLogin(), login)
                && Objects.equals(other.getPassword(), password)
                && Objects.equals(other.getRole(), role)
                && Objects.equals(other.getUserCars(), userCars);
    }
}
