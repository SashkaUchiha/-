package com.example.regapp.service;

import com.example.regapp.entity.License;
import com.example.regapp.repository.LicenseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LicenseService {
    private final LicenseRepository licenseRepository;

    public boolean saveLicense(String licenseKey) {
        License license = licenseRepository.findByLicenseKey(licenseKey);
        if (license == null) {
            log.info("Entered license is not found");
            System.out.println((licenseRepository.findAll()));
            return false;
        }

        if (license.getActivatedUuid() != null) {
            log.info("License is already used");
            return false;
        }

        license.setActivatedUuid(UUIDService.getMotherBoardUUID());
        licenseRepository.save(license);
        return true;
    }
}
