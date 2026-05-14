package com.bartz.analyzer.ui.details;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import com.bartz.analyzer.api.ErpAPI;
import com.bartz.analyzer.api.PedidosAPI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.collections.FXCollections;
import java.util.List;

public class CoringaDetails {

    private static VBox apiContent;
    private static FontIcon waitIcon;
    private static Label placeholder;

    public static Node build(String filename) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(10, 0, 0, 0));

        // --- 1. CARD SUPERIOR: INFORMAÇÕES DO PEDIDO (API) ---
        VBox orderInfoCard = createOrderInfoCard();
        root.getChildren().add(orderInfoCard);

        // Dispara busca na API se tiver número do pedido
        if(filename != null && filename.length() >= 5){
            String numeroPedido = filename.substring(0, 5);
            PedidosAPI.retornaComentario(numeroPedido, apiContent, waitIcon, placeholder);
        }

        // --- 2. GRID INFERIOR: ERP E TROCAR CORINGA ---
        GridPane bottomGrid = new GridPane();
        bottomGrid.setHgap(20);
        bottomGrid.setVgap(20);

        ColumnConstraints col = new ColumnConstraints();
        col.setPercentWidth(50);
        bottomGrid.getColumnConstraints().addAll(col, col);

        // Card Esquerdo: Busca de Produto (ERP)
        VBox erpCard = createErpSearchCard();
        bottomGrid.add(erpCard, 0, 0);

        // Card Direito: Trocar Cor Coringa
        VBox coringaCard = createCoringaSwapCard();
        bottomGrid.add(coringaCard, 1, 0);

        root.getChildren().add(bottomGrid);

        ErpAPI.codigoPanel("Azul");

        return root;
    }

    private static VBox createOrderInfoCard() {
        VBox card = new VBox(12);
        card.getStyleClass().add("details-card");

        Label title = new Label("INFORMAÇÕES DO PEDIDO");
        title.getStyleClass().add("details-label");

        apiContent = new VBox(15);
        apiContent.setAlignment(Pos.CENTER);
        apiContent.setMinHeight(100);
        apiContent.setStyle("-fx-background-color: #0D0D0D; -fx-background-radius: 8; -fx-border-color: #1A1A1A; -fx-padding: 20;");

        waitIcon = new FontIcon(FontAwesomeSolid.SYNC);
        waitIcon.setIconSize(24);
        waitIcon.setIconColor(Color.web("#3498DB"));
        
        // Animação de rotação
        javafx.animation.RotateTransition rt = new javafx.animation.RotateTransition(javafx.util.Duration.seconds(2), waitIcon);
        rt.setByAngle(360);
        rt.setCycleCount(javafx.animation.Animation.INDEFINITE);
        rt.setInterpolator(javafx.animation.Interpolator.LINEAR);
        rt.play();

        placeholder = new Label("CONSULTANDO OBSERVAÇÕES NO SERVIDOR...");
        placeholder.setStyle("-fx-text-fill: #666666; -fx-font-size: 10px; -fx-font-weight: bold; -fx-letter-spacing: 1px;");

        apiContent.getChildren().addAll(waitIcon, placeholder);
        card.getChildren().addAll(title, apiContent);

        return card;
    }

    private static VBox createErpSearchCard() {
        VBox card = new VBox(15);
        card.getStyleClass().add("details-card");
        card.setStyle(card.getStyle() + "-fx-border-color: rgba(52, 152, 219, 0.2);");

        // Header
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        
        VBox titleBox = createTitleBox("Buscar Código no ERP");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(titleBox, spacer);

        // Info Box
        Label infoText = new Label("Pesquise códigos no servidor para preencher campos coringa.");
        infoText.setWrapText(true);
        infoText.setStyle("-fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold;");
        VBox infoBox = new VBox(infoText);
        infoBox.setStyle("-fx-background-color: #080808; -fx-padding: 15; -fx-background-radius: 6;");

        // --- REFERÊNCIAS DOS CAMPOS ---
        TextField tfCode = new TextField();
        ComboBox<String> cbType = new ComboBox<>();
        TextField tfDesc = new TextField();
        
        // --- NOVO PAINEL DE RESULTADOS (ESTILO PREMIUM) ---
        VBox resultsContainer = new VBox(0); // Espaçamento 0 pois usaremos bordas/padding nas linhas
        resultsContainer.setStyle("-fx-background-color: #0D0D0D; -fx-background-radius: 8; -fx-border-color: #1A1A1A;");
        resultsContainer.setMaxWidth(Double.MAX_VALUE); // Garante que ocupe toda a largura
        
        // Isso faz o painel sumir e não ocupar espaço quando não houver resultados
        resultsContainer.setVisible(false);
        resultsContainer.managedProperty().bind(resultsContainer.visibleProperty());

        // Form Fields
        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(15);

        VBox fieldCode = createField("CÓDIGO DO PRODUTO", "Ex: 10.01.0001", tfCode);
        VBox fieldType = createComboField("TIPO DE ITEM", "SELECIONE UM TIPO...", cbType);
        VBox fieldDesc = createField("DESCRIÇÃO (COR, ACABAMENTO, ESPESSURA)", "Ex: BRANCO SUPREMO 18MM", tfDesc);

        // --- LÓGICA 1: POPULAR COMBOBOX ---
        cbType.setItems(FXCollections.observableArrayList("CHAPA", "FITA", "TAPAFURO", "PAINEL"));

        // --- LÓGICA 2: DESATIVAR CÓDIGO AO SELECIONAR TIPO ---
        cbType.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                tfCode.setDisable(true);
                tfCode.setOpacity(0.5); // Feedback visual de desativado
            }
        });

        form.add(fieldCode, 0, 0);
        form.add(fieldType, 1, 0);
        form.add(fieldDesc, 0, 1, 2, 1);

        // Button
        Button btnSearch = new Button("Buscar Código");
        btnSearch.setGraphic(new FontIcon(FontAwesomeSolid.SEARCH));
        btnSearch.setMaxWidth(Double.MAX_VALUE);
        btnSearch.setStyle("-fx-background-color: #1A428A; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10; -fx-background-radius: 6;");

        // --- LÓGICA 3: AÇÃO DE BUSCA ---
        btnSearch.setOnAction(e -> {
            String selectedType = cbType.getValue();
            String query = tfDesc.getText();

            if ("PAINEL".equals(selectedType) || "CHAPA".equals(selectedType)) {
                List<String> results = ErpAPI.codigoPanel(query);
                
                // Limpa resultados anteriores
                resultsContainer.getChildren().clear();
                
                if (results.isEmpty()) {
                    resultsContainer.setVisible(false);
                } else {
                    // Adiciona o Cabeçalho
                    resultsContainer.getChildren().add(createResultHeader());
                    
                    // Adiciona cada linha de resultado
                    for (String line : results) {
                        resultsContainer.getChildren().add(createResultRow(line));
                    }
                    resultsContainer.setVisible(true);
                }
            } else {
                resultsContainer.setVisible(false);
            }
        });

        card.getChildren().addAll(header, infoBox, form, btnSearch, resultsContainer);
        return card;
    }

    private static HBox createResultHeader() {
        HBox header = new HBox(20);
        header.setPadding(new Insets(15, 20, 15, 20));
        header.setStyle("-fx-border-color: transparent transparent #1A1A1A transparent;");
        
        Label lblCod = new Label("COD");
        lblCod.setMinWidth(50);
        Label lblDesc = new Label("DESCRIÇÃO DETALHADA");
        
        String style = "-fx-text-fill: #666666; -fx-font-size: 11px; -fx-font-weight: bold; -fx-letter-spacing: 1px;";
        lblCod.setStyle(style);
        lblDesc.setStyle(style);
        
        header.getChildren().addAll(lblCod, lblDesc);
        return header;
    }

    private static HBox createResultRow(String csvLine) {
        // Formato esperado: Código;Descrição;Espessura (ou similar)
        String[] parts = csvLine.split(";");
        String code = parts.length > 0 ? parts[0] : "???";
        String desc = parts.length > 1 ? parts[1] : csvLine;
        if (parts.length > 2) desc += " - " + parts[2]; // Adiciona espessura se houver

        HBox row = new HBox(15); // Reduzi um pouco o gap para dar mais espaço à descrição
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12, 20, 12, 20));
        row.getStyleClass().add("result-row");
        row.setStyle("-fx-border-color: transparent transparent #1A1A1A transparent;");

        Label lblCode = new Label(code);
        lblCode.setMinWidth(60); // Aumentei um pouco o mínimo do código
        lblCode.setStyle("-fx-text-fill: #3498DB; -fx-font-weight: bold;");

        Label lblDesc = new Label(desc.toUpperCase());
        lblDesc.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px;");
        lblDesc.setWrapText(true);

        // --- O SEGREDO PARA FICAR NA DIREITA: UM SPACER ---
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS); // Este espaço vazio vai crescer e empurrar o botão

        Button btnCopy = new Button("COPIAR");
        btnCopy.setCursor(javafx.scene.Cursor.HAND); // Mostra a "mãozinha" ao passar o mouse
        btnCopy.setMinWidth(Button.USE_PREF_SIZE);
        
        // Estilo Inicial
        String styleNormal = "-fx-background-color: transparent; -fx-border-color: #1A428A; -fx-border-radius: 4; -fx-text-fill: #3498DB; -fx-font-size: 9px; -fx-font-weight: bold; -fx-padding: 4 12;";
        String styleHover = "-fx-background-color: #1A428A; -fx-border-color: #1A428A; -fx-border-radius: 4; -fx-text-fill: white; -fx-font-size: 9px; -fx-font-weight: bold; -fx-padding: 4 12;";
        
        btnCopy.setStyle(styleNormal);
        
        // Efeito de passar o mouse (Parecer um botão de verdade)
        btnCopy.setOnMouseEntered(e -> btnCopy.setStyle(styleHover));
        btnCopy.setOnMouseExited(e -> btnCopy.setStyle(styleNormal));
        
        // Lógica de copiar
        btnCopy.setOnAction(e -> {
            final javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
            final javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
            content.putString(code);
            clipboard.setContent(content);
        });

        row.getChildren().addAll(lblCode, lblDesc, spacer, btnCopy);
        return row;
    }

    private static VBox createCoringaSwapCard() {
        VBox card = new VBox(15);
        card.getStyleClass().add("details-card");
        card.setStyle(card.getStyle() + "-fx-border-color: rgba(243, 156, 18, 0.2);");

        // Header
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        
        StackPane iconBox = createIconBox(FontAwesomeSolid.EXCLAMATION_TRIANGLE, "#F39C12");
        VBox titleBox = createTitleBox("Trocar Cor Coringa");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        FontIcon arrow = new FontIcon(FontAwesomeSolid.CHEVRON_UP);
        arrow.setIconColor(Color.web("#333333"));

        header.getChildren().addAll(iconBox, titleBox, spacer, arrow);

        // Info Box
        Label infoText = new Label("Escolha a sigla coringa e o novo código para substituição do código.");
        infoText.setWrapText(true);
        infoText.setStyle("-fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold;");
        VBox infoBox = new VBox(infoText);
        infoBox.setStyle("-fx-background-color: #080808; -fx-padding: 15; -fx-background-radius: 6;");

        // Form Fields
        HBox form = new HBox(15);
        ComboBox<String> cbSigla = new ComboBox<>();
        TextField tfNovoCodigo = new TextField();

        VBox fieldSigla = createComboField("SIGLA ENCONTRADA", "", cbSigla);
        VBox fieldNovo = createField("NOVO CÓDIGO/VALOR", "Ex: 10.01.0000", tfNovoCodigo);
        HBox.setHgrow(fieldSigla, Priority.ALWAYS);
        HBox.setHgrow(fieldNovo, Priority.ALWAYS);
        form.getChildren().addAll(fieldSigla, fieldNovo);

        // Button
        Button btnApply = new Button("Aplicar Substituição de Cor");
        btnApply.setGraphic(new FontIcon(FontAwesomeSolid.SYNC));
        btnApply.setMaxWidth(Double.MAX_VALUE);
        btnApply.setStyle("-fx-background-color: #A36F15; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10; -fx-background-radius: 6;");

        // Mappings (Bottom List)
        VBox mappings = new VBox(10);
        mappings.getChildren().add(new Label("CG1"));
        
        HBox mappingItem = new HBox(10);
        mappingItem.setAlignment(Pos.CENTER_LEFT);
        Label lblMap = new Label("CG1 → ");
        lblMap.setStyle("-fx-text-fill: #F39C12; -fx-font-weight: bold;");
        
        TextField tfMap = new TextField("");
        tfMap.setStyle("-fx-background-color: #111111; -fx-text-fill: #888888; -fx-padding: 5 10; -fx-background-radius: 4;");
        HBox.setHgrow(tfMap, Priority.ALWAYS);
        
        ComboBox<String> cbMap = new ComboBox<>();
        cbMap.setPromptText("Selecione...");
        cbMap.setStyle("-fx-background-color: #111111; -fx-text-fill: #888888;");
        
        Button btnConfirm = new Button();
        btnConfirm.setGraphic(new FontIcon(FontAwesomeSolid.CHECK));
        btnConfirm.setStyle("-fx-background-color: #222222; -fx-text-fill: #444444; -fx-background-radius: 4;");

        mappingItem.getChildren().addAll(lblMap, tfMap, cbMap, btnConfirm);
        mappings.getChildren().add(mappingItem);

        card.getChildren().addAll(header, infoBox, form, btnApply, mappings);
        return card;
    }

    private static StackPane createIconBox(FontAwesomeSolid icon, String color) {
        StackPane box = new StackPane();
        box.setPrefSize(36, 36);
        box.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-background-radius: 8;");
        FontIcon fi = new FontIcon(icon);
        fi.setIconSize(16);
        fi.setIconColor(Color.web(color).deriveColor(0, 1, 1, 0.6));
        box.getChildren().add(fi);
        return box;
    }

    private static VBox createTitleBox(String t) {
        VBox box = new VBox(2);
        Label lblT = new Label(t);
        lblT.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold;");
        box.getChildren().addAll(lblT);
        return box;
    }

    private static VBox createField(String label, String prompt, TextField tf) {
        VBox box = new VBox(5);
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill: #A7A7A7; -fx-font-size: 10px; -fx-font-weight: bold;");
        tf.setPromptText(prompt);
        tf.setStyle("-fx-background-color: #0D0D0D; -fx-border-color: #1A1A1A; -fx-border-radius: 4; -fx-text-fill: white; -fx-padding: 8;");
        box.getChildren().addAll(lbl, tf);
        return box;
    }

    private static VBox createComboField(String label, String value, ComboBox<String> cb) {
        VBox box = new VBox(5);
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill: #A7A7A7; -fx-font-size: 10px; -fx-font-weight: bold;");
        cb.setPromptText(value);
        cb.setMaxWidth(Double.MAX_VALUE);
        cb.setStyle("-fx-background-color: #0D0D0D; -fx-border-color: #1A1A1A; -fx-border-radius: 4; -fx-text-fill: white;");
        box.getChildren().addAll(lbl, cb);
        return box;
    }
}
