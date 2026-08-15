package com.example.demo.model;

import java.util.UUID;
import lombok.Builder;

@Builder
public record Group(UUID id, String reference) {}
