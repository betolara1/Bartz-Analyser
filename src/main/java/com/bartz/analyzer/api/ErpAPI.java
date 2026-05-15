package com.bartz.analyzer.api;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.layout.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import java.util.function.*;

public class ErpAPI {
    // BUSCA INTEGRADA NO ERP (Para a lista de resultados)
    public static void buscaItensNoERP(String tipo, String codigo, String descricao, VBox container,
            Function<String, HBox> rowFactory, Node header) {
        String url;
        final String prefixo = "CHAPA".equals(tipo) ? "10.01."
                : "FITA".equals(tipo) ? "10.02." : "TAPAFURO".equals(tipo) ? "10.15." : "CAPA".equals(tipo) ? "10.03." : "";

        try {
            String queryCode = (codigo != null && !codigo.trim().isEmpty()) ? codigo.trim().toUpperCase() : "";
            String queryDesc = (descricao != null) ? descricao.trim().toUpperCase() : "";
            String[] searchTerms = queryDesc.split("\\s+");

            if (!queryCode.isEmpty()) {
                // Busca por CÓDIGO
                url = "http://192.168.1.10:8081/api/item/search-code?q="
                        + URLEncoder.encode(queryCode, StandardCharsets.UTF_8);
            } else if (!queryDesc.isEmpty()) {
                // Busca por DESCRIÇÃO (Usa o termo mais longo como no código antigo)
                String longestTerm = "";
                for (String s : searchTerms)
                    if (s.length() > longestTerm.length())
                        longestTerm = s;
                url = "http://192.168.1.10:8081/api/item/search-desc?q="
                        + URLEncoder.encode(longestTerm, StandardCharsets.UTF_8);
            } else {
                url = "http://192.168.1.10:8081/api/item";
            }

            System.out.println("Buscando no ERP: " + url);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("X-API-KEY", "bartznewmoveisapi")
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body)
                .thenAccept(json -> {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode root = mapper.readTree(json);

                        Platform.runLater(() -> {
                            if (root.isArray() && root.size() > 0) {
                                String finalDesc = (descricao != null) ? descricao.toUpperCase() : "";
                                String[] terms = finalDesc.split("\\s+");

                                for (JsonNode item : root) {
                                    String itemCode = item.path("codeItem").asText().toUpperCase();
                                    String itemDesc = item.path("description").asText().toUpperCase();

                                    // 1. FILTRO DE FORMATO (Exatamente 2 pontos: xx.xx.xxxx)
                                    long dotCount = itemCode.chars().filter(ch -> ch == '.').count();
                                    if (dotCount != 2)
                                        continue;

                                    // 2. FILTRO DE PREFIXO (Se selecionou categoria)
                                    if (!prefixo.isEmpty() && !itemCode.startsWith(prefixo))
                                        continue;

                                    // 3. FILTRO DE TERMOS (Todos os termos devem bater)
                                    boolean matchAll = true;
                                    for (String t : terms) {
                                        if (t.isEmpty())
                                            continue;
                                        if (!itemDesc.contains(t) && !itemCode.contains(t)) {
                                            matchAll = false;
                                            break;
                                        }
                                    }
                                    if (!matchAll && !finalDesc.isEmpty())
                                        continue;

                                    if (container.getChildren().isEmpty())
                                        container.getChildren().add(header);
                                    container.getChildren().add(rowFactory.apply(itemCode + ";" + itemDesc));
                                }
                            }

                            // Feedback final
                            if (container.getChildren().isEmpty()) {
                                Label lbl = new Label("NENHUM ITEM ENCONTRADO PARA ESTA BUSCA.");
                                lbl.setStyle(
                                        "-fx-text-fill: #E74C3C; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 20;");
                                container.getChildren().add(lbl);
                            }
                            container.setVisible(true);
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    // BUSCA SIGLAS PARA COMBOBOX (Ex: BRANCO -> BRANCO (BR))
    public static void buscaSiglasParaCombo(String query, ComboBox<String> combo) {
        if (query == null || query.trim().length() < 3) {
            Platform.runLater(() -> combo.getItems().clear());
            return;
        }

        String url;
        try {
            url = "http://192.168.1.10:8081/api/cor/search-descricao?q="
                    + URLEncoder.encode(query.trim().toUpperCase(), StandardCharsets.UTF_8);
            System.out.println("Buscando Cores no ERP: " + url);
        } catch (Exception e) {
            return;
        }

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).header("X-API-KEY", "bartznewmoveisapi").build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body)
                .thenAccept(json -> {
                    System.out.println("Resposta ERP (Cores): " + json);
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode root = mapper.readTree(json);
                        java.util.List<String> results = new java.util.ArrayList<>();

                        Consumer<JsonNode> processNode = item -> {
                            String sigla = item.has("siglaCor") ? item.path("siglaCor").asText()
                                    : item.has("sigla") ? item.path("sigla").asText()
                                            : item.has("codigo") ? item.path("codigo").asText() : "";
                            String desc = item.has("descricao") ? item.path("descricao").asText()
                                    : item.has("description") ? item.path("description").asText() : "";

                            if (!sigla.isEmpty() && !desc.isEmpty()) {
                                String itemDisplay = desc.trim().toUpperCase() + " (" + sigla.trim().toUpperCase() + ")";
                                if (!results.contains(itemDisplay)) {
                                    results.add(itemDisplay);
                                }
                            }
                        };

                        if (root.isArray()) {
                            for (JsonNode item : root) {
                                processNode.accept(item);
                            }
                        } else if (root.isObject()) {
                            processNode.accept(root);
                        }

                        Platform.runLater(() -> {
                            combo.setItems(FXCollections.observableArrayList(results));
                            if (!results.isEmpty()) {
                                combo.show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    // BUSCA NO CVS OS PAINEIS
    public static List<String> codigoPanel(String descricao) {
        String csvPath = "\\\\192.168.1.10\\Promob\\codigos_paineis.csv";
        Path path = Paths.get(csvPath);

        // O 'try-with-resources' garante que o arquivo seja fechado automaticamente
        try (Stream<String> lines = Files.lines(path)) {

            // Filtramos as linhas que contém o termo.
            // Pulamos a primeira linha (cabeçalho) com .skip(1)
            String busca = (descricao == null) ? "" : descricao.trim();

            return lines.skip(1)
                    .filter(line -> line.toLowerCase().contains(busca.toLowerCase()))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return List.of("ERRO AO LER CSV: " + e.getMessage());
        }

    }
}
