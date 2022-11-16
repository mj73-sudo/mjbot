package org.mjbot.client.kucoin.dto.rest.common;

import com.kucoin.sdk.model.InstanceServer;
import java.util.ArrayList;
import java.util.List;

public class GetWsPublicTokenDTO {

    private String code;
    private DataItem data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public DataItem getData() {
        return data;
    }

    public void setData(DataItem data) {
        this.data = data;
    }

    public static class DataItem {

        private String token;
        private List<InstanceServer> instanceServers = new ArrayList<>();

        public List<InstanceServer> getInstanceServers() {
            return instanceServers;
        }

        public void setInstanceServers(List<InstanceServer> instanceServers) {
            this.instanceServers = instanceServers;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
