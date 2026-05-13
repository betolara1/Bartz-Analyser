package com.bartz.analyzer.ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public class FileDetailsView extends ScrollPane {

    private final VBox content;
    private VBox apiContent;
    private VBox dynamicScreen;

    private FontIcon waitIcon;
    private Label placeholder;

    public FileDetailsView(FileTable.FileRow row, Runnable onBack) {
        this.setFitToWidth(true);
        this.getStyleClass().add("dark-scroll-pane");

        content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: transparent;");

        // --- CABEÇALHO ---
        HBox header = createHeader(row, onBack);

        // --- ABAS (VISÃO GERAL / COMPONENTES) ---
        HBox tabs = createTabs(row);

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

        // Card: Maquinário (Move to row 0, col 1 next to data processamento)
        VBox maquinario = createMaquinario();
        grid.add(maquinario, 1, 0);

        // Card: Caminho do Arquivo (Full Width)
        VBox cardCaminho = createCard("CAMINHO DO ARQUIVO", row.getFullPath(), null);
        cardCaminho.setStyle(cardCaminho.getStyle() + "-fx-background-color: #0D0D0D;");
        grid.add(cardCaminho, 0, 1, 2, 1);

        // Card: Inconformidades
        VBox inconformidades = createInconformidades(row.getErrors());
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

            retornaComentario(numeroPedido);
        }

        content.getChildren().addAll(header, tabs, grid);
        this.setContent(content);
    }

    private HBox createHeader(FileTable.FileRow row, Runnable onBack) {
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        Button btnBack = new Button();
        btnBack.setGraphic(new FontIcon(FontAwesomeSolid.ARROW_LEFT));
        btnBack.getStyleClass().add("btn-ghost");
        btnBack.setOnAction(e -> onBack.run());

        VBox titleBox = new VBox(2);
        Label title = new Label(row.getFilename());
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label subtitle = new Label("ANÁLISE TÉCNICA PROFUNDA DO COMPONENTE");
        subtitle.setStyle("-fx-font-size: 11px; -fx-text-fill: #A7A7A7;");

        Label statusBadge = new Label(row.getStatus());
        statusBadge.getStyleClass().addAll("badge", row.getStatus().equals("OK") ? "badge-ok" : "badge-erro");

        titleBox.getChildren().addAll(title, subtitle, statusBadge);
        header.getChildren().addAll(btnBack, titleBox);
        return header;
    }

    private VBox createCard(String titleStr, String valueStr, javafx.scene.Node customNode) {
        VBox card = new VBox(8);
        card.getStyleClass().add("details-card");
        Label lbl = new Label(titleStr);
        lbl.getStyleClass().add("details-label");

        card.getChildren().add(lbl);
        if (customNode != null)
            card.getChildren().add(customNode);
        else {
            Label val = new Label(valueStr);
            val.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            card.getChildren().add(val);
        }
        return card;
    }

    // --- MÉTODO DAS ABAS ---
    private HBox createTabs(FileTable.FileRow row) {
        HBox tabs = new HBox(20);
        tabs.setAlignment(Pos.CENTER_LEFT);

        // ABA PRINCIPAL COM TODAS AS INFORMAÇÕES
        Button btnGeral = new Button("VISÃO GERAL");
        btnGeral.getStyleClass().addAll("btn-ghost", "tab-active"); // tab-active define a linha verde embaixo
        FontIcon iconGeral = new FontIcon(FontAwesomeSolid.EYE);
        iconGeral.setIconColor(Color.web("#27AE60"));
        btnGeral.setGraphic(iconGeral);

        tabs.getChildren().addAll(btnGeral);

        // ABAS DOS ERROS CONDICIONAIS
        String erros = row.getErrors();

        if (erros.contains("CORINGA")) {
            tabs.getChildren().add(createTabButton("CORINGA", FontAwesomeSolid.MAGIC, false));
        }
        if (erros.contains("SEM ITEM FILHO")) {
            tabs.getChildren().add(createTabButton("ITENS FILHOS", FontAwesomeSolid.SITEMAP, false));
        }
        if (erros.contains("MUXARABI")) {
            tabs.getChildren().add(createTabButton("MUXARABI", FontAwesomeSolid.BORDER_ALL, false));
        }
        if (erros.contains("DUPLADOS")) {
            tabs.getChildren().add(createTabButton("DUPLADOS", FontAwesomeSolid.COPY, false));
        }
        if (erros.contains("SEM CODIGO")) {
            tabs.getChildren().add(createTabButton("SEM CÓDIGO", FontAwesomeSolid.BARCODE, false));
        }

        return tabs;
    }

    // --- MÉTODO DAS INCONFORMIDADES (ERROS) ---
    private VBox createInconformidades(String errors) {
        VBox card = new VBox(10);
        card.getStyleClass().add("details-card");

        Label title = new Label("INCONFORMIDADES");
        title.getStyleClass().add("details-label");
        title.setTextFill(Color.web("#E74C3C")); // Vermelho para erro
        FlowPane flow = new FlowPane(10, 10);
        if (errors == null || errors.isEmpty()) {
            flow.getChildren().add(new Label("Nenhuma inconformidade encontrada."));
        } else {
            for (String err : errors.split(";")) {
                String trimmed = err.trim();
                Label badge = new Label(trimmed);
                
                // 1. Começamos com a classe base de badge
                badge.getStyleClass().add("badge");
                // 2. Lógica de cores:
                if ("FERRAGENS".equals(trimmed) || "MUXARABI".equals(trimmed)) {
                    // Aplica a cor amarela/laranja (mesma que usamos na tabela principal)
                    badge.getStyleClass().add("badge-ferragens"); 
                } else {
                    // Mantém vermelho para os outros erros
                    badge.getStyleClass().add("badge-erro");
                }
                flow.getChildren().add(badge);
            }
        }
        card.getChildren().addAll(title, flow);
        return card;
    }

    // --- MÉTODO DO MAQUINÁRIO ---
    private VBox createMaquinario() {
        VBox card = new VBox(10);
        card.getStyleClass().add("details-card");
        Label title = new Label("MAQUINÁRIO / PLUGINS (GERADOS)");
        title.getStyleClass().add("details-label");
        HBox machines = new HBox(10);
        machines.getChildren().addAll(
                createMachineBox("2530", "ASPAN", true),
                createMachineBox("2534", "NCB612", true),
                createMachineBox("2341", "CYFLEX 900", true),
                createMachineBox("2525", "MSZ600", true));
        card.getChildren().addAll(title, machines);
        return card;
    }

    private VBox createMachineBox(String id, String name, boolean ok) {
        VBox box = new VBox(2);
        box.getStyleClass().add("machine-box");
        if (!ok)
            box.setStyle("-fx-border-color: #E74C3C;");
        Label lblId = new Label(id);
        lblId.getStyleClass().add("machine-id");

        Label lblName = new Label(name);
        lblName.getStyleClass().add("machine-name");
        if (!ok)
            lblName.setStyle("-fx-text-fill: #E74C3C;");
        box.getChildren().addAll(lblId, lblName);
        return box;
    }

    // --- MÉTODO DA CHAVE DE IMPORTAÇÃO ---
    private VBox createImportKeyCard(String erpKey) {
        VBox card = new VBox(8);
        card.getStyleClass().add("details-card");
        Label lbl = new Label("CHAVE DE IMPORTAÇÃO (ERP)");
        lbl.getStyleClass().add("details-label");

        HBox keyBox = new HBox(10);
        keyBox.setAlignment(Pos.CENTER_LEFT);
        keyBox.setStyle(
                "-fx-background-color: #1A1A1A; -fx-padding: 12 16; -fx-background-radius: 4; -fx-cursor: hand;");

        Label val = new Label(erpKey != null && !erpKey.isEmpty() ? erpKey : "Nenhuma chave encontrada");
        val.setStyle(
                "-fx-text-fill: white; -fx-font-size: 16px; -fx-font-family: 'Consolas', 'Courier New', monospace;");
        HBox.setHgrow(val, Priority.ALWAYS);

        FontIcon copyIcon = new FontIcon(FontAwesomeSolid.COPY);
        copyIcon.setIconColor(Color.web("#A7A7A7"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        keyBox.getChildren().addAll(val, spacer, copyIcon);

        Tooltip copyTooltip = new Tooltip("Copiar chave");
        Tooltip.install(keyBox, copyTooltip);

        keyBox.setOnMouseClicked(e -> {
            if (erpKey != null && !erpKey.isEmpty()) {
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent clipboardContent = new ClipboardContent();
                clipboardContent.putString(erpKey);
                clipboard.setContent(clipboardContent);

                // Feedback visual
                copyIcon.setIconColor(Color.web("#27AE60")); // Verde
                javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(
                        javafx.util.Duration.seconds(1.5));
                pause.setOnFinished(event -> copyIcon.setIconColor(Color.web("#A7A7A7")));
                pause.play();
            }
        });

        card.getChildren().addAll(lbl, keyBox);
        return card;
    }

    // --- MÉTODO DAS INFORMAÇÕES DO PEDIDO (API) ---
    private VBox createOrderInfoCard() {
        VBox card = new VBox(10);
        card.getStyleClass().add("details-card");

        Label title = new Label("INFORMAÇÕES DO PEDIDO");
        title.getStyleClass().add("details-label");

        apiContent = new VBox(15);
        apiContent.setAlignment(Pos.CENTER);
        apiContent.setStyle(
                "-fx-padding: 30; -fx-background-color: #0D0D0D; -fx-background-radius: 6; -fx-border-color: #1A1A1A; -fx-border-radius: 6;");

        waitIcon = new FontIcon(FontAwesomeSolid.SYNC);
        waitIcon.setIconSize(24);
        waitIcon.setIconColor(Color.web("#A7A7A7"));

        placeholder = new Label("Buscando informações do pedido...");
        placeholder.setStyle("-fx-text-fill: #A7A7A7; -fx-font-style: italic;");

        apiContent.getChildren().addAll(waitIcon, placeholder);

        card.getChildren().addAll(title, apiContent);

        return card;
    }

    private Button createTabButton(String text, org.kordamp.ikonli.Ikon icon, boolean active) {
        Button btn = new Button(text);
        btn.getStyleClass().add("btn-ghost");
        if (active) btn.getStyleClass().add("tab-active");
        
        FontIcon fontIcon = new FontIcon(icon);
        if (active) fontIcon.setIconColor(javafx.scene.paint.Color.web("#27AE60"));
        
        btn.setGraphic(fontIcon);
        return btn;
    }


    // Método que busca os comentarios dos pedidos
    public void retornaComentario(String numeroPedido) {
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
                    for(JsonNode item : root){
                        // busca o nome dos campos pra retornar o comentario
                        String titulo = item.path("txt_titulo").asText();
                        String comentario = item.path("txt_comentario").asText();

                        // MONTA O TITULO + COMENTARIO
                        if (!comentario.isEmpty()) {
                            textoCompleto.append(titulo.toUpperCase())
                                        .append(":\n")
                                        .append(comentario)
                                        .append("\n\n"); // Pula linha para o próximo
                        }
                    }
                }

                String resultadoFinal = textoCompleto.toString().trim();

                Platform.runLater(() -> {
                    waitIcon.setVisible(false);
                    waitIcon.setManaged(false);

                    placeholder.setText(resultadoFinal.isEmpty() ? "Pedido sem comentário." : resultadoFinal);
                    placeholder.setStyle("-fx-text-fill: white; -fx-font-style: italic; -fx-font-size: 14px;");

                    apiContent.setAlignment(Pos.CENTER_LEFT);
                    apiContent.setStyle(apiContent.getStyle() + "-fx-padding: 20;");
                });
            } 
            catch (Exception e) {
                Platform.runLater(() -> {
                    waitIcon.setVisible(false);
                    waitIcon.setManaged(false);

                    placeholder.setText("Informações do pedido indisponíveis.");
                    placeholder.setStyle("-fx-text-fill: #E74C3C; -fx-font-style: italic; -fx-font-size: 14px;");
                });
            }
        });
    }
}
