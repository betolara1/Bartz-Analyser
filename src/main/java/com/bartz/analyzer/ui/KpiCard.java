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
import javafx.scene.layout.HBox;

// VBox: layout vertical (filhos empilhados, como flexbox column)
import javafx.scene.layout.VBox;

// StackPane: empilha filhos um sobre o outro (como position: absolute)
import javafx.scene.layout.StackPane;

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
public class KpiCard extends VBox {
    // "extends VBox" significa que KpiCard É um VBox.
    // VBox é um layout que organiza filhos verticalmente.
    // Herdando dele, nosso KpiCard pode ser adicionado a qualquer layout
    // como se fosse um VBox normal.

    // --- PROPRIEDADES DO CARD ---

    // Label que mostra o valor numérico (ex: "156")
    private final Label valueLabel;

    // Label que mostra o título (ex: "Recebidos")
    private final Label titleLabel;

    /**
     * Construtor do KpiCard.
     *
     * @param title  Texto do título (ex: "Recebidos")
     * @param value  Valor numérico inicial (ex: 156)
     * @param icon   Ícone do Ikonli (ex: FontAwesomeSolid.FILE_ALT)
     * @param color  Cor do ícone e do fundo do ícone (ex: "#3498DB")
     */
    public KpiCard(String title, int value, Ikon icon, String color) {

        // --- 1. CRIAR O ÍCONE ---

        // FontIcon cria um ícone vetorial a partir do pack Ikonli.
        // É como usar <i class="fas fa-file-alt"></i> no HTML.
        FontIcon fontIcon = new FontIcon(icon);

        // Define o tamanho do ícone em pixels
        fontIcon.setIconSize(20);

        // Define a cor do ícone usando Color.web() que aceita hex como "#3498DB"
        fontIcon.setIconColor(Color.web(color));

        // --- 2. CONTAINER DO ÍCONE (quadrado colorido atrás do ícone) ---

        // StackPane empilha seus filhos. Aqui, o ícone fica centralizado
        // dentro de um quadrado com fundo colorido (como um div com background).
        StackPane iconContainer = new StackPane(fontIcon);

        // Adiciona a classe CSS "kpi-icon-container" para estilizar via CSS
        iconContainer.getStyleClass().add("kpi-icon-container");

        // Define o fundo do container do ícone.
        // Usamos a cor com 20% de opacidade (o "33" no final é ~20% em hex).
        // Isso cria um efeito sutil de fundo, como "background: rgba(52,152,219,0.2)"
        iconContainer.setStyle(
            "-fx-background-color: " + color + "33;"  // cor + 33 = 20% opacidade
        );

        // Define tamanho fixo do container do ícone (36x36 pixels)
        iconContainer.setPrefSize(36, 36);
        iconContainer.setMinSize(36, 36);
        iconContainer.setMaxSize(36, 36);

        // --- 3. LABEL DO TÍTULO ---

        // Label é o componente de texto do JavaFX (como <label> no HTML)
        titleLabel = new Label(title);

        // Adiciona a classe CSS "kpi-title" para estilizar (cor cinza, fonte pequena)
        titleLabel.getStyleClass().add("kpi-title");

        // --- 4. LABEL DO VALOR ---

        valueLabel = new Label(String.valueOf(value));

        // String.valueOf(value) converte o int para String.
        // Ex: 156 → "156"

        // Adiciona a classe CSS "kpi-value" para estilizar (fonte grande, branco, bold)
        valueLabel.getStyleClass().add("kpi-value");

        // --- 5. VBOX: Empilha título + valor verticalmente ---

        // VBox organiza filhos de cima para baixo.
        // Aqui: título em cima, valor embaixo.
        VBox textBox = new VBox(2);
        // O "2" é o spacing: 2 pixels de espaço entre título e valor

        // Adiciona os filhos ao VBox (ordem importa: primeiro = topo)
        textBox.getChildren().addAll(titleLabel, valueLabel);

        // --- 6. HBOX: Coloca ícone + textos lado a lado ---

        // HBox organiza filhos da esquerda para a direita.
        // Aqui: ícone à esquerda, textos à direita.
        HBox content = new HBox(12);
        // O "12" é o spacing: 12 pixels de espaço entre ícone e textos

        // Alinha verticalmente ao centro (ícone e texto ficam na mesma altura)
        content.setAlignment(Pos.CENTER_LEFT);

        // Adiciona ícone e textos ao HBox
        content.getChildren().addAll(iconContainer, textBox);

        // --- 7. CONFIGURAR O CARD (this = o próprio VBox que herdamos) ---

        // Adiciona o HBox como filho deste VBox
        this.getChildren().add(content);

        // Adiciona a classe CSS "kpi-card" para estilizar (borda, fundo, radius)
        this.getStyleClass().add("kpi-card");

        // Define que o card deve expandir horizontalmente para preencher o espaço
        // HBox.setHgrow() diz ao layout pai para dar espaço extra para este nó
        HBox.setHgrow(this, javafx.scene.layout.Priority.ALWAYS);
    }

    // --- MÉTODOS PÚBLICOS ---

    /**
     * Atualiza o valor exibido no card.
     * Futuramente, quando tiver backend, você pode chamar este método
     * para atualizar o número de arquivos processados, por exemplo.
     *
     * @param newValue novo valor numérico
     */
    public void setValue(int newValue) {
        // setText() muda o texto de um Label — como element.textContent no JS
        valueLabel.setText(String.valueOf(newValue));
    }

    /**
     * Retorna o valor atual exibido no card.
     *
     * @return valor como inteiro
     */
    public int getValue() {
        // getText() retorna o texto atual do Label
        return Integer.parseInt(valueLabel.getText());
    }
}
