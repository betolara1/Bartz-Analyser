package com.bartz.analyzer.ui.details;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import com.bartz.analyzer.api.PedidosAPI;
import com.bartz.analyzer.ui.FileDetailsView;
import com.bartz.analyzer.ui.FileTable;

import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;


import javafx.scene.paint.Color;

public class GeralDetails {

    private static VBox apiContent;
    private static FontIcon waitIcon;
    private static Label placeholder;

    public static Node build(FileTable.FileRow row, FileDetailsView parent){

        // --- GRID DE CARDS ---
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);

        // FORÇAR AS COLUNAS SEREM DINAMICAS
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);

        grid.getColumnConstraints().addAll(col1, col2);

        // Card: Data Processamento
        VBox cardData = createCard("DATA DO PROCESSAMENTO", row.getTimestamp(), null);
        grid.add(cardData, 0, 0);

        // Card: Maquinário
        VBox maquinario = createMaquinario();
        grid.add(maquinario, 1, 0);

        // Card: Caminho do Arquivo (Full Width)
        VBox cardCaminho = createCard("CAMINHO DO ARQUIVO", row.getFullPath(), null);
        cardCaminho.setStyle(cardCaminho.getStyle() + "-fx-background-color: #0D0D0D;");
        grid.add(cardCaminho, 0, 1, 2, 1);

        // Card: Inconformidades (Erros e Tags)
        VBox inconformidades = createFindingsCard(row.getErrors(), row.getTags());
        grid.add(inconformidades, 0, 2);

        // Card: Chave de Importação
        VBox importKeyCard = createImportKeyCard(row.getErpKey());
        grid.add(importKeyCard, 1, 2);

        // Card: Informações do Pedido (API)
        VBox orderInfoCard = createOrderInfoCard();
        grid.add(orderInfoCard, 0, 3, 2, 1);

        // MOSTRA AS INFORMAÇÕES DO PEDIDO
        String nomeArquivo = row.getFilename();

        if(nomeArquivo != null && nomeArquivo.length() >= 5){
            String numeroPedido = nomeArquivo.substring(0, 5);
            PedidosAPI.retornaComentario(numeroPedido, apiContent, waitIcon, placeholder);
        }

        return grid;
    }

    private static VBox createCard(String titleStr, String valueStr, javafx.scene.Node customNode) {
        VBox card = new VBox(8);
        card.getStyleClass().add("details-card");
        
        Label lbl = new Label(titleStr);
        lbl.getStyleClass().add("details-label");

        card.getChildren().add(lbl);
        if (customNode != null) {
            card.getChildren().add(customNode);
        } 
        else {
            Label val = new Label(valueStr);
            val.setStyle("-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold;");
            card.getChildren().add(val);
        }
        return card;
    }

    private static VBox createFindingsCard(String errors, String tags) {
        VBox card = new VBox(15);
        card.getStyleClass().add("details-card");

        // --- Seção de Erros ---
        VBox sectionErros = new VBox(8);
        Label titleErros = new Label("INCONFORMIDADES (ERROS)");
        titleErros.getStyleClass().add("details-label");
        titleErros.setTextFill(Color.web("#E74C3C"));
        
        FlowPane flowErros = new FlowPane(8, 8);
        if (errors == null || errors.isEmpty()) {
            Label lbl = new Label("Nenhum erro crítico.");
            lbl.setStyle("-fx-text-fill: #555555; -fx-font-size: 11px;");
            flowErros.getChildren().add(lbl);
        } else {
            for (String err : errors.split(";")) {
                Label badge = new Label(err.trim().toUpperCase());
                badge.getStyleClass().addAll("badge", "badge-erro");
                flowErros.getChildren().add(badge);
            }
        }
        sectionErros.getChildren().addAll(titleErros, flowErros);

        // --- Seção de Tags ---
        VBox sectionTags = new VBox(8);
        Label titleTags = new Label("TAGS / INCONSISTÊNCIAS");
        titleTags.getStyleClass().add("details-label");
        titleTags.setTextFill(Color.web("#3498DB"));
        
        FlowPane flowTags = new FlowPane(8, 8);
        if (tags == null || tags.isEmpty()) {
            Label lbl = new Label("Nenhuma tag detectada.");
            lbl.setStyle("-fx-text-fill: #555555; -fx-font-size: 11px;");
            flowTags.getChildren().add(lbl);
        } else {
            for (String tag : tags.split(";")) {
                Label badge = new Label(tag.trim().toUpperCase());
                badge.getStyleClass().addAll("badge", "badge-ferragens"); // Usando a mesma cor azul/amarela da tabela
                flowTags.getChildren().add(badge);
            }
        }
        sectionTags.getChildren().addAll(titleTags, flowTags);

        card.getChildren().addAll(sectionErros, sectionTags);
        return card;
    }

    private static VBox createMachineBox(String id, String name, boolean ok) {
        HBox box = new HBox(12);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(10, 15, 10, 15));
        box.setStyle("-fx-background-color: #1A1A1A; -fx-background-radius: 8; -fx-border-color: #2C2C2C; -fx-border-radius: 8;");
        
        if (!ok) {
            box.setStyle(box.getStyle() + "-fx-border-color: #E74C3C; -fx-background-color: rgba(231, 76, 60, 0.05);");
        }

        FontIcon icon = new FontIcon(ok ? FontAwesomeSolid.CHECK_CIRCLE : FontAwesomeSolid.EXCLAMATION_CIRCLE);
        icon.setIconSize(16);
        icon.setIconColor(Color.web(ok ? "#27AE60" : "#E74C3C"));

        VBox text = new VBox(0);
        Label lblId = new Label(id);
        lblId.setStyle("-fx-text-fill: #A7A7A7; -fx-font-size: 9px; -fx-font-weight: bold;");
        
        Label lblName = new Label(name);
        lblName.setStyle("-fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold;");
        
        text.getChildren().addAll(lblId, lblName);
        box.getChildren().addAll(icon, text);
        
        return new VBox(box);
    }

    private static VBox createMaquinario() {
        VBox card = new VBox(10);
        card.getStyleClass().add("details-card");
        
        Label title = new Label("MAQUINÁRIO / PLUGINS (GERADOS)");
        title.getStyleClass().add("details-label");
        
        FlowPane machines = new FlowPane(10, 10);
        machines.getChildren().addAll(
                createMachineBox("2530", "ASPAN", true),
                createMachineBox("2534", "NCB612", true),
                createMachineBox("2341", "CYFLEX 900", true),
                createMachineBox("2525", "MSZ600", true));
        
        card.getChildren().addAll(title, machines);
        return card;
    }

    private static VBox createImportKeyCard(String erpKey) {
        VBox card = new VBox(10);
        card.getStyleClass().add("details-card");
        
        Label lbl = new Label("CHAVE DE IMPORTAÇÃO (ERP)");
        lbl.getStyleClass().add("details-label");

        HBox keyBox = new HBox(15);
        keyBox.setAlignment(Pos.CENTER_LEFT);
        keyBox.setStyle("-fx-background-color: #0D0D0D; -fx-padding: 15; -fx-background-radius: 8; -fx-border-color: #2C2C2C; -fx-border-radius: 8; -fx-cursor: hand;");

        Label val = new Label(erpKey != null && !erpKey.isEmpty() ? erpKey : "NENHUMA CHAVE ENCONTRADA");
        val.setStyle("-fx-text-fill: #E0E0E0; -fx-font-size: 14px; -fx-font-family: 'Consolas', monospace;");
        val.setWrapText(true);
        HBox.setHgrow(val, Priority.ALWAYS);

        FontIcon copyIcon = new FontIcon(FontAwesomeSolid.COPY);
        copyIcon.setIconSize(18);
        copyIcon.setIconColor(Color.web("#A7A7A7"));

        keyBox.getChildren().addAll(val, copyIcon);

        keyBox.setOnMouseEntered(e -> keyBox.setStyle(keyBox.getStyle() + "-fx-border-color: #3498DB;"));
        keyBox.setOnMouseExited(e -> keyBox.setStyle(keyBox.getStyle().replace("-fx-border-color: #3498DB;", "-fx-border-color: #2C2C2C;")));

        keyBox.setOnMouseClicked(e -> {
            if (erpKey != null && !erpKey.isEmpty()) {
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent content = new ClipboardContent();
                content.putString(erpKey);
                clipboard.setContent(content);

                copyIcon.setIconColor(Color.web("#27AE60"));
                copyIcon.setIconCode(FontAwesomeSolid.CHECK);
                
                javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(2));
                pause.setOnFinished(ev -> {
                    copyIcon.setIconColor(Color.web("#A7A7A7"));
                    copyIcon.setIconCode(FontAwesomeSolid.COPY);
                });
                pause.play();
            }
        });

        card.getChildren().addAll(lbl, keyBox);
        return card;
    }

    private static VBox createOrderInfoCard() {
        VBox card = new VBox(12);
        card.getStyleClass().add("details-card");

        Label title = new Label("INFORMAÇÕES DO PEDIDO");
        title.getStyleClass().add("details-label");

        apiContent = new VBox(20);
        apiContent.setAlignment(Pos.CENTER);
        apiContent.setMinHeight(120);
        apiContent.setStyle("-fx-background-color: #0D0D0D; -fx-background-radius: 10; -fx-border-color: #1A1A1A; -fx-border-radius: 10; -fx-padding: 20;");

        waitIcon = new FontIcon(FontAwesomeSolid.SYNC);
        waitIcon.setIconSize(28);
        waitIcon.setIconColor(Color.web("#3498DB"));
        
        // Animação de rotação para o ícone de espera
        javafx.animation.RotateTransition rt = new javafx.animation.RotateTransition(javafx.util.Duration.seconds(2), waitIcon);
        rt.setByAngle(360);
        rt.setCycleCount(javafx.animation.Animation.INDEFINITE);
        rt.setInterpolator(javafx.animation.Interpolator.LINEAR);
        rt.play();

        placeholder = new Label("CONSULTANDO BASE DE DADOS...");
        placeholder.setStyle("-fx-text-fill: #666666; -fx-font-size: 11px; -fx-font-weight: bold; -fx-letter-spacing: 1px;");

        apiContent.getChildren().addAll(waitIcon, placeholder);
        card.getChildren().addAll(title, apiContent);

        return card;
    }
}
