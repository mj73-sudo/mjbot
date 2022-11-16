package org.mjbot.client.kucoin.builder;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperBuilder {

    private static ObjectMapper objectMapper = null;

    private ObjectMapperBuilder() {}

    public static ObjectMapper getInstance() {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
        }
        return objectMapper;
    }
}
