package com.fwea.loopychat.controller;

import com.fwea.loopychat.AppException;
import com.fwea.loopychat.form.LoginForm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @PostMapping("/login")
    public ResponseEntity login(@Valid @RequestBody LoginForm form, BindingResult bindingResult,
                                HttpServletRequest request, HttpServletResponse response) {
        if (request.getUserPrincipal() != null) {
            throw new AppException("Please logout first.");
        }



    }

}
