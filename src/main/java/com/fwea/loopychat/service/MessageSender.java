package com.fwea.loopychat.service;

import com.fwea.loopychat.entity.Message;
import com.fwea.loopychat.entity.SseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

@RequiredArgsConstructor
@Service
public class MessageSender {
    private final SseEmitterService emitterService;

    public void send(Message message) {
        var payload = new HashMap<String, String>();
        payload.put("id",  message.getId().toString());
        payload.put("username",  message.getUsername());
        payload.put("body",  message.getBody());
        payload.put("createdAt",  formatDate(message.getCreatedAt()));

        var sse = new SseMessage();
        sse.setId(message.getId());
        sse.setName("chat");
        sse.setPayload(payload);

        emitterService.send(sse);
    }

    private String formatDate(Date date) {
        return (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")).format(date);
    }
}
