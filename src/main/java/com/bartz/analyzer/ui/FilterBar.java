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

    // Referências para acessar os valores futuramente
    private final ComboBox<String> statusFilter;
    private final TextField searchField;

    /**
     * Construtor — monta a barra de filtros.
     */
    public FilterBar() {

        // === COMBOBOX DE STATUS ===

        // ComboBox<String> é um dropdown que contém Strings.
        // O <String> é um "generic" — define o tipo dos itens da lista.
        statusFilter = new ComboBox<>();

        // getItems() retorna a lista de opções do dropdown.
        // addAll() adiciona várias opções de uma vez.
        statusFilter.getItems().addAll(
            "Todos",           // Mostra todos os arquivos
            "Ok",              // Somente arquivos corretos
            "Erro",            // Somente com erros
            "Ferragens"   // Somente com ferragens
        );

        // setValue() define o valor inicial selecionado
        statusFilter.setValue("Todos");

        // setPrefWidth() define a largura preferida do componente em pixels
        statusFilter.setPrefWidth(180);

        // Adiciona classe CSS para estilizar (fundo escuro, borda)
        statusFilter.getStyleClass().add("combo-dark");

        // === TEXTFIELD DE BUSCA ===

        // TextField é um campo de texto editável.
        searchField = new TextField();

        // setPromptText() define o texto "placeholder" (cinza, desaparece ao digitar)
        // É como o atributo placeholder="" do HTML
        searchField.setPromptText("Buscar por nome do arquivo...");

        // Define largura preferida (350px)
        searchField.setPrefWidth(350);

        // Adiciona classe CSS para estilizar
        searchField.getStyleClass().add("search-field");

        // === LABEL DE PERÍODO ===

        // Ícone de calendário
        FontIcon calendarIcon = new FontIcon(FontAwesomeSolid.CALENDAR_ALT);
        calendarIcon.setIconSize(14);
        calendarIcon.setIconColor(Color.web("#A7A7A7"));

        // Label com o texto do período
        Label periodLabel = new Label("Últimas 24h");
        periodLabel.getStyleClass().add("text-muted");

        // Agrupa ícone + label em um HBox
        HBox periodGroup = new HBox(6);
        // 6px de espaço entre ícone e texto

        periodGroup.setAlignment(Pos.CENTER_LEFT);
        periodGroup.getChildren().addAll(calendarIcon, periodLabel);

        // === ESPAÇADOR ===
        // Empurra o label do período para a direita
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // === MONTAR A BARRA ===

        // Adiciona todos os componentes ao HBox (this)
        this.getChildren().addAll(statusFilter, searchField, spacer, periodGroup);

        // Define o espaço entre componentes: 16 pixels
        this.setSpacing(16);

        // Alinha verticalmente ao centro
        this.setAlignment(Pos.CENTER_LEFT);

        // Adiciona a classe CSS "filter-bar"
        this.getStyleClass().add("filter-bar");
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
