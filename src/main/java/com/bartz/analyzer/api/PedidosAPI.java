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
                JsonNode root = mapper.readTree(json);

                Platform.runLater(() -> {
                    if (apiContent != null) {
                        apiContent.getChildren().clear();
                        apiContent.setAlignment(Pos.CENTER_LEFT);
                        apiContent.setSpacing(15);
                    }

                    if (waitIcon != null) {
                        waitIcon.setVisible(false);
                        waitIcon.setManaged(false);
                    }

                    if (!root.isArray() || root.size() == 0) {
                        Label empty = new Label("PEDIDO SEM COMENTÁRIOS NO SERVIDOR.");
                        empty.setStyle("-fx-text-fill: #555555; -fx-font-size: 11px; -fx-font-weight: bold;");
                        if (apiContent != null) apiContent.getChildren().add(empty);
                        return;
                    }

                    for (JsonNode item : root) {
                        String titulo = item.path("txt_titulo").isMissingNode() || item.path("txt_titulo").isNull() 
                                        ? "" : item.path("txt_titulo").asText();
                        String comentario = item.path("txt_comentario").isMissingNode() || item.path("txt_comentario").isNull() 
                                            ? "" : item.path("txt_comentario").asText();

                        if (!comentario.isEmpty() && !comentario.equalsIgnoreCase("null")) {
                            VBox itemBox = new VBox(4);
                            String tituloFormatado = (titulo.isEmpty() || titulo.equalsIgnoreCase("null")) 
                                                    ? "COMENTÁRIO" : titulo.toUpperCase();

                            Label lblTitulo = new Label(tituloFormatado);
                            lblTitulo.setStyle("-fx-text-fill: #3498DB; -fx-font-size: 10px; -fx-font-weight: bold; -fx-letter-spacing: 1px;");

                            Label lblComentario = new Label(comentario);
                            lblComentario.setWrapText(true);
                            lblComentario.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");

                            itemBox.getChildren().addAll(lblTitulo, lblComentario);
                            if (apiContent != null) apiContent.getChildren().add(itemBox);
                        }
                    }
                });
            } 
            catch (Exception e) {
                Platform.runLater(() -> {
                    if (waitIcon != null) {
                        waitIcon.setVisible(false);
                        waitIcon.setManaged(false);
                    }
                    if (apiContent != null) {
                        apiContent.getChildren().clear();
                        Label err = new Label("INFORMAÇÕES DO PEDIDO INDISPONÍVEIS.");
                        err.setStyle("-fx-text-fill: #E74C3C; -fx-font-size: 11px; -fx-font-weight: bold;");
                        apiContent.getChildren().add(err);
                    }
                });
            }
        });
    }
}
