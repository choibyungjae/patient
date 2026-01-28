package com.heuron.patient.patient.repository;

import com.heuron.patient.patient.domain.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {
}
