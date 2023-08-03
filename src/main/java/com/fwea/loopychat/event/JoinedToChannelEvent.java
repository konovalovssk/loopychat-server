package com.fwea.loopychat.event;

import com.fwea.loopychat.entity.User;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.EventListener;

public class JoinedToChannelEvent extends ApplicationEvent {
    private final User user;

    public JoinedToChannelEvent(Object source, User user) {
        super(source);
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
