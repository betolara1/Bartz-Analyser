package com.bartz.analyzer.ui.details;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;
import org.w3c.dom.Document;

import com.bartz.analyzer.api.ErpAPI;
import com.bartz.analyzer.api.PedidosAPI;
import com.bartz.analyzer.service.ArquivoService;
import com.bartz.analyzer.service.CoringaService;
import com.bartz.analyzer.ui.FileTable.FileRow;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.collections.FXCollections;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilderFactory;

public class CoringaDetails {

    private static VBox apiContent;
    private static FontIcon waitIcon;
    private static Label placeholder;

    public static Node build(FileRow row) {
        String filename = row != null ? row.getFilename() : null;
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

        // --- PREPARAR DADOS DOS CORINGAS ---
        List<String> allSiglas = new java.util.ArrayList<>();
        if (row != null && row.getFullPath() != null) {
            File xmlFile = new File(row.getFullPath());
            if (xmlFile.exists()) {
                try {
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    Document doc = factory.newDocumentBuilder().parse(xmlFile);
                    allSiglas = new CoringaService().listarSiglas(doc);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        ComboBox<String> cbSigla = new ComboBox<>();
        if (!allSiglas.isEmpty()) {
            cbSigla.setItems(FXCollections.observableArrayList(allSiglas));
            cbSigla.getSelectionModel().selectFirst();
        }

        TextField tfNovoCodigo = new TextField();

        // Card Esquerdo: Busca de Produto (ERP)
        VBox erpCard = createErpSearchCard(cbSigla, allSiglas, tfNovoCodigo);
        bottomGrid.add(erpCard, 0, 0);

        // Card Direito: Trocar Cor Coringa
        VBox coringaCard = createCoringaSwapCard(cbSigla, tfNovoCodigo, row, allSiglas);
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

    private static VBox createErpSearchCard(ComboBox<String> cbSigla, List<String> allSiglas, TextField tfNovoCodigo) {
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

        // ---------------SELECT DO ERP------------------
        // --- LÓGICA 1: POPULAR COMBOBOX ---
        cbType.setItems(FXCollections.observableArrayList("TODOS", "CHAPA", "FITA", "TAPAFURO", "PAINEL", "CAPA"));
        cbType.getSelectionModel().select("TODOS"); // Define como padrão inicial

        // --- LÓGICA 2: CONTROLAR CAMPO DE CÓDIGO E FILTRAR SIGLAS ---
        cbType.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if ("TODOS".equals(newVal)) {
                tfCode.setDisable(false);
                tfCode.setOpacity(1.0); // Totalmente visível
            } else {
                tfCode.clear(); // ZERA O CAMPO AUTOMATICAMENTE
                tfCode.setDisable(true);
                tfCode.setOpacity(0.5); // Feedback visual de desativado
            }

            // NOVA LÓGICA: Filtrar cbSigla
            if (allSiglas != null && !allSiglas.isEmpty()) {
                if ("TODOS".equals(newVal)) {
                    cbSigla.setItems(FXCollections.observableArrayList(allSiglas));
                } else {
                    List<String> filtered = allSiglas.stream()
                        .filter(s -> s.contains(newVal))
                        .collect(Collectors.toList());
                    cbSigla.setItems(FXCollections.observableArrayList(filtered));
                }
                if (!cbSigla.getItems().isEmpty()) {
                    cbSigla.getSelectionModel().selectFirst();
                }
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
            String queryDesc = tfDesc.getText();
            String queryCode = tfCode.getText();

            // Limpa resultados anteriores e esconde
            resultsContainer.getChildren().clear();
            resultsContainer.setVisible(false);

            // 1. BUSCA NO CSV (Síncrona - Painéis)
            if ("PAINEL".equals(selectedType) || "TODOS".equals(selectedType)) {
                // PRIORIDADE: Se tiver código, busca por ele. Se não, busca por descrição.
                String termoBuscaCSV = (queryCode != null && !queryCode.trim().isEmpty()) ? queryCode : queryDesc;
                
                List<String> csvResults = ErpAPI.codigoPanel(termoBuscaCSV);
                if (!csvResults.isEmpty()) {
                    resultsContainer.getChildren().add(createResultHeader());
                    for (String line : csvResults) {
                        resultsContainer.getChildren().add(createResultRow(line, tfNovoCodigo));
                    }
                    resultsContainer.setVisible(true);
                }
            }

            // 2. BUSCA NO ERP (Assíncrona - Itens)
            if (!"PAINEL".equals(selectedType)) {
                // Se for "TODOS" ou um tipo que não seja PAINEL (como CHAPA, FITA, etc)
                ErpAPI.buscaItensNoERP(selectedType, queryCode, queryDesc, resultsContainer, 
                                      line -> createResultRow(line, tfNovoCodigo), createResultHeader());
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

    private static HBox createResultRow(String csvLine, TextField tfNovoCodigo) {
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
            
            // Cola automaticamente no campo tfNovoCodigo
            if (tfNovoCodigo != null) {
                tfNovoCodigo.setText(code);
            }
        });

        row.getChildren().addAll(lblCode, lblDesc, spacer, btnCopy);
        return row;
    }

    private static VBox createCoringaSwapCard(ComboBox<String> cbSigla, TextField tfNovoCodigo, FileRow row, List<String> allSiglas) {
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

        VBox fieldSigla = createComboField("SIGLA ENCONTRADA", "", cbSigla);
        VBox fieldNovo = createField("NOVO CÓDIGO/VALOR", "Ex: 10.01.0000", tfNovoCodigo);
        HBox.setHgrow(fieldSigla, Priority.ALWAYS);
        HBox.setHgrow(fieldNovo, Priority.ALWAYS);
        form.getChildren().addAll(fieldSigla, fieldNovo);

        // Mappings (Bottom List)
        VBox mappings = new VBox(10);
        mappings.getChildren().add(new Label("Trocar Sigla"));
        
        HBox mappingItem = new HBox(10);
        mappingItem.setAlignment(Pos.CENTER_LEFT);
        Label lblMap = new Label(" → ");
        lblMap.setStyle("-fx-text-fill: #F39C12; -fx-font-weight: bold;");
        
        TextField tfMap = new TextField("");
        tfMap.setPromptText("DIGITE A COR...");
        tfMap.setStyle("-fx-background-color: #111111; -fx-text-fill: white; -fx-padding: 5 30 5 10; -fx-background-radius: 4;");
        HBox.setHgrow(tfMap, Priority.ALWAYS);

        // Ícone de busca dentro do TextField
        FontIcon searchIcon = new FontIcon(FontAwesomeSolid.SEARCH);
        searchIcon.setIconSize(12);
        searchIcon.setIconColor(Color.WHITE);
        searchIcon.setCursor(javafx.scene.Cursor.HAND);
        
        StackPane tfWrapper = new StackPane(tfMap, searchIcon);
        StackPane.setAlignment(searchIcon, Pos.CENTER_RIGHT);
        StackPane.setMargin(searchIcon, new Insets(0, 10, 0, 0));
        HBox.setHgrow(tfWrapper, Priority.ALWAYS);

        ComboBox<String> cbMap = new ComboBox<>();
        cbMap.setPromptText("Selecione...");
        cbMap.setStyle("-fx-background-color: #111111; -fx-text-fill: white;");
        
        // Lógica de busca automática no ERP
        tfMap.textProperty().addListener((obs, oldVal, newVal) -> {
            ErpAPI.buscaSiglasParaCombo(newVal, cbMap);
        });

        // Lógica de busca ao clicar na lupa ou dar Enter
        searchIcon.setOnMouseClicked(e -> ErpAPI.buscaSiglasParaCombo(tfMap.getText(), cbMap));
        tfMap.setOnAction(e -> ErpAPI.buscaSiglasParaCombo(tfMap.getText(), cbMap));
        
        Button btnConfirm = new Button();
        btnConfirm.setGraphic(new FontIcon(FontAwesomeSolid.CHECK));
        btnConfirm.setStyle("-fx-background-color: #222222; -fx-text-fill: #444444; -fx-background-radius: 4;");
        btnConfirm.setCursor(javafx.scene.Cursor.HAND);

        btnConfirm.setOnAction(e -> {
            String selected = cbMap.getValue();
            if (selected == null || !selected.contains("(") || !selected.contains(")")) {
                return;
            }

            String siglaMM = selected.substring(selected.lastIndexOf("(") + 1, selected.lastIndexOf(")"));
            
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmar Mapeamento");
            alert.setHeaderText("Substituir CG1 por " + siglaMM);
            alert.setContentText("Deseja substituir todas as ocorrências de 'CG1' por '" + siglaMM + "' no arquivo?");
            
            DialogPane dp = alert.getDialogPane();
            dp.setStyle("-fx-background-color: #1A1A1A;");
            dp.lookupAll(".label").forEach(n -> n.setStyle("-fx-text-fill: white;"));

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    if (row != null && row.getFullPath() != null) {
                        File xmlFile = new File(row.getFullPath());
                        if (xmlFile.exists()) {
                            try {
                                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                                Document doc = factory.newDocumentBuilder().parse(xmlFile);
                                
                                new CoringaService().substituirSiglaEspecifica(doc, "CG1", siglaMM);
                                new ArquivoService().salvarArquivo(doc, xmlFile);
                                
                                // Feedback visual de sucesso
                                btnConfirm.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white; -fx-background-radius: 4;");
                                
                                Alert success = new Alert(Alert.AlertType.INFORMATION);
                                success.setTitle("Sucesso");
                                success.setHeaderText(null);
                                success.setContentText("Todas as ocorrências de 'CG1' foram substituídas por '" + siglaMM + "'!");
                                DialogPane sdp = success.getDialogPane();
                                sdp.setStyle("-fx-background-color: #1A1A1A;");
                                sdp.lookupAll(".label").forEach(n -> n.setStyle("-fx-text-fill: white;"));
                                success.showAndWait();

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            });
        });

        mappingItem.getChildren().addAll(lblMap, tfWrapper, cbMap, btnConfirm);
        mappings.getChildren().add(mappingItem);

        // Button
        Button btnApply = new Button("Aplicar Substituição de Cor");
        btnApply.setGraphic(new FontIcon(FontAwesomeSolid.SYNC));
        btnApply.setMaxWidth(Double.MAX_VALUE);
        btnApply.setStyle("-fx-background-color: #A36F15; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10; -fx-background-radius: 6;");

        btnApply.setOnAction(e -> {
            String sigla = cbSigla.getValue();
            String novoCodigo = tfNovoCodigo.getText();

            if (sigla == null || sigla.isEmpty() || novoCodigo == null || novoCodigo.trim().isEmpty()) {
                Alert alertInfo = new Alert(Alert.AlertType.WARNING);
                alertInfo.setTitle("Campos Vazios");
                alertInfo.setHeaderText(null);
                alertInfo.setContentText("Por favor, selecione uma sigla e informe o novo código.");
                DialogPane dialogPane = alertInfo.getDialogPane();
                dialogPane.setStyle("-fx-background-color: #1A1A1A;");
                dialogPane.lookupAll(".label").forEach(node -> node.setStyle("-fx-text-fill: white;"));
                alertInfo.showAndWait();
                return;
            }

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmar Substituição");
            alert.setHeaderText("Confirmar Substituição de Coringa");
            alert.setContentText("Deseja substituir a sigla '" + sigla + "' pelo novo código/valor '" + novoCodigo + "'?");
            
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.setStyle("-fx-background-color: #1A1A1A;");
            dialogPane.lookupAll(".label").forEach(node -> node.setStyle("-fx-text-fill: white;"));
            
            
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    if (row != null && row.getFullPath() != null) {
                        File xmlFile = new File(row.getFullPath());
                        if (xmlFile.exists()) {
                            try {
                                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                                Document doc = factory.newDocumentBuilder().parse(xmlFile);
                                
                                new CoringaService().substituirSiglaEspecifica(doc, sigla, novoCodigo);
                                new ArquivoService().salvarArquivo(doc, xmlFile);
                                
                                // Remover a sigla das listas
                                cbSigla.getItems().remove(sigla);
                                if (allSiglas != null) {
                                    allSiglas.remove(sigla);
                                }
                                
                                // Limpar o campo de novo código
                                tfNovoCodigo.clear();
                                
                                // Se não houver mais itens, desativa a troca de siglas e libera o CG1
                                if (cbSigla.getItems().isEmpty()) {
                                    cbSigla.setDisable(true);
                                    tfNovoCodigo.setDisable(true);
                                    btnApply.setDisable(true);
                                    
                                    mappings.setDisable(false);
                                    mappings.setOpacity(1.0);
                                } else {
                                    cbSigla.getSelectionModel().selectFirst();
                                }
                                
                                // Mostra alerta de sucesso
                                Alert success = new Alert(Alert.AlertType.INFORMATION);
                                success.setTitle("Sucesso");
                                success.setHeaderText(null);
                                success.setContentText("A sigla '" + sigla + "' foi substituída por '" + novoCodigo + "' no arquivo!");
                                DialogPane dp = success.getDialogPane();
                                dp.setStyle("-fx-background-color: #1A1A1A;");
                                dp.lookupAll(".label").forEach(n -> n.setStyle("-fx-text-fill: white;"));
                                success.showAndWait();
                                
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            });
        });

        // Estado Inicial: se houver siglas, desativa os mapeamentos CG1
        if (!cbSigla.getItems().isEmpty()) {
            mappings.setDisable(true);
            mappings.setOpacity(0.5);
        } else {
            // Se já não tiver siglas de cara, desativa o topo e deixa CG1 livre
            cbSigla.setDisable(true);
            tfNovoCodigo.setDisable(true);
            btnApply.setDisable(true);
        }

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
