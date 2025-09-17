package com.abhish.shani_shop.dto;

import java.util.HashSet;
import java.util.Set;

import com.abhish.shani_shop.enums.RoleType;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    @Enumerated(EnumType.STRING)
    private Set<RoleType> roles = new HashSet<>();

}
