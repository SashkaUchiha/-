package com.example.regapp.repository;

import com.example.regapp.entity.License;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LicenseRepository extends JpaRepository<License, Long> {
    License findByActivatedUuid(String activatedUuid);
    License findByLicenseKey(String licenseKey);
}
