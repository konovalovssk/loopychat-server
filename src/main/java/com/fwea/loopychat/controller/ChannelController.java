package com.fwea.loopychat.controller;

import com.fwea.loopychat.entity.Message;
import com.fwea.loopychat.entity.User;
import com.fwea.loopychat.form.MessageForm;
import com.fwea.loopychat.service.ChannelService;
import com.fwea.loopychat.service.SseEmitterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/api/channel")
@RequiredArgsConstructor
@DependsOn("securityFilterChain")
public class ChannelController {
    private final ChannelService channelService;
    private final SseEmitterService emitterService;

    @GetMapping(path="/sse", produces= MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter sse(final HttpServletResponse response, HttpServletRequest request) {
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:3000");
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");

        var auth = (Authentication) request.getUserPrincipal();
        var user = (User) auth.getPrincipal();
        return emitterService.add(user);
    }

    @PostMapping(path="/send")
    public void send(@Valid @RequestBody MessageForm form, HttpServletRequest request) {
        var user = (User)((Authentication) request.getUserPrincipal()).getPrincipal();
        var message = new Message();
        message.setBody(form.getBody());
        message.setCreatedAt(new Date());
        message.setUsername(user.getUsername());
        channelService.sendMessage(message);
    }

/*
    public record JoinResponse(CurrentChannel channel, List<CurrentUser> userList) {}
    public record CurrentUser(Integer id, String username) {}
    public record CurrentChannel(String name) {}
*/
}
