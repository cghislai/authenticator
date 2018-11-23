package com.charlyghislain.authenticator.example.app.rest;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.glassfish.jersey.media.sse.SseBroadcaster;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@ApplicationScoped
public class ServerEventListeners {

    @NonNull
    private List<SseBroadcaster> listeners = new CopyOnWriteArrayList<>();

    public void addListener(SseBroadcaster sseBroadcaster) {
        this.listeners.add(sseBroadcaster);
    }

    public void removeListener(SseBroadcaster sseBroadcaster) {
        this.listeners.remove(sseBroadcaster);
    }

    @NonNull
    public List<SseBroadcaster> getListeners() {
        return new ArrayList<>(listeners);
    }

}
