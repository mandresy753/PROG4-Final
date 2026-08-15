package com.example.demo.model;

import lombok.Builder;

import java.util.UUID;

@Builder
public record Group(UUID id, String reference) {}