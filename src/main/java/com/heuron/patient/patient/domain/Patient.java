package com.heuron.patient.patient.domain;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "patients")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false, length = 10)
    private String gender;

    @Column(nullable = false)
    private Boolean hasDisease;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PatientStatus status;

    @Column
    private String imageFileName;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Patient(String name, Integer age, String gender, Boolean hasDisease){
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.hasDisease = hasDisease;
        this.status = PatientStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void completeImageUpload(String imageFileName){
        this.imageFileName = imageFileName;
        this.status = PatientStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }

}
