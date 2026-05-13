package com.bartz.analyzer.ui.details;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class CoringaDetails {

    public static Node build(String filename) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(10, 0, 0, 0));

        // --- CARD SUPERIOR: INFORMAÇÕES DO PEDIDO ---
        VBox orderInfoCard = createOrderInfoCard();
        root.getChildren().add(orderInfoCard);

        // --- GRID INFERIOR: ERP E COR CORINGA ---
        GridPane bottomGrid = new GridPane();
        bottomGrid.setHgap(20);
        bottomGrid.setVgap(20);

        ColumnConstraints col = new ColumnConstraints();
        col.setPercentWidth(50);
        bottomGrid.getColumnConstraints().addAll(col, col);

        VBox erpCard = createSmallCard(
            "Conexão Direta ERP: Busca de Produto", 
            "PESQUISA NO SERVIDOR POR CÓDIGOS ORIGINAIS", 
            FontAwesomeSolid.SEARCH, 
            "#3498DB" // Azul
        );
        
        VBox coringaCard = createSmallCard(
            "Cor Coringa Detectada", 
            "SUBSTITUIÇÃO DE SIGLAS GENÉRICAS IDENTIFICADAS", 
            FontAwesomeSolid.EXCLAMATION_TRIANGLE, 
            "#F1C40F" // Amarelo/Laranja
        );
        coringaCard.setStyle(coringaCard.getStyle() + "-fx-border-color: rgba(241, 196, 15, 0.3); -fx-border-width: 1; -fx-border-radius: 8;");

        bottomGrid.add(erpCard, 0, 0);
        bottomGrid.add(coringaCard, 1, 0);

        root.getChildren().add(bottomGrid);

        return root;
    }

    private static VBox createOrderInfoCard() {
        VBox card = new VBox(15);
        card.getStyleClass().add("details-card");
        card.setStyle(card.getStyle() + "-fx-border-color: rgba(52, 152, 219, 0.2); -fx-border-width: 1; -fx-border-radius: 8;");

        // Header do Card
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        StackPane iconContainer = new StackPane();
        iconContainer.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-background-radius: 50%; -fx-min-width: 40; -fx-min-height: 40;");
        FontIcon infoIcon = new FontIcon(FontAwesomeSolid.INFO_CIRCLE);
        infoIcon.setIconColor(Color.web("#A7A7A7"));
        infoIcon.setIconSize(18);
        iconContainer.getChildren().add(infoIcon);

        VBox titleBox = new VBox(2);
        Label title = new Label("Informações do Pedido");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold;");
        Label subtitle = new Label("CONSULTA DE OBSERVAÇÕES DE FÁBRICA");
        subtitle.setStyle("-fx-text-fill: #A7A7A7; -fx-font-size: 11px;");
        titleBox.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        FontIcon arrow = new FontIcon(FontAwesomeSolid.CHEVRON_UP);
        arrow.setIconColor(Color.web("#333333"));
        arrow.setIconSize(14);
        StackPane arrowBtn = new StackPane(arrow);
        arrowBtn.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-background-radius: 50%; -fx-min-width: 28; -fx-min-height: 28;");

        header.getChildren().addAll(iconContainer, titleBox, spacer, arrowBtn);

        // Section: DADOS DO SERVIDOR
        HBox serverSection = new HBox(10);
        serverSection.setAlignment(Pos.CENTER_LEFT);
        serverSection.setPadding(new Insets(10, 0, 5, 0));

        FontIcon dbIcon = new FontIcon(FontAwesomeSolid.DATABASE);
        dbIcon.setIconColor(Color.web("#A7A7A7"));
        dbIcon.setIconSize(12);

        Label serverTitle = new Label("DADOS DO SERVIDOR");
        serverTitle.setStyle("-fx-text-fill: #A7A7A7; -fx-font-size: 11px; -fx-font-weight: bold; -fx-letter-spacing: 1px;");

        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        Button btnSync = new Button("SINCRONIZAR");
        btnSync.setGraphic(new FontIcon(FontAwesomeSolid.SYNC));
        btnSync.getStyleClass().add("btn-sync");
        btnSync.setStyle("-fx-background-color: rgba(52, 152, 219, 0.1); -fx-text-fill: #3498DB; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 6 12; -fx-background-radius: 4; -fx-border-color: rgba(52, 152, 219, 0.3); -fx-border-radius: 4;");

        serverSection.getChildren().addAll(dbIcon, serverTitle, spacer2, btnSync);

        // Content Area (Black box)
        VBox contentBox = new VBox(8);
        contentBox.setStyle("-fx-background-color: #080808; -fx-padding: 20; -fx-background-radius: 6; -fx-border-color: #111111;");
        
        Label contentTitle = new Label("ALTERAÇÃO DE COR");
        contentTitle.setStyle("-fx-text-fill: #3498DB; -fx-font-size: 11px; -fx-font-weight: bold;");
        
        Label contentText = new Label("Segue cantos para serem fabricados na cor PANNA");
        contentText.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");

        contentBox.getChildren().addAll(contentTitle, contentText);

        card.getChildren().addAll(header, serverSection, contentBox);
        return card;
    }

    private static VBox createSmallCard(String titleStr, String subStr, FontAwesomeSolid icon, String colorHex) {
        VBox card = new VBox(15);
        card.getStyleClass().add("details-card");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(20));

        HBox top = new HBox(15);
        top.setAlignment(Pos.CENTER_LEFT);

        StackPane iconContainer = new StackPane();
        iconContainer.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-background-radius: 8; -fx-min-width: 36; -fx-min-height: 36;");
        FontIcon mainIcon = new FontIcon(icon);
        mainIcon.setIconColor(Color.web(colorHex).deriveColor(0, 1, 1, 0.5));
        mainIcon.setIconSize(16);
        iconContainer.getChildren().add(mainIcon);

        VBox titleBox = new VBox(2);
        Label title = new Label(titleStr);
        title.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold;");
        Label sub = new Label(subStr);
        sub.setStyle("-fx-text-fill: #A7A7A7; -fx-font-size: 10px;");
        titleBox.getChildren().addAll(title, sub);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Indicador (ponto colorido)
        Circle indicator = new Circle(4, Color.web(colorHex));
        
        FontIcon arrow = new FontIcon(FontAwesomeSolid.CHEVRON_DOWN);
        arrow.setIconColor(Color.web("#333333"));
        arrow.setIconSize(12);

        top.getChildren().addAll(iconContainer, titleBox, spacer, indicator, arrow);
        card.getChildren().add(top);

        return card;
    }

    // Helper for Circle indicator
    private static class Circle extends javafx.scene.shape.Circle {
        public Circle(double radius, Color color) {
            super(radius, color);
            this.setOpacity(0.8);
            // Efeito de brilho
            this.setStyle("-fx-effect: dropshadow(three-pass-box, " + toRGBCode(color) + ", 10, 0, 0, 0);");
        }

        private String toRGBCode(Color color) {
            return String.format("#%02X%02X%02X",
                    (int) (color.getRed() * 255),
                    (int) (color.getGreen() * 255),
                    (int) (color.getBlue() * 255));
        }
    }
}
