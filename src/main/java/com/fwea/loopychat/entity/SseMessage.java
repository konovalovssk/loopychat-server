package com.fwea.loopychat.entity;

import com.fwea.loopychat.service.SseEmitterService;
import lombok.Data;
import java.util.HashMap;

@Data
public class SseMessage implements SseEmitterService.SseEvent {
    private Integer id;
    private String name;
    private HashMap<String, String> payload;
}
