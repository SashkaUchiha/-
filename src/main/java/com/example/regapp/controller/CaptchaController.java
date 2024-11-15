package com.example.regapp.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

@Controller
public class CaptchaController {
    private String captchaText;

    @GetMapping("/captchaForm")
    public String showForm() {
        return "captchaForm";
    }

    @GetMapping("/captcha")
    @ResponseBody
    public byte[] getCaptcha(HttpSession session) throws IOException {
        captchaText = generateCaptchaText();
        BufferedImage bufferedImage = new BufferedImage(100, 50, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, 100, 50);
        g2d.setColor(Color.BLACK);
        g2d.drawString(captchaText, 20, 30);
        g2d.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos);
        return baos.toByteArray();
    }

    @PostMapping("/submit")
    public String submitForm(Model model, @RequestParam String captcha, HttpSession session) {
        if (captcha.equals(captchaText)) {
            return "enterForm";
        } else {
            model.addAttribute("errorMessage", "Вы ввели неверное значение");
            return "captchaForm";
        }
    }

    private String generateCaptchaText() {
        Random random = new Random();
        return String.valueOf(random.nextInt(100000));
    }
}
