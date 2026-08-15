package com.example.demo.model;

import com.example.demo.enums.UserRole;
import lombok.Builder;

import java.util.UUID;

@Builder
public record User(UUID id, String lastName, String firstName, String email, String password, UserRole role) {}