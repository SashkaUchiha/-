package com.example.regapp.service;

import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Slf4j
public class HashService {
    public static String md5(String inputData) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(inputData.getBytes());
            byte[] hashedData = messageDigest.digest();
            BigInteger bigInteger = new BigInteger(1, hashedData);
            return bigInteger.toString(16);
        } catch (NoSuchAlgorithmException e) {
            log.error("Error while password encoding");
        }
        return null;
    }
}
