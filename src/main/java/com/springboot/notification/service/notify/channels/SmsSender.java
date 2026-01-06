package com.springboot.notification.service.notify.channels;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.springboot.lib.helper.JsonHelper;
import com.springboot.notification.service.notify.MessageSender;
import com.springboot.notification.service.notify.dto.NotifyRequest;
import com.springboot.notification.service.notify.properties.SmsProperties;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Objects;

@Service
public class SmsSender implements MessageSender {
    private final SmsProperties smsProperties;
    private static final HttpClient client = HttpClient.newHttpClient();

    public SmsSender(SmsProperties smsProperties) {
        this.smsProperties = smsProperties;
    }

    @Override
    public boolean send(NotifyRequest notifyRequest) {
        Map<String, Object> payload = smsProperties.buildPayload(notifyRequest);
        sendRequest(smsProperties.getSmsEndpoint(), payload);
        return false;
    }

    @Override
    public long getBalance() {
        return 0;
    }

    private void sendRequest(String endpoint, Map<String, Object> payload) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(endpoint))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", "Basic " + smsProperties.getAuth())
                .POST(HttpRequest.BodyPublishers.ofString(Objects.requireNonNull(JsonHelper.toJson(payload))))
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            smsProperties.handleApiResponse(response);
        } catch (IOException | InterruptedException ioException) {

        }
    }
}
