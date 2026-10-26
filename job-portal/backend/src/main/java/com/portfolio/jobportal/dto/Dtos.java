package com.portfolio.jobportal.dto;

import com.portfolio.jobportal.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class Dtos {
    public record RegisterRequest(
            @NotBlank String name,
            @NotBlank @Email String email,
            @NotBlank @Size(min = 6, message = "must be at least 6 characters") String password,
            @NotNull Role role) {}

    public record LoginRequest(@NotBlank String email, @NotBlank String password) {}

    public record AuthResponse(String token, String name, String email, Role role) {}

    public record JobRequest(
            @NotBlank String title,
            @NotBlank String company,
            @NotBlank String location,
            @NotBlank @Size(max = 2000) String description) {}
}
