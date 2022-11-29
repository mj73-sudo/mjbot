package org.mjbot.client.kucoin.dto.rest.request;

public class KlineRequestDTO {

    private String type;
    private String symbol;
    private long startAt;
    private long endAt;

    public String getType() {
        return type;
    }

    public KlineRequestDTO setType(String type) {
        this.type = type;
        return this;
    }

    public String getSymbol() {
        return symbol;
    }

    public KlineRequestDTO setSymbol(String symbol) {
        this.symbol = symbol;
        return this;
    }

    public long getStartAt() {
        return startAt;
    }

    public KlineRequestDTO setStartAt(long startAt) {
        this.startAt = startAt;
        return this;
    }

    public long getEndAt() {
        return endAt;
    }

    public KlineRequestDTO setEndAt(long endAt) {
        this.endAt = endAt;
        return this;
    }
}
