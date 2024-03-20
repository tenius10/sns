package com.tenius.sns.dto;

import com.tenius.sns.service.AuthService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDTO {
    @NotBlank
    @Size(min= AuthService.MIN_USERNAME_LENGTH, max=AuthService.MAX_USERNAME_LENGTH)
    private String username;
    @NotBlank
    @Size(min=AuthService.MIN_PASSWORD_LENGTH, max=AuthService.MAX_PASSWORD_LENGTH)
    private String password;
}
