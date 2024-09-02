package com.picsimplificado.dtos;

import com.picsimplificado.enums.UserType;

import java.math.BigDecimal;

public record UserDTO(String name, String lastName, String document, BigDecimal balance, String email, String password, UserType userType) {
}
