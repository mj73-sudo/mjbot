package org.mjbot.config;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Pong;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfluxDbConfiguration {

    @Value("${influxdb.address}")
    private String address;

    @Value("${influxdb.username}")
    private String username;

    @Value("${influxdb.password}")
    private String password;

    @Bean
    public InfluxDB getInfluxDB() {
        InfluxDB influxDB = null;
        username = null;
        password = null;
        if (username == null && password == null) {
            influxDB = InfluxDBFactory.connect(address);
        } else if (username != null && password != null) {
            influxDB = InfluxDBFactory.connect(address, username, password);
        }

        Pong ping = influxDB.ping();
        if (ping.getVersion().equalsIgnoreCase("unknown")) {
            return null;
        }
        return influxDB;
    }
}
