package com.bartz.analyzer.api;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List; // Importante!
import java.util.stream.Collectors; // Importante!
import java.util.stream.Stream;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import org.kordamp.ikonli.javafx.FontIcon;

public class ErpAPI {

    
    

    // BUSCA EM API DE CORES (Se type === 'CORINGA')
    String corCod = "http://192.168.1.10:8085/cores/search?codigo=";
    String corDesc = "http://192.168.1.10:8085/cores/search?descricao=";
    String cor = "http://192.168.1.10:8085/cores";

    // 2. BUSCA PELO ITEM NO ERP (CODIGO E/OU DESCRIÇÃO)
    String itemCod = "http://192.168.1.10:8085/itens/search?codigo=";
    String itemDesc = "http://192.168.1.10:8085/itens/search?descricao=";


    public static void retornaDescricao(String codigo, VBox apiContent, FontIcon waitIcon, Label placeholder){
        String url = "http://192.168.1.10:8085/itens/search?codigo=" + codigo;
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



    // BUSCA NO CVS OS PAINEIS
    public static List<String> codigoPanel(String descricao){
        String csvPath = "\\\\192.168.1.10\\Promob\\codigos_paineis.csv";
        Path path = Paths.get(csvPath);

        
        // O 'try-with-resources' garante que o arquivo seja fechado automaticamente
        try(Stream<String> lines = Files.lines(path)){

            // Filtramos as linhas que contém o termo. 
            // Pulamos a primeira linha (cabeçalho) com .skip(1)
            String busca = (descricao == null) ? "" : descricao.trim();

            return lines.skip(1)
                        .filter(line -> line.toLowerCase().contains(busca.toLowerCase()))
                        .collect(Collectors.toList());
        }
        catch(IOException e){
            return List.of("ERRO AO LER CSV: " + e.getMessage());
        }

    }
}
