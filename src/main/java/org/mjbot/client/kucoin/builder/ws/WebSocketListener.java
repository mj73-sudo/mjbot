package org.mjbot.client.kucoin.builder.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletionStage;
import org.mjbot.client.kucoin.builder.ObjectMapperBuilder;
import org.mjbot.client.kucoin.dto.ws.request.WsRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebSocketListener implements WebSocket.Listener {

    private final Logger log = LoggerFactory.getLogger(WebSocketListener.class);
    private final String url;
    private final WsRequestDTO request;

    public WebSocketListener(String url, WsRequestDTO request) {
        this.url = url;
        this.request = request;
    }

    @Override
    public void onOpen(WebSocket webSocket) {
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
        return WebSocket.Listener.super.onText(webSocket, data, last);
    }

    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
        HttpClient.newHttpClient().newWebSocketBuilder().buildAsync(URI.create(url), this).join();
        return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        WebSocket.Listener.super.onError(webSocket, error);
    }
}
