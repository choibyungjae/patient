package com.heuron.patient.patient.dto.response;

public record PatientResponse(
        String name,
        Integer age,
        String gender,
        Boolean hasDisease,
        String imageUrl
) {
}