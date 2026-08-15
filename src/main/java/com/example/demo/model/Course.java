package com.example.demo.model;

import com.example.demo.enums.Track;
import lombok.Builder;

import java.util.UUID;

@Builder
public record Course(UUID id, String ref, String title, Integer creditCount, Track track) {}