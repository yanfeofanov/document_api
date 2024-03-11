package org.document;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class CrptApi {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final Semaphore requestSemaphore;

    public CrptApi(HttpClient httpClient, ObjectMapper objectMapper, Semaphore requestSemaphore) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.requestSemaphore = requestSemaphore;
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> requestSemaphore.release(requestSemaphore.availablePermits()), 0, 1, TimeUnit.SECONDS);
    }

    public void createDocument(String apiKey, Document document, String signature) {
        try {
            requestSemaphore.acquire();
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("doc_id", document.getDoc_id());
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://ismp.crpt.ru/api/v3/lk/documents/create"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                System.out.println("Документ создан!");
            } else {
                System.err.println("Ошибка: " + response.statusCode() + ", " + response.body());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            requestSemaphore.release();
        }

    }

    }
