package com.heuron.patient.patient.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PatientCreateRequest (
    @NotBlank String name,
    @NotNull @Min(0) Integer age,
    @NotBlank String gender,
    @NotNull Boolean hasDisease
){

}
