package com.example.regapp.service;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Slf4j
public class HashService {
    private static String generateFileHash(File file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] fileBytes = Files.readAllBytes(file.toPath());
            byte[] hashBytes = digest.digest(fileBytes);
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String[] hashPassword(String password) {
        try {
            // Генерация случайной соли
            byte[] salt = new byte[16];
            SecureRandom random = new SecureRandom();
            random.nextBytes(salt);

            // Хеширование пароля с солью
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(salt);
            messageDigest.update(password.getBytes());
            byte[] hashedData = messageDigest.digest();

            // Кодируем соль и хеш в base64
            String saltBase64 = Base64.getEncoder().encodeToString(salt);
            String hashBase64 = Base64.getEncoder().encodeToString(hashedData);

            return new String[]{saltBase64, hashBase64};

        } catch (NoSuchAlgorithmException e) {
            log.error("Error while password encoding", e);
        }
        return null;
    }

    public static String[] hashPasswordWithSalt(String password, String saltBase64) {
        try {
            byte[] salt = Base64.getDecoder().decode(saltBase64);
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(salt);
            messageDigest.update(password.getBytes());
            byte[] hashedData = messageDigest.digest();
            String hashBase64 = Base64.getEncoder().encodeToString(hashedData);
            return new String[]{saltBase64, hashBase64}; // Возвращаем соль и хеш
        } catch (NoSuchAlgorithmException e) {
            log.error("Error while password encoding with salt", e);
        }
        return null;
    }
}


