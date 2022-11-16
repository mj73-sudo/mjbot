package org.mjbot.client.kucoin.builder.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.ParameterizedType;
import org.mjbot.client.kucoin.builder.ObjectMapperBuilder;

public interface OnMessageListener {
    void getMessage(String text) throws JsonProcessingException;
}
