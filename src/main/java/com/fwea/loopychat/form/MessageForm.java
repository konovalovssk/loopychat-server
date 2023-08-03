package com.fwea.loopychat.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MessageForm {
    @NotBlank
    private String body;
}