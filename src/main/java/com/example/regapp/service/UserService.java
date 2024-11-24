package com.example.regapp.service;

import com.example.regapp.entity.File;
import com.example.regapp.entity.Permission;
import com.example.regapp.entity.Privilege;
import com.example.regapp.entity.User;
import com.example.regapp.repository.FileRepository;
import com.example.regapp.repository.PriviledgeRepository;
import com.example.regapp.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class UserService {
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PriviledgeRepository privilegeRepository;

    private User currentUser;

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

    public boolean hasCopyPermissions(String fileName) {
        if (isAdmin()) {return true; }
        Permission permission = getPermission(fileName);
        return permission == Permission.C || permission == Permission.CE;
    }


    private Permission getPermission(String fileName) {
        Privilege privilege = privilegeRepository.findPriviledgeByFileNameAndUserIdentifier(fileName, currentUser.getIdentifier());
        return Permission.valueOf(privilege.getPermission());
    }

    public boolean hasEditPermissions(String fileName) {
        if (isAdmin()) {return true; }

        Permission permission = getPermission(fileName);
        return permission == Permission.E || permission == Permission.CE;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public boolean registerUser(User user) {
        // Хешируем пароль и сохраняем соль
        String[] hashedPasswordWithSalt = HashService.hashPassword(user.getPassword());
        user.setPassword(hashedPasswordWithSalt[1]); // Сохраняем только хеш
        user.setSalt(hashedPasswordWithSalt[0]); // Сохраняем соль

        if (userRepository.findUserByIdentifier(user.getIdentifier()) != null) {
            return false;
        } else {
            userRepository.save(user);
            saveNewPrivileges(user.getIdentifier());
            this.currentUser = user;
            return true;
        }
    }

    private void saveNewPrivileges(String identifier) {
        for (File file: fileRepository.findAll()) {
            Privilege privilege = new Privilege();
            privilege.setPermission("C");
            privilege.setUserIdentifier(identifier);
            privilege.setFileName(file.getFileName());
            privilegeRepository.save(privilege);
        }
    }

    public void savePrivileges(String fileName) {
        for (User user: userRepository.findAll()) {
            Privilege privilege = new Privilege();
            privilege.setPermission("C");
            privilege.setUserIdentifier(user.getIdentifier());
            privilege.setFileName(fileName);
            privilegeRepository.save(privilege);
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
            return triesChecker(ipAddress, tries);
        }

        String salt = user.getSalt();
        String storedHash = user.getPassword();

        // Хешируем введённый пароль с использованием соли
        String[] hashedInput = HashService.hashPasswordWithSalt(password, salt);
        boolean isEnteringSuccessful = hashedInput[1].equals(storedHash);

        if (isEnteringSuccessful) {
            resetEnteringTries(ipAddress);
            this.currentUser = user;
            return EnteringResult.SUCCESS;
        } else {
            return triesChecker(ipAddress, tries);
        }
    }

    public boolean isAdmin() {
        String role = currentUser.getRole();
        return role != null && role.equals("ADMIN");
    }

    private EnteringResult triesChecker(String ipAddress, Integer tries) {
        tries += 1;
        if (tries == 3) {
            userEnteringTries.put(ipAddress, 0);
            return EnteringResult.ATTEMPTS_OVER;
        }

        userEnteringTries.put(ipAddress, tries);
        return EnteringResult.FAILED;
    }
}
