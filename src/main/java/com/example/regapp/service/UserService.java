package com.example.regapp.service;

import com.example.regapp.entity.User;
import com.example.regapp.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class UserService {
    @Autowired
    private UserRepository userRepository;

    private final Map<String, Integer> userEnteringTries = new ConcurrentHashMap<>();

    public User findUserByIdentifier(String identifier) {
        User user = userRepository.findUserByIdentifier(identifier);
        if (user == null) {
            System.out.println("no user");
            return user;
        }
        else {
            return user;
        }
    }

    public boolean registerUser(User user) {
        user.setPassword(HashService.md5(user.getPassword()));
        if (userRepository.findUserByIdentifier(user.getIdentifier()) != null) {
            return false;
        } else {
            userRepository.save(user);
            return true;
        }
    }

    public void resetEnteringTries(String ipAddress) {
        userEnteringTries.remove(ipAddress);
    }

    public EnteringResult enterSuccessful(String ipAddress, String identifier, String password) {
        User user = findUserByIdentifier(identifier);
        Integer tries = userEnteringTries.get(ipAddress);
        if (tries == null) {
            tries = 0;
        }
        if (user == null) {
            return triesChecker(ipAddress,tries);
        }

        boolean isEnteringSuccessful = HashService.md5(password).equals(user.getPassword());
        if (isEnteringSuccessful) {
            resetEnteringTries(ipAddress);
            return EnteringResult.SUCCESS;
        } else {
            return triesChecker(ipAddress, tries);
        }
    }

    private EnteringResult triesChecker(String ipAddress, Integer tries) {
        tries += 1;
        if (tries == 3) {
            return EnteringResult.ATTEMPTS_OVER;
        }

        userEnteringTries.put(ipAddress, tries);
        return EnteringResult.FAILED;
    }
}
