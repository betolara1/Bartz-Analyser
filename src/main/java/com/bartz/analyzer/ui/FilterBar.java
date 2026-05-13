package com.bartz.analyzer.ui;

// ============================================================
// FilterBar.java - Barra de filtros
// ============================================================
// Este componente cria a barra abaixo dos KPIs com:
// - ComboBox para filtrar por status (Todos, OK, Erro, Ferragens)
// - TextField para buscar por nome do arquivo
// - Label indicando o período (Últimas 24h)
//
// Equivale ao <div className="flex gap-4 mb-6"> do EnhancedDashboard
// ============================================================

// --- IMPORTS ---

// ComboBox: dropdown de seleção (como <select> no HTML)
import javafx.scene.control.ComboBox;

// TextField: campo de texto editável (como <input type="text"> no HTML)
import javafx.scene.control.TextField;

// Label: texto estático
import javafx.scene.control.Label;
import javafx.scene.control.Button;

// HBox: layout horizontal
import javafx.scene.layout.HBox;

// Region: espaçador invisível
import javafx.scene.layout.Region;

// Priority: controle de crescimento no layout
import javafx.scene.layout.Priority;

// Pos: alinhamento
import javafx.geometry.Pos;

// FontIcon: ícone vetorial
import org.kordamp.ikonli.javafx.FontIcon;

// FontAwesomeSolid: pack de ícones
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

// Color: cor para ícones
import javafx.scene.paint.Color;

/**
 * FilterBar — Barra de filtros do dashboard.
 *
 * Estrutura visual:
 * ┌──────────────────────────────────────────────────────────┐
 * │ [▼ Todos]   [🔍 Buscar por nome do arquivo...]  📅 24h  │
 * └──────────────────────────────────────────────────────────┘
 *
 * Internamente é um HBox com:
 * - ComboBox (dropdown de filtro)
 * - TextField (campo de busca)
 * - Label com ícone (período)
 */
public class FilterBar extends HBox {
    // FilterBar herda de HBox — é um layout horizontal.

    private final ComboBox<String> statusFilter;
    private final TextField searchField;
    private final Label countLabel;

    public FilterBar() {
        this.setSpacing(12);
        this.setAlignment(Pos.CENTER_LEFT);
        this.getStyleClass().add("filter-bar");

        // --- 1. CAMPO DE BUSCA COM ÍCONE ---
        HBox searchContainer = new HBox(10);
        searchContainer.setAlignment(Pos.CENTER_LEFT);
        searchContainer.getStyleClass().add("search-container");
        searchContainer.setStyle("-fx-background-color: #1B1B1B; -fx-border-color: #2C2C2C; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 0 12;");
        searchContainer.setPrefWidth(300);

        FontIcon searchIcon = new FontIcon(FontAwesomeSolid.SEARCH);
        searchIcon.setIconSize(14);
        searchIcon.setIconColor(Color.web("#A7A7A7"));

        searchField = new TextField();
        searchField.setPromptText("Buscar arquivo...");
        searchField.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-text-fill: white; -fx-padding: 10 0;");
        HBox.setHgrow(searchField, Priority.ALWAYS);

        searchContainer.getChildren().addAll(searchIcon, searchField);

        // --- 2. BOTÃO DE DATA ---
        Button btnDate = new Button("Últimas 24h");
        btnDate.setGraphic(new FontIcon(FontAwesomeSolid.CALENDAR_ALT));
        btnDate.setStyle("-fx-background-color: #1B1B1B; -fx-border-color: #2C2C2C; -fx-text-fill: #A7A7A7; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8 16; -fx-font-size: 11px;");

        // --- 3. COMBOBOX (ESCONDIDO POR PADRÃO OU USADO PARA LÓGICA) ---
        statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("Todos", "Ok", "Erro", "Ferragens", "Muxarabi", "Coringa", "Duplado", "Sem Código");
        statusFilter.setValue("Todos");
        statusFilter.setVisible(false); // Escondemos para usar a lógica dos KPI cards
        statusFilter.setManaged(false);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // --- 4. CONTADOR ---
        countLabel = new Label("MOSTRANDO 0 DE 0 ARQUIVOS");
        countLabel.setStyle("-fx-text-fill: #A7A7A7; -fx-font-size: 10px; -fx-font-weight: bold; -fx-letter-spacing: 1px; -fx-background-color: #1B1B1B; -fx-padding: 8 16; -fx-background-radius: 20;");

        this.getChildren().addAll(searchContainer, btnDate, spacer, countLabel);
    }

    public void updateCount(int current, int total) {
        countLabel.setText("MOSTRANDO " + current + " DE " + total + " ARQUIVOS");
    }

    // === MÉTODOS PÚBLICOS ===
    // Para o backend usar futuramente.

    /**
     * Retorna o ComboBox de filtro de status.
     * Use: getStatusFilter().getValue() para saber o filtro selecionado.
     * Use: getStatusFilter().setOnAction(e -> { ... }) para reagir a mudanças.
     */
    public ComboBox<String> getStatusFilter() {
        return statusFilter;
    }

    /**
     * Retorna o TextField de busca.
     * Use: getSearchField().getText() para pegar o texto digitado.
     * Use: getSearchField().textProperty().addListener(...) para reagir em tempo real.
     *
     * textProperty() retorna uma "Property" — um valor observável do JavaFX.
     * Quando o texto muda, o listener é chamado automaticamente.
     * Isso é chamado de "binding" ou "data binding reativo".
     */
    public TextField getSearchField() {
        return searchField;
    }
}
