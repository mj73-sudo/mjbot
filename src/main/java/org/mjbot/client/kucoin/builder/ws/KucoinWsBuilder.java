package org.mjbot.client.kucoin.builder.ws;

import com.kucoin.sdk.model.InstanceServer;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.time.Instant;
import java.util.List;
import javax.annotation.PostConstruct;
import org.mjbot.client.kucoin.dto.rest.common.GetWsPublicTokenDTO;
import org.mjbot.client.kucoin.dto.ws.request.WsRequestDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

//@Configuration
public class KucoinWsBuilder {

    public static String wsEndpointUrl;
    public static String wsPublicToken;
    private final RestTemplate restTemplate;

    @Value("${kucoin.base-url}")
    private String baseUrl;

    @Value("${kucoin.socket-endpoint-url}")
    private String socketEndpointUrl;

    @Value("${kucoin.public-token-endpoint-url}")
    private String publicTokenEndpointUrl;

    public KucoinWsBuilder(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public static void setWsEndpointUrl(String wsEndpointUrl) {
        KucoinWsBuilder.wsEndpointUrl = wsEndpointUrl;
    }

    public static void setWsPublicToken(String wsPublicToken) {
        KucoinWsBuilder.wsPublicToken = wsPublicToken;
    }

    @PostConstruct
    public void init() {
        GetWsPublicTokenDTO publicWsConfig = getPublicWsConfig();
        setWsEndpointUrl(publicWsConfig.getData().getInstanceServers().get(0).getEndpoint());
        setWsPublicToken(publicWsConfig.getData().getToken());
    }

    private GetWsPublicTokenDTO getPublicWsConfig() {
        String requestUrl = baseUrl + publicTokenEndpointUrl;

        InstanceServer instanceServer = new InstanceServer();
        instanceServer.setEncrypt(true);
        instanceServer.setEndpoint(socketEndpointUrl);
        instanceServer.setProtocol("websocket");
        instanceServer.setPingInterval(50000);
        instanceServer.setPingTimeout(10000);

        GetWsPublicTokenDTO.DataItem dataItem = new GetWsPublicTokenDTO.DataItem();
        dataItem.setInstanceServers(List.of(instanceServer));

        GetWsPublicTokenDTO requestBody = new GetWsPublicTokenDTO();
        requestBody.setCode("200000");
        requestBody.setData(dataItem);

        ResponseEntity<GetWsPublicTokenDTO> response = restTemplate.postForEntity(requestUrl, requestBody, GetWsPublicTokenDTO.class);

        return response.getBody();
    }
}
