package com.example.regapp.controller;

import com.example.regapp.controller.request.ActivateLicenseRequest;
import com.example.regapp.service.LicenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/license")
@RequiredArgsConstructor
public class LicenseController {

    private final LicenseService licenseService;

    @GetMapping(value = "/form")
    public String showLicenseForm(Model model) {
        return "licenseForm";
    }

    @PostMapping(consumes = "application/x-www-form-urlencoded;charset=UTF-8")
    public String activateLicense(@ModelAttribute ActivateLicenseRequest request, Model model) {
        if (!licenseService.saveLicense(request.getLicenseNumber())) {
            model.addAttribute("errorMessage", "Недействительная лицензия");
            return "licenseForm";
        }

        return "enterForm";
    }
}

