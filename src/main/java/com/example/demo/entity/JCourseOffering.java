package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(
        name = "course_offerings",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"course_id", "academic_year_id", "group_id"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JCourseOffering {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private JCourse course;

    @ManyToOne(optional = false)
    @JoinColumn(name = "academic_year_id", nullable = false)
    private JAcademicYear academicYear;

    @ManyToOne(optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private JGroup group;
}