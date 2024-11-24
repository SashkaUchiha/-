package com.example.regapp.controller;

import com.example.regapp.entity.File;
import com.example.regapp.entity.User;
import com.example.regapp.entity.forms.UserEnterForm;
import com.example.regapp.entity.forms.UserRegistrationForm;
import com.example.regapp.service.EnteringResult;
import com.example.regapp.service.FileService;
import com.example.regapp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    @Autowired
    private final UserService userService;
    @Autowired
    private FileService fileService;

    @GetMapping("/registerForm")
    public String getRegisterForm() {
        return "registrationForm";
    }

    @GetMapping("/register")
    public String registerUser(@ModelAttribute UserRegistrationForm userForm, Model model) {
        User user = new User();
        user.setIdentifier(userForm.getIdentifier());
        user.setPassword(userForm.getPassword());
        user.setFullName(userForm.getFullName());
        user.setPhoneNumber(userForm.getPhoneNumber());
        user.setEmail(userForm.getEmail());
        user.setAddress(userForm.getAddress());

        boolean registrationSuccess = userService.registerUser(user);
        if (!registrationSuccess) {
            model.addAttribute("errorMessage", "Такой пользователь уже существует");
            return "registrationForm";
        }
        model.addAttribute("message", "Вы успешно зарегистрированы!");
        return showFiles(model);
    }

    @GetMapping("/enterForm")
    public String showEnterForm(Model model) {
        return "enterForm";
    }

    @GetMapping("/enter")
    public String userEnter(@ModelAttribute UserEnterForm userEnterForm, Model model, HttpServletRequest request, Authentication authentication) {
        String ipAddress = request.getRemoteAddr();
        String password = userEnterForm.getPassword();
        String identifier = userEnterForm.getIdentifier();
        EnteringResult enterSuccess = userService.enterSuccessful(ipAddress, identifier, password);

        if (enterSuccess == EnteringResult.FAILED) {
            model.addAttribute("errorMessage", "Неверный идентификатор или пароль");
            return "enterForm";
        }

        if (enterSuccess == EnteringResult.ATTEMPTS_OVER) {
            return "captchaForm";
        }

        if (userService.isAdmin()) {
            return "adminForm";
        }
        model.addAttribute("message", "Вы успешно авторизовались");
        return showFiles(model);
    }

    @PostMapping("/showFiles")
    public String showFiles(Model model) {
        model.addAttribute("files", fileService.getAllFiles());
        return "selectFile";
    }

    @PostMapping("/newFile")
    public String createFile(Model model, @ModelAttribute File file) {
        if(userService.isAdmin()) {
            fileService.saveFile(file);
            return showFiles(model);
        }
        model.addAttribute("errorMessage", "Вы должны обладать правами администратора");
        return "createFile";
    }

    @PostMapping("/showCreateFileForm")
    public String showCreateFileForm(Model model) {
        return "createFile";
    }

    @PostMapping("/saveFileChanges")
    public String saveFileChanges(Model model, @RequestParam("content") String content, @RequestParam("fileName") String fileName) {
        File file = fileService.getFileByName(fileName);
        if (userService.hasEditPermissions(fileName)) {
            File editedFile = fileService.editFile(content, fileName);
            model.addAttribute("message", "Изменения успешно сохранены");
            model.addAttribute("file", file);
            return "fileEdit";
        }
        model.addAttribute("errorMessage", "Вы не обладаете правами изменения файла");
        model.addAttribute("file", file);
        return "fileEdit";
    }

    @PostMapping("showAdminForm")
    public String showAdminForm(Model model) {
        if(userService.isAdmin()) {
            return "adminForm";
        }
        model.addAttribute("errorMessage", "Вы не являетесь администратором");
        return showFiles(model);
    }

    @GetMapping("/open")
    public String openFile(@RequestParam("selectedFile") String selectedFile, Model model) {
        File file = fileService.openFile(selectedFile);
        model.addAttribute("file", file);
        return "fileEdit";
    }

    @PostMapping("/checkCopyPermissions")
    @ResponseBody
    public ResponseEntity<Map<String, Boolean>> checkCopyPermissions(@RequestBody Map<String, String> request) {
        String fileName = request.get("fileName");
        boolean hasPermission = userService.hasCopyPermissions(fileName);

        Map<String, Boolean> response = new HashMap<>();
        response.put("hasPermission", hasPermission);

        return ResponseEntity.ok(response);
    }
}
