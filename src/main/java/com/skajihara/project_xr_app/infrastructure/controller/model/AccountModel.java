package com.skajihara.project_xr_app.infrastructure.controller.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class AccountModel {

    @NotNull
    @Size(max = 20)
    private String id;

    @NotNull
    @Size(max = 50)
    private String name;

    @NotNull
    @Size(max = 200)
    private String bio;

    @NotNull
    @Size(max = 100)
    private String icon;

    @NotNull
    @Size(max = 100)
    private String headerPhoto;

    @Size(max = 50)
    private String location;

    private LocalDate birthday;

    @NotNull
    private LocalDate registered;

    @PositiveOrZero
    private int following;

    @PositiveOrZero
    private int follower;

    @NotNull
    private int validFlag;

    @NotNull
    private int deleteFlag;

    public AccountModel() {
    }
}
