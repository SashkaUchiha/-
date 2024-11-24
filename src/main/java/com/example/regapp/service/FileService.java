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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class FileService {
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private PriviledgeRepository priviledgeRepository;
    public File openFile(String fileName) {
        return fileRepository.findByFileName(fileName);
    }

    public List<File> getAllFiles() {
        return fileRepository.findAll();
    }


    public void saveFile(File file) {
        File thisFile = getFileByName(file.getFileName());
        if (thisFile == null) {
            userService.savePrivileges(file.getFileName());
        }

        fileRepository.save(file);
        String fileName = file.getFileName();
        String content = file.getContent();

        try {
            java.io.File diskFile = new java.io.File("src/main/java/data/files/" + fileName);

            try (FileWriter writer = new FileWriter(diskFile)) {
                writer.write(content);
                log.info("File {} is created", fileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File editFile(String content, String fileName) {
        File file = fileRepository.findByFileName(fileName);
        file.setContent(content);
        saveFile(file);
        return file;
    }

    public File getFileByName(String fileName) {
        return fileRepository.findByFileName(fileName);
    }
}
