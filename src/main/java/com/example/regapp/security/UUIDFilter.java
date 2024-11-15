package com.example.regapp.security;

import com.example.regapp.entity.License;
import com.example.regapp.repository.LicenseRepository;
import com.example.regapp.service.UUIDService;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class UUIDFilter implements Filter {
    private final LicenseRepository licenseRepository;

    // Каждый запрос, поступающий на сервер, проходит проверку на наличие в базы пары UUID - LICENSE KEY
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String uuid = UUIDService.getMotherBoardUUID();
        License license = licenseRepository.findByActivatedUuid(uuid);

        if (request.getRequestURI().equals("/license/form") || request.getRequestURI().equals("/license")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (license == null) {
            // Если лицензии нет - редирект на страницу с вводом лицензии
            log.info("Cannot find a license for [{}]", uuid);
            response.sendRedirect("/license/form");
        } else {
            // Если лицензия корректная - запрос проходит
            log.info("License is valid");
            filterChain.doFilter(request, response);
        }
    }
}
