package com.example.regapp.service;

import com.example.regapp.entity.License;
import com.example.regapp.repository.LicenseRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class UUIDService {
    private static String MOTHERBOARD_SERIAL_NUMBER_COMMAND = "wmic baseboard get serialnumber";

    @SneakyThrows
    public static String getMotherBoardUUID() {
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(MOTHERBOARD_SERIAL_NUMBER_COMMAND);

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        reader.readLine();
        reader.readLine();

        return reader.readLine().strip();
    }
}
