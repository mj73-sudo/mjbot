package org.mjbot.client.kucoin.dto.ws.request;

public class WsRequestDTO {

    private Long id;
    private String type;

    private String topic;
    private Boolean privateChannel;
    private Boolean response;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getPrivateChannel() {
        return privateChannel;
    }

    public void setPrivateChannel(Boolean privateChannel) {
        this.privateChannel = privateChannel;
    }

    public Boolean getResponse() {
        return response;
    }

    public void setResponse(Boolean response) {
        this.response = response;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
