package org.mjbot;

import static org.mjbot.client.kucoin.builder.ws.KucoinWsBuilder.wsEndpointUrl;
import static org.mjbot.client.kucoin.builder.ws.KucoinWsBuilder.wsPublicToken;

import java.io.*;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import javax.annotation.PostConstruct;
import javax.websocket.*;
import org.apache.commons.lang3.StringUtils;
import org.mjbot.client.kucoin.builder.ws.WebSocketListener;
import org.mjbot.client.kucoin.dto.ws.request.WsRequestDTO;
import org.mjbot.config.ApplicationProperties;
import org.mjbot.config.CRLFLogConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.Environment;
import tech.jhipster.config.DefaultProfileUtil;
import tech.jhipster.config.JHipsterConstants;

@SpringBootApplication
@EnableConfigurationProperties({ LiquibaseProperties.class, ApplicationProperties.class })
public class MjbotApp {

    private static final Logger log = LoggerFactory.getLogger(MjbotApp.class);

    private final Environment env;

    public MjbotApp(Environment env) {
        this.env = env;
    }

    /**
     * Initializes mjbot.
     * <p>
     * Spring profiles can be configured with a program argument --spring.profiles.active=your-active-profile
     * <p>
     * You can find more information on how profiles work with JHipster on <a href="https://www.jhipster.tech/profiles/">https://www.jhipster.tech/profiles/</a>.
     */
    @PostConstruct
    public void initApplication() {
        Collection<String> activeProfiles = Arrays.asList(env.getActiveProfiles());
        if (
            activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT) &&
            activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_PRODUCTION)
        ) {
            log.error(
                "You have misconfigured your application! It should not run " + "with both the 'dev' and 'prod' profiles at the same time."
            );
        }
        if (
            activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT) &&
            activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_CLOUD)
        ) {
            log.error(
                "You have misconfigured your application! It should not " + "run with both the 'dev' and 'cloud' profiles at the same time."
            );
        }
    }

    /**
     * Main method, used to run the application.
     *
     * @param args the command line arguments.
     */
    public static void main(String[] args) throws DeploymentException, IOException {
        SpringApplication app = new SpringApplication(MjbotApp.class);
        DefaultProfileUtil.addDefaultProfile(app);
        Environment env = app.run(args).getEnvironment();
        logApplicationStartup(env);

        String hostName = "wss://ws-api-spot.kucoin.com" + "?token=" + wsPublicToken;
        hostName = hostName.replace("endpoint", "");
        WsRequestDTO wsRequestDTO = new WsRequestDTO();
        wsRequestDTO.setId(Instant.now().getEpochSecond());
        wsRequestDTO.setPrivateChannel(false);
        wsRequestDTO.setResponse(true);
        wsRequestDTO.setType("subscribe");
        wsRequestDTO.setTopic("/market/candles:BTC-USDT_1min");
        WebSocket ws = HttpClient
            .newHttpClient()
            .newWebSocketBuilder()
            .buildAsync(URI.create(hostName), new WebSocketListener(hostName, wsRequestDTO))
            .join();
        while (true) {}
        /* String hostname =
            "wss://ws-api-spot.kucoin.com/?token=2neAiuYvAU61ZDXANAGAsiL4-iAExhsBXZxftpOeh_55i3Ysy2q2LEsEWU64mdzUOPusi34M_wGoSf7iNyEWJxFBnUVnEOanp8XFhYKwF9yiWEZy82HSutiYB9J6i9GjsxUuhPw3BlrzazF6ghq4L0TDqeEUUpGdSGAa2YJm0sE=.cNnxQ85QmyyRwoc8N1Aakg==";
        WebSocket ws = HttpClient
            .newHttpClient()
            .newWebSocketBuilder()
            .buildAsync(URI.create(hostname), new WebSocketClient(hostname))
            .join();
        while (true) {}*/
    }

    private static class WebSocketClient implements WebSocket.Listener {

        private String url;

        private WebSocketClient(String url) {
            this.url = url;
        }

        @Override
        public void onOpen(WebSocket webSocket) {
            System.out.println("onOpen using subprotocol " + webSocket.getSubprotocol());
            WebSocket.Listener.super.onOpen(webSocket);
            String json =
                "{\n" +
                "    \"id\": 1545910660739,           \n" +
                "    \"type\": \"subscribe\",\n" +
                "    \"topic\": \"/market/candles:BTC-USDT_1min\", \n" +
                "    \"privateChannel\": false,                      \n" +
                "    \"response\": true                         \n" +
                "}";
            webSocket.sendText(json, true);
        }

        @Override
        public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
            System.out.println("onText received " + data);
            return WebSocket.Listener.super.onText(webSocket, data, last);
        }

        @Override
        public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
            System.out.println(reason);
            HttpClient.newHttpClient().newWebSocketBuilder().buildAsync(URI.create(url), this).join();
            return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
        }

        @Override
        public void onError(WebSocket webSocket, Throwable error) {
            System.out.println(error.getMessage());
            WebSocket.Listener.super.onError(webSocket, error);
        }
    }

    private static void logApplicationStartup(Environment env) {
        String protocol = Optional.ofNullable(env.getProperty("server.ssl.key-store")).map(key -> "https").orElse("http");
        String serverPort = env.getProperty("server.port");
        String contextPath = Optional
            .ofNullable(env.getProperty("server.servlet.context-path"))
            .filter(StringUtils::isNotBlank)
            .orElse("/");
        String hostAddress = "localhost";
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.warn("The host name could not be determined, using `localhost` as fallback");
        }
        log.info(
            CRLFLogConverter.CRLF_SAFE_MARKER,
            "\n----------------------------------------------------------\n\t" +
            "Application '{}' is running! Access URLs:\n\t" +
            "Local: \t\t{}://localhost:{}{}\n\t" +
            "External: \t{}://{}:{}{}\n\t" +
            "Profile(s): \t{}\n----------------------------------------------------------",
            env.getProperty("spring.application.name"),
            protocol,
            serverPort,
            contextPath,
            protocol,
            hostAddress,
            serverPort,
            contextPath,
            env.getActiveProfiles().length == 0 ? env.getDefaultProfiles() : env.getActiveProfiles()
        );
    }
}
