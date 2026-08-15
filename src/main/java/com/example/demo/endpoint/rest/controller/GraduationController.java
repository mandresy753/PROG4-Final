package com.example.demo.endpoint.rest.controller;

import com.example.demo.enums.Track;
import com.example.demo.model.report.Graduate;
import com.example.demo.service.GraduationService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/graduates")
@AllArgsConstructor
public class GraduationController {

    private final GraduationService graduationService;

    @GetMapping
    public List<Graduate> listGraduates(@RequestParam Track track) {
        return graduationService.listGraduates(track);
    }
}