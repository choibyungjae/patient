package com.heuron.patient.patient.service;

import com.heuron.patient.patient.domain.Patient;
import com.heuron.patient.patient.domain.PatientStatus;
import com.heuron.patient.patient.dto.request.PatientCreateRequest;
import com.heuron.patient.patient.dto.response.PatientResponse;
import com.heuron.patient.patient.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.io.IOException;
import java.nio.file.*;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    private static final Set<String> ALLOWED_EXT = Set.of("png", "jpg", "jpeg");

    @Value("${app.upload-dir}")
    private String uploadDir;

    @Transactional
    public Long createPatient(PatientCreateRequest request){
        Patient patient = new Patient(
                request.name(),
                request.age(),
                request.gender(),
                request.hasDisease()
        );

        Patient saved = patientRepository.save(patient);
        return saved.getId();
    }

    @Transactional
    public void uploadImage(Long patientId, MultipartFile image) throws IOException {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found. id=" + patientId));

        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("Image file is required.");
        }

        String original = image.getOriginalFilename();
        String ext = getExtension(original);
        if (!ALLOWED_EXT.contains(ext)) {
            throw new IllegalArgumentException("Only png/jpg/jpeg allowed. ext=" + ext);
        }

        Path dirPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(dirPath);

        String savedFileName = UUID.randomUUID() + "." + ext;
        Path target = dirPath.resolve(savedFileName);

        // 덮어쓰기 방지 + 안전 저장
        try {
            Files.copy(image.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IOException("Failed to save image.", e);
        }

        // 상태 전이 (PENDING -> COMPLETED)
        patient.completeImageUpload(savedFileName);
    }

    @Transactional(readOnly = true)
    public PatientResponse getPatient(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Patient not found. id=" + patientId));

        if (patient.getStatus() != PatientStatus.COMPLETED) {
            // 1단계만 저장된 환자는 조회 불가 (요구사항)
            throw new ResponseStatusException(CONFLICT, "Patient image not uploaded yet. id=" + patientId);
        }

        String imageUrl = "/images/" + patient.getImageFileName();

        return new PatientResponse(
                patient.getName(),
                patient.getAge(),
                patient.getGender(),
                patient.getHasDisease(),
                imageUrl
        );
    }


    private String getExtension(String filename) {
        if (filename == null) return "";
        int idx = filename.lastIndexOf('.');
        if (idx < 0 || idx == filename.length() - 1) return "";
        return filename.substring(idx + 1).toLowerCase();
    }

    @Transactional
    public void deletePatient(Long patientId){
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found. id=" + patientId));

        if (patient.getImageFileName() != null && !patient.getImageFileName().isBlank()) {
            Path dirPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Path imagePath = dirPath.resolve(patient.getImageFileName());

            try {
                Files.deleteIfExists(imagePath);
            } catch (Exception e) {
                throw new IllegalStateException("Failed to delete image file.", e);
            }
        }

        patientRepository.delete(patient);
    }

}
