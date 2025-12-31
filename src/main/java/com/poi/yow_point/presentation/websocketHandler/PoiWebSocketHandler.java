package com.poi.yow_point.presentation.websocketHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poi.yow_point.application.services.websocket.PoiEventPublisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
//import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class PoiWebSocketHandler implements WebSocketHandler {

    private final PoiEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        log.info("WebSocket session started: {}", session.getId());

        return session.send(eventPublisher.getPublisher()
                .flatMap(poiEvent -> {
                    try {
                        String json = objectMapper.writeValueAsString(poiEvent);
                        return Mono.just(session.textMessage(json));
                    } catch (JsonProcessingException e) {
                        log.error("Error serializing POI event", e);
                        return Mono.empty();
                    }
                }))
                .doOnTerminate(() -> log.info("WebSocket session terminated: {}", session.getId()));
    }
}