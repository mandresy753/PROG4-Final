package com.example.demo.mapper;

import com.example.demo.entity.JGrade;
import com.example.demo.model.Grade;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class GradeMapper {

    private final ExamMapper examMapper;
    private final UserMapper userMapper;

    public Grade toModel(JGrade entity) {
        return Grade.builder()
                .id(entity.getId())
                .exam(examMapper.toModel(entity.getExam()))
                .student(userMapper.toModel(entity.getStudent()))
                .value(entity.getValue())
                .entryDate(entity.getEntryDate())
                .enteredBy(userMapper.toModel(entity.getEnteredBy()))
                .reason(entity.getReason())
                .build();
    }

    public JGrade toEntity(Grade model) {
        return JGrade.builder()
                .id(model.id())
                .exam(examMapper.toEntity(model.exam()))
                .student(userMapper.toEntity(model.student()))
                .value(model.value())
                .entryDate(model.entryDate())
                .enteredBy(userMapper.toEntity(model.enteredBy()))
                .reason(model.reason())
                .build();
    }
}