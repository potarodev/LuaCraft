package com.luacraft.test;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestRunner {
    private static String PATH = "https://fill.papermc.io/v3/projects/paper/versions/1.21.4/builds/latest";
    private static Gson gson = new Gson();

    public static void main(String[] args) throws IOException, InterruptedException {
        Path destinationPath = Paths.get("build/test_environment/paperclip.jar");
        if (Files.exists(destinationPath)) {
            System.out.println("Paperclip already exists, skipping download.");
        } else {
            Files.createDirectories(destinationPath.getParent());
            downloadJar(URI.create(PATH), Paths.get("build/test_environment/paperclip.jar"));
        }
    }

    private static void downloadJar(URI source, Path destination) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest buildRequest = HttpRequest.newBuilder()
                .uri(source)
                .build();
        HttpResponse<InputStream> buildResponse = client.send(buildRequest, HttpResponse.BodyHandlers.ofInputStream());
        JsonObject buildObject;
        try (InputStreamReader reader = new InputStreamReader(buildResponse.body())) {
            buildObject = gson.fromJson(reader, JsonObject.class);
        }
        String downloadUrl = buildObject.get("downloads").getAsJsonObject()
                .get("server:default").getAsJsonObject()
                .get("url").getAsString();

        HttpRequest downloadRequest = HttpRequest.newBuilder()
                .uri(URI.create(downloadUrl))
                .build();
        HttpResponse<InputStream> downloadResponse = client.send(downloadRequest, HttpResponse.BodyHandlers.ofInputStream());
        try (InputStream inputStream = downloadResponse.body()) {
            Files.copy(inputStream, destination);
        }
    }
}
