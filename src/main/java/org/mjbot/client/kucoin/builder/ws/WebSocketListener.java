package org.mjbot.client.kucoin.builder.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import org.mjbot.client.kucoin.builder.ObjectMapperBuilder;
import org.mjbot.client.kucoin.dto.ws.request.WsRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebSocketListener implements WebSocket.Listener {

    private final Logger log = LoggerFactory.getLogger(WebSocketListener.class);
    private final String url;
    private final WsRequestDTO request;

    private final OnMessageListener onMessageListener;

    public WebSocketListener(String url, WsRequestDTO request, OnMessageListener onMessageListener) {
        this.url = url;
        this.request = request;
        this.onMessageListener = onMessageListener;
    }

    @Override
    public void onOpen(WebSocket webSocket) {
        log.debug("-------------------on open {}--------------------", request.getTopic());
        WebSocket.Listener.super.onOpen(webSocket);
        ObjectMapper instance = ObjectMapperBuilder.getInstance();
        try {
            String json = instance.writeValueAsString(request);
            webSocket.sendText(json, true);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        if (data.toString().contains("welcome")) {
            String json = "{\"id:\":\"" + UUID.randomUUID().toString() + "\",\"type\":\"ping\"}";
            webSocket.sendText(json, true);

            log.debug("-------------------on ping {}--------------------", request.getTopic());
        }
        onMessage(data.toString());
        return WebSocket.Listener.super.onText(webSocket, data, last);
    }

    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
        log.error("-----------------------on close {}--------------------------------", request.getTopic());
        HttpClient.newHttpClient().newWebSocketBuilder().buildAsync(URI.create(url), this).join();
        return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        log.error("---------------------------on error {}--------------------------------", request.getTopic());
        HttpClient.newHttpClient().newWebSocketBuilder().buildAsync(URI.create(url), this).join();
        WebSocket.Listener.super.onError(webSocket, error);
    }

    private void onMessage(String text) {
        if (text.contains("message")) {
            try {
                onMessageListener.getMessage(text);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
