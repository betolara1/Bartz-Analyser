package com.bartz.analyzer.ui;

// ============================================================
// KpiCard.java - Card de KPI (Key Performance Indicator)
// ============================================================
// Este componente cria um "cartão" visual que mostra uma métrica,
// como por exemplo: "Corretos: 142". É reutilizável — você pode
// criar vários cards com cores e ícones diferentes.
// ============================================================

// --- IMPORTS DO JAVAFX ---

// Label: componente de texto (como um <span> no HTML)
import javafx.scene.control.Label;

// HBox: layout horizontal (filhos lado a lado, como flexbox row)
import javafx.scene.layout.*;
import javafx.geometry.Insets;
// Pos: enum com posições de alinhamento (CENTER, TOP_LEFT, etc.)
import javafx.geometry.Pos;

// Color: representa uma cor RGB(A) — usada para pintar ícones
import javafx.scene.paint.Color;

// FontIcon: componente do Ikonli que renderiza ícones como texto
// (parecido com FontAwesome no HTML)
import org.kordamp.ikonli.javafx.FontIcon;

// Ikon: interface que representa um ícone específico do Ikonli
import org.kordamp.ikonli.Ikon;

/**
 * KpiCard — Um card visual que mostra um indicador numérico.
 *
 * Estrutura visual:
 * ┌─────────────────────────────────┐
 * │  [🔵]  Recebidos                │
 * │        156                      │
 * └─────────────────────────────────┘
 *
 * Componentes internos:
 * - HBox (layout horizontal principal)
 *   - StackPane (container do ícone com fundo colorido)
 *   - VBox (título + valor, empilhados verticalmente)
 */
public class KpiCard extends HBox {
    private final Label valueLabel;
    private final Label titleLabel;
    private final StackPane iconContainer;
    private final FontIcon fontIcon;
    private final Circle activeIndicator;

    public KpiCard(String title, int value, Ikon icon, String color) {
        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(16);
        this.setPadding(new Insets(16, 20, 16, 20));
        this.getStyleClass().add("kpi-card");
        this.setPrefWidth(220);
        this.setMinWidth(180);
        this.setPrefHeight(80); // Altura fixa como solicitado

        // --- 1. ÍCONE E CONTAINER ---
        fontIcon = new FontIcon(icon);
        fontIcon.setIconSize(20);
        fontIcon.setIconColor(Color.web(color));

        iconContainer = new StackPane(fontIcon);
        iconContainer.setPrefSize(40, 40);
        iconContainer.setMinSize(40, 40);
        iconContainer.setMaxSize(40, 40);
        iconContainer.setStyle("-fx-background-color: " + color + "1A; -fx-background-radius: 8;");
        iconContainer.setAlignment(Pos.CENTER);

        // --- 2. TÍTULO E VALOR EM VBOX ---
        titleLabel = new Label(title.toUpperCase());
        titleLabel.setStyle("-fx-text-fill: #A7A7A7; -fx-font-size: 10px; -fx-font-weight: bold; -fx-letter-spacing: 0.5px;");

        valueLabel = new Label(String.valueOf(value));
        valueLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");

        VBox textContainer = new VBox(2);
        textContainer.setAlignment(Pos.CENTER_LEFT);
        textContainer.getChildren().addAll(titleLabel, valueLabel);

        // --- 3. INDICADOR ATIVO ---
        activeIndicator = new Circle(3, Color.web("#3498DB"));
        activeIndicator.setVisible(false);
        activeIndicator.setOpacity(0.8);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // --- 4. MONTAGEM ---
        this.getChildren().addAll(iconContainer, textContainer, spacer, activeIndicator);

        // Hover Effect
        this.setOnMouseEntered(e -> {
            if (!isActive()) {
                this.setStyle("-fx-border-color: #404040; -fx-background-color: #222222;");
            }
        });
        this.setOnMouseExited(e -> {
            if (!isActive()) {
                this.setStyle("");
            }
        });

        HBox.setHgrow(this, Priority.ALWAYS);
    }

    public void setActive(boolean active) {
        if (active) {
            this.setStyle("-fx-border-color: #3498DB; -fx-background-color: #1B1B1B; -fx-effect: dropshadow(three-pass-box, rgba(52, 152, 219, 0.1), 10, 0, 0, 0);");
            activeIndicator.setVisible(true);
            activeIndicator.setFill(Color.web("#3498DB"));
            activeIndicator.setStyle("-fx-effect: dropshadow(three-pass-box, #3498DB, 10, 0.5, 0, 0);");
        } else {
            this.setStyle("");
            activeIndicator.setVisible(false);
        }
    }

    public boolean isActive() {
        return activeIndicator.isVisible();
    }

    public void setValue(int newValue) {
        valueLabel.setText(String.valueOf(newValue));
    }

    public int getValue() {
        return Integer.parseInt(valueLabel.getText());
    }

    private static class Circle extends javafx.scene.shape.Circle {
        public Circle(double radius, Color color) {
            super(radius, color);
        }
    }
}
