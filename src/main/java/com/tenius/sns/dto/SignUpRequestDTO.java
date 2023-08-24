package com.tenius.sns.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequestDTO {
    @NotBlank
    @Size(min=8, max=15)
    private String username;
    @NotBlank
    @Size(min=12, max=20)
    private String password;
    @Email
    @Size(max=50)
    private String email;
    @NotBlank
    @Size(min=2, max=10)
    private String nickname;
}
