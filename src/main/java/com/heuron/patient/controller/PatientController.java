package com.heuron.patient.controller;

import com.heuron.patient.patient.dto.request.PatientCreateRequest;
import com.heuron.patient.patient.dto.response.PatientResponse;
import com.heuron.patient.patient.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/patients")
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long createPatient(@RequestBody @Valid PatientCreateRequest request) {
        return patientService.createPatient(request);
    }

    @PostMapping("/{patientId}/image")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void uploadImage(@PathVariable Long patientId, @RequestPart("image")MultipartFile image) throws IOException{
        patientService.uploadImage(patientId, image);
    }

    @GetMapping("/{patientId}")
    public PatientResponse getPatient(@PathVariable Long patientId) {
        return patientService.getPatient(patientId);
    }

    @DeleteMapping("/{patientId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePatient(@PathVariable Long patientId){
        patientService.deletePatient(patientId);
    }

}
