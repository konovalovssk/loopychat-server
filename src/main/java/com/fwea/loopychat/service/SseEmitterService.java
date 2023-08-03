package com.fwea.loopychat.service;

import com.fwea.loopychat.entity.User;
import com.fwea.loopychat.event.JoinedToChannelEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.context.ApplicationEventPublisher;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RequiredArgsConstructor
@Service
public class SseEmitterService {
    private final ApplicationEventPublisher publisher;
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SseEmitter add(User user) {
        var emitter = new SseEmitter(Long.MAX_VALUE);
        //var emitter = new SseEmitter(50000L);
        emitter.onCompletion(() -> {
            log.info("SseEmitterService - onCompletion");
            //this.emitters.remove(emitter);
        });
        emitter.onError((error) -> {
            log.info("SseEmitterService - onError {}", error.toString());
            //emitter.completeWithError(error);
            //this.emitters.remove(emitter);
        });
        emitter.onTimeout(() -> {
            log.info("SseEmitterService - onTimeout");
            //emitter.complete();
            //this.emitters.remove(emitter);
        });

        this.emitters.add(emitter);
        log.info("SseEmitterService - emitters: " + emitters.size());

        publisher.publishEvent(new JoinedToChannelEvent(this, user));
        return emitter;
    }

    public void send(SseEvent event) {
        List<SseEmitter> failedEmitters = new ArrayList<>();

        log.info("SseEmitterService - send to emitters: " + emitters.size());
        this.emitters.forEach(emitter -> {
            SseEmitter.SseEventBuilder eventBuilder = SseEmitter.event()
                    .id(String.valueOf(event.getId()))
                    .name(event.getName())
                    .data(event.getPayload());

            try {
                emitter.send(eventBuilder);
            } catch (Exception e) {
                //emitter.completeWithError(e);
                failedEmitters.add(emitter);
                log.info("SseEmitterService - exception: " + e.getMessage());
            }
        });

        log.info("SseEmitterService - failed emitters: " + failedEmitters.size());
        this.emitters.removeAll(failedEmitters);
    }

    public interface SseEvent {
        Integer getId();
        String getName();
        HashMap<String, String> getPayload();
    }
}
