package com.fwea.loopychat.form;

import com.fwea.loopychat.entity.User;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterForm {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @Enumerated(EnumType.ORDINAL)
    private User.Gender gender;
}