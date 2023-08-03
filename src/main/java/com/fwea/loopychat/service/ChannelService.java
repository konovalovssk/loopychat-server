package com.fwea.loopychat.service;

import com.fwea.loopychat.entity.Message;
import com.fwea.loopychat.event.JoinedToChannelEvent;
import com.fwea.loopychat.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChannelService {
    private final MessageRepository messageRepository;
    private final MessageSender messageSender;

    public void sendMessage(Message message) {
        log.info("[sendMessage] message: {}", message);
        var saved = messageRepository.save(message);
        log.info("[sendMessage] result: {}", saved);
        messageSender.send(saved);
    }

    @EventListener
    public void listenEvent(JoinedToChannelEvent event) {
        log.info("Event received c: {}, t: {}", event.getUser(), event.getTimestamp());

        var message = new Message();
        message.setBody(event.getUser().getUsername() + " joined to channel!! You're awesome ;)");
        message.setCreatedAt(new Date());
        message.setUsername("system");

        sendMessage(message);
    }




}
