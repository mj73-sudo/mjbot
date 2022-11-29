package org.mjbot.client.kucoin.builder.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.mjbot.client.kucoin.dto.rest.request.KlineRequestDTO;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class KucoinRestBuilder {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public KucoinRestBuilder(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public List<List<String>> getKline(KlineRequestDTO body, Map<String, String> h) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();

        h.forEach(headers::add);
        headers.add("User-agent", "mj bot 0.1");
        HttpEntity<JsonNode> entity = new HttpEntity<>(headers);
        //        String url = "https://api.kucoin.com/api/v1/market/candles?type=1min&symbol=BTC-USDT&startAt=1566703297&endAt=1566789757";
        String url =
            "https://api.kucoin.com/api/v1/market/candles?type=" +
            body.getType() +
            "&symbol=" +
            body.getSymbol() +
            "&startAt=" +
            body.getStartAt() +
            "&endAt=" +
            body.getEndAt();
        ResponseEntity<JsonNode> exchange = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);
        if (exchange.getStatusCodeValue() == 200) {
            List<List<String>> response = objectMapper.readValue(
                exchange.getBody().get("data").toString(),
                new TypeReference<List<List<String>>>() {}
            );

            return response;
        }
        return null;
    }
}
