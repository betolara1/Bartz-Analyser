package com.bartz.analyzer.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import com.bartz.analyzer.ui.details.CoringaDetails;
import com.bartz.analyzer.ui.details.GeralDetails;

public class FileDetailsView extends ScrollPane {

    private final VBox content;
    private VBox dynamicScreen; // Onde as telas vão aparecer
    private Button activeTab; // Para sabermos qual aba está selecionada

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

        dynamicScreen = new VBox(20);

        showVisaoGeral(row);

        content.getChildren().addAll(header, tabs, dynamicScreen);
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



    // --- MÉTODO DAS ABAS ---
    private HBox createTabs(FileTable.FileRow row) {
        HBox tabs = new HBox(20);
        tabs.setAlignment(Pos.CENTER_LEFT);

        // ABA PRINCIPAL COM TODAS AS INFORMAÇÕES
        Button btnGeral = new Button("VISÃO GERAL");
        btnGeral.getStyleClass().addAll("btn-ghost", "tab-active");
        activeTab = btnGeral; // <--- AGORA O SISTEMA SABE QUEM ESTÁ ATIVO
        
        FontIcon iconGeral = new FontIcon(FontAwesomeSolid.EYE);
        iconGeral.setIconColor(Color.web("#27AE60"));
        btnGeral.setGraphic(iconGeral);

        btnGeral.setOnAction(e -> switchTab(btnGeral, GeralDetails.build(row, this)));

        tabs.getChildren().addAll(btnGeral);

        // ABAS DOS ERROS CONDICIONAIS
        String erros = row.getErrors();

        if (erros.contains("CORINGA")) {
            Button btnCoringa = createTabButton("CORINGA", FontAwesomeSolid.MAGIC, false);
            btnCoringa.setOnAction(e -> switchTab(btnCoringa, CoringaDetails.build(row)));
            tabs.getChildren().add(btnCoringa);
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

    private Button createTabButton(String text, org.kordamp.ikonli.Ikon icon, boolean active) {
        Button btn = new Button(text);
        btn.getStyleClass().add("btn-ghost");
        if (active) btn.getStyleClass().add("tab-active");
        
        FontIcon fontIcon = new FontIcon(icon);
        if (active) fontIcon.setIconColor(javafx.scene.paint.Color.web("#27AE60"));
        
        btn.setGraphic(fontIcon);
        return btn;
    }

    private void switchTab(Button button, Node view){
        if(activeTab != null){
            activeTab.getStyleClass().remove("tab-active");
            activeTab.setStyle(""); // Limpa estilos inline se houver
            if(activeTab.getGraphic() instanceof FontIcon icon){
                icon.setIconColor(Color.web("#A7A7A7"));
            }
        }

        // 2. Ativa o novo botão
        activeTab = button;
        activeTab.getStyleClass().add("tab-active");
        if (activeTab.getGraphic() instanceof FontIcon icon) {
            icon.setIconColor(Color.web("#27AE60"));
        }
        
        // 3. Troca a tela no "palco"
        dynamicScreen.getChildren().clear();
        dynamicScreen.getChildren().add(view);
    }


    // CRIA A CLASSE DO GERALDETAILS PRA BUSCAR PELA ABA DE 'VISÃO GERAL'
    private void showVisaoGeral(FileTable.FileRow row){
        Node screen = GeralDetails.build(row, this);

        dynamicScreen.getChildren().clear();
        dynamicScreen.getChildren().add(screen);
    }
}
