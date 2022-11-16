package org.mjbot.client.kucoin.dto.ws.response;

import java.util.Map;
import org.apache.commons.collections4.map.HashedMap;

public class BaseWsResponseDTO {

    private String type;
    private String topic;
    private String subject;
    private Map<String, Object> data = new HashedMap<>();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
