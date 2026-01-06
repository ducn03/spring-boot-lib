package com.springboot.notification.service.notify.channels;

import com.springboot.lib.helper.JsonHelper;
import com.springboot.notification.service.notify.MessageSender;
import com.springboot.notification.service.notify.dto.NotifyRequest;
import com.springboot.notification.service.notify.properties.ZnsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class ZaloSender implements MessageSender {
    private final ZnsProperties znsProperties;
    private static final HttpClient client = HttpClient.newHttpClient();

    public ZaloSender(ZnsProperties znsProperties) {
        this.znsProperties = znsProperties;
    }

    @Override
    public boolean send(NotifyRequest notifyRequest) {
        Map<String, Object> payload = znsProperties.buildPayload(notifyRequest);
        sendRequest(znsProperties.getZnsEndpoint(), payload);
        return false;
    }

    @Override
    public long getBalance() {
        return 0;
    }

    private void sendRequest(String endpoint, Map<String, Object> payload) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(endpoint))
                .header("Content-Type", "application/json")
                .header("Authorization", "Basic " + znsProperties.getAuth())
                .POST(HttpRequest.BodyPublishers.ofString(Objects.requireNonNull(JsonHelper.toJson(payload))))
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            znsProperties.handleApiResponse(response);
        } catch (IOException | InterruptedException ioException) {

        }
    }
}
