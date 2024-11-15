package com.example.regapp.controller;

import com.example.regapp.entity.User;
import com.example.regapp.entity.UserEnterForm;
import com.example.regapp.entity.UserRegistrationForm;
import com.example.regapp.service.EnteringResult;
import com.example.regapp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

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

        return "registrationSuccess";
    }

    @GetMapping("/enterForm")
    public String showEnterForm(Model model) {
        return "enterForm";
    }

    @GetMapping("/enter")
    public String userEnter(@ModelAttribute UserEnterForm userEnterForm, Model model, HttpServletRequest request) {
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

        return "enterSuccess";
    }
}
