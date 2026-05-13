package com.bartz.analyzer.api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.kordamp.ikonli.javafx.FontIcon;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.control.*;

public class PedidosAPI {

    // Método que busca os comentarios dos pedidos DA API
    public static void retornaComentario(String numeroPedido, VBox apiContent, FontIcon waitIcon, Label placeholder) {
        String url = "http://192.168.1.10:8080/api_pedidos.php?num_pedido=" + numeroPedido;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body).thenAccept(json -> {
            try{
                ObjectMapper mapper = new ObjectMapper();

                //Lê o JSON 
                JsonNode root = mapper.readTree(json);
                StringBuilder textoCompleto = new StringBuilder();

                // Verifica se é uma lista e percorremos cada item
                if(root.isArray()){
                    for (JsonNode item : root) {
                        // 1. Lemos os valores com um "fallback" (se for nulo, vira vazio)
                        String titulo = item.path("txt_titulo").isMissingNode() || item.path("txt_titulo").isNull() 
                                        ? "" : item.path("txt_titulo").asText();
                                        
                        String comentario = item.path("txt_comentario").isMissingNode() || item.path("txt_comentario").isNull() 
                                            ? "" : item.path("txt_comentario").asText();

                        // 2. SÓ MONTA se o comentário não for vazio E não for a palavra "null"
                        if (!comentario.isEmpty() && !comentario.equalsIgnoreCase("null")) {
                            
                            // Se o título for "null" ou vazio, usamos um padrão ou deixamos sem título
                            String tituloFormatado = (titulo.isEmpty() || titulo.equalsIgnoreCase("null")) 
                                                    ? "COMENTÁRIO" : titulo.toUpperCase();

                            textoCompleto.append(tituloFormatado)
                                        .append(":\n")
                                        .append(comentario)
                                        .append("\n\n");
                        }
                    }
                }

                String resultadoFinal = textoCompleto.toString().trim();

                Platform.runLater(() -> {
                    if (waitIcon != null) {
                        waitIcon.setVisible(false);
                        waitIcon.setManaged(false);
                    }

                    if (placeholder != null) {
                        placeholder.setText(resultadoFinal.isEmpty() ? "Pedido sem comentário." : resultadoFinal);
                        placeholder.setStyle("-fx-text-fill: white; -fx-font-style: italic; -fx-font-size: 14px;");
                    }

                    if (apiContent != null) {
                        apiContent.setAlignment(Pos.CENTER_LEFT);
                        apiContent.setStyle(apiContent.getStyle() + "-fx-padding: 20;");
                    }
                });
            } 
            catch (Exception e) {
                Platform.runLater(() -> {
                    if (waitIcon != null) {
                        waitIcon.setVisible(false);
                        waitIcon.setManaged(false);
                    }

                    if (placeholder != null) {
                        placeholder.setText("Informações do pedido indisponíveis.");
                        placeholder.setStyle("-fx-text-fill: #E74C3C; -fx-font-style: italic; -fx-font-size: 14px;");
                    }
                });
            }
        });
    }
}
