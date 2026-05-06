package com.bartz.analyzer.ui;

// ============================================================
// HeaderBar.java - Barra superior da aplicação
// ============================================================
// Este componente cria a barra no topo da janela, contendo:
// - Logo (quadrado amarelo com "B")
// - Título "Bartz Verificador XML"
// - Botões de ação (Iniciar, Reanalisar, Configurações)
//
// Equivale ao <div className="border-b border-border bg-card">
// do componente React Dashboard.tsx
// ============================================================

// --- IMPORTS ---

// Button: botão clicável (como <button> no HTML)
import javafx.scene.control.Button;

// Label: texto estático (como <span> no HTML)
import javafx.scene.control.Label;

// Tooltip: dica que aparece ao passar o mouse sobre um componente
import javafx.scene.control.Tooltip;

// HBox: layout horizontal (filhos lado a lado)
import javafx.scene.layout.HBox;

// Region: "espaçador" invisível — usado para empurrar elementos
import javafx.scene.layout.Region;

// Priority: define como um nó cresce dentro de um layout
// ALWAYS = cresce para preencher todo espaço disponível
import javafx.scene.layout.Priority;

// StackPane: empilha filhos (usado para o logo)
import javafx.scene.layout.StackPane;

// Pos: posições de alinhamento
import javafx.geometry.Pos;

// FontIcon: ícone vetorial do Ikonli
import org.kordamp.ikonli.javafx.FontIcon;

// FontAwesomeSolid: pack de ícones FontAwesome 5 (versão sólida)
// Contém ícones como PLAY, PAUSE, COG (engrenagem), SYNC, etc.
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

// Color: cor para pintar ícones
import javafx.scene.paint.Color;

/**
 * HeaderBar — Barra superior do dashboard.
 *
 * Estrutura visual:
 * ┌──────────────────────────────────────────────────────────┐
 * │ [B] Bartz Verificador XML        [▶ Iniciar] [⟳] [⚙]  │
 * └──────────────────────────────────────────────────────────┘
 *
 * Internamente é um HBox com:
 * - Logo + Título à esquerda
 * - Region (espaçador) no meio
 * - Botões à direita
 */
public class HeaderBar extends HBox {
    // HeaderBar herda de HBox, então é um layout horizontal.
    // Tudo dentro dele fica lado a lado.

    // Referências aos botões (para futuramente adicionar ações)
    private final Button btnCaminhos;
    private final Button btnStart;
    private final Button btnReanalyze;
    private final Button btnReanalyzeErrors;
    private final Button btnSettings;

    /**
     * Construtor — monta toda a barra.
     */
    public HeaderBar() {

        // === PARTE ESQUERDA: Logo + Título ===

        // --- Logo: Quadrado amarelo com a letra "B" ---

        // Label com o texto "B" (o logo da Bartz)
        Label logoText = new Label("B");

        // Adiciona a classe CSS "logo-text" (cor preta, negrito)
        logoText.getStyleClass().add("logo-text");

        // StackPane para criar o quadrado amarelo atrás do "B"
        // StackPane centraliza automaticamente seus filhos
        StackPane logoBox = new StackPane(logoText);

        // Adiciona a classe CSS "logo-box" (fundo amarelo #F1C40F, radius)
        logoBox.getStyleClass().add("logo-box");

        // --- Título da aplicação ---
        Label appTitle = new Label("Bartz Verificador XML");
        appTitle.getStyleClass().add("app-title");

        // --- Botão Caminhos (NOVO) ---
        btnCaminhos = new Button("Caminhos");
        btnCaminhos.getStyleClass().add("btn-ghost");
        FontIcon pathIcon = new FontIcon(FontAwesomeSolid.FOLDER);
        pathIcon.setIconColor(Color.web("#A7A7A7"));
        btnCaminhos.setGraphic(pathIcon);

        // HBox para agrupar logo + título + botão
        HBox leftGroup = new HBox(16);
        leftGroup.setAlignment(Pos.CENTER_LEFT);
        leftGroup.getChildren().addAll(logoBox, appTitle, btnCaminhos);

        // === ESPAÇADOR CENTRAL ===
        // Region invisível que "empurra" os botões para a direita.
        // É como usar flex-grow: 1 no CSS.
        Region spacer = new Region();

        // HBox.setHgrow() diz que este Region deve crescer
        // para ocupar Todo o espaço disponível entre esquerda e direita.
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // === PARTE DIREITA: Botões de ação ===

        // --- Botão "Iniciar monitoramento" ---
        btnStart = new Button("Iniciar");

        // Cria um ícone de "play" (▶) do FontAwesome 5
        FontIcon playIcon = new FontIcon(FontAwesomeSolid.PLAY);
        playIcon.setIconSize(14);
        playIcon.setIconColor(Color.WHITE);

        // setGraphic() adiciona um ícone dentro do botão (à esquerda do texto)
        btnStart.setGraphic(playIcon);

        // Adiciona classe CSS "btn-success" (fundo verde)
        btnStart.getStyleClass().add("btn-success");

        // --- Botão "Reanalisar tudo" ---
        btnReanalyze = new Button("Reanalisar tudo");

        FontIcon syncIcon = new FontIcon(FontAwesomeSolid.SYNC);
        syncIcon.setIconSize(14);
        syncIcon.setIconColor(Color.WHITE);

        btnReanalyze.setGraphic(syncIcon);
        btnReanalyze.getStyleClass().add("btn-outline");

        // --- Botão "Reanalisar só erros" ---
        btnReanalyzeErrors = new Button("Reanalisar só erros");

        FontIcon redoIcon = new FontIcon(FontAwesomeSolid.REDO);
        redoIcon.setIconSize(14);
        redoIcon.setIconColor(Color.WHITE);

        btnReanalyzeErrors.setGraphic(redoIcon);
        btnReanalyzeErrors.getStyleClass().add("btn-outline");

        // --- Botão de Configurações (apenas ícone, sem texto) ---
        btnSettings = new Button();

        FontIcon cogIcon = new FontIcon(FontAwesomeSolid.COG);
        cogIcon.setIconSize(16);
        cogIcon.setIconColor(Color.web("#A7A7A7"));

        btnSettings.setGraphic(cogIcon);
        btnSettings.getStyleClass().add("btn-ghost");

        // Tooltip: texto que aparece ao passar o mouse (como title="" no HTML)
        btnSettings.setTooltip(new Tooltip("Configurações"));

        // --- Agrupar botões à direita ---
        HBox rightGroup = new HBox(8);
        // 8px de espaço entre botões

        rightGroup.setAlignment(Pos.CENTER_RIGHT);
        rightGroup.getChildren().addAll(btnStart, btnReanalyze, btnReanalyzeErrors, btnSettings);

        // === MONTAR O HEADER COMPLETO ===

        // Adiciona todos os elementos ao HBox principal (this)
        // Ordem: esquerda → espaçador → direita
        this.getChildren().addAll(leftGroup, spacer, rightGroup);

        // Alinha verticalmente ao centro (todos os filhos na mesma altura)
        this.setAlignment(Pos.CENTER_LEFT);

        // Adiciona a classe CSS "header-bar" (fundo #1B1B1B, borda inferior)
        this.getStyleClass().add("header-bar");
    }

    // === MÉTODOS PÚBLICOS ===
    // Futuramente, o backend pode usar esses métodos para
    // registrar ações nos botões.

    /**
     * Retorna o botão de caminhos.
     */
    public Button getCaminhosButton() {
        return btnCaminhos;
    }

    /**
     * Retorna o botão de iniciar/parar monitoramento.
     * Use getStartButton().setOnAction(e -> { ... }) para definir a ação.
     */
    public Button getStartButton() {
        return btnStart;
    }

    /**
     * Retorna o botão de reanalisar tudo.
     */
    public Button getReanalyzeButton() {
        return btnReanalyze;
    }

    /**
     * Retorna o botão de reanalisar só erros.
     */
    public Button getReanalyzeErrorsButton() {
        return btnReanalyzeErrors;
    }

    /**
     * Retorna o botão de configurações.
     */
    public Button getSettingsButton() {
        return btnSettings;
    }

    /**
     * Alterna o botão entre "Iniciar" (verde) e "Pausar" (vermelho).
     * Futuramente, chame este método quando o monitoramento iniciar/parar.
     *
     * @param monitoring true = está monitorando (mostrar "Pausar")
     */
    public void setMonitoringState(boolean monitoring) {
        if (monitoring) {
            // Muda o texto e a cor do botão para "Pausar" (vermelho)
            btnStart.setText("Pausar");

            // Remove classe verde e adiciona vermelha
            btnStart.getStyleClass().remove("btn-success");
            btnStart.getStyleClass().add("btn-danger");

            // Troca o ícone para "pause" (⏸)
            FontIcon pauseIcon = new FontIcon(FontAwesomeSolid.PAUSE);
            pauseIcon.setIconSize(14);
            pauseIcon.setIconColor(Color.WHITE);
            btnStart.setGraphic(pauseIcon);
        } else {
            // Volta para "Iniciar" (verde)
            btnStart.setText("Iniciar");

            btnStart.getStyleClass().remove("btn-danger");
            btnStart.getStyleClass().add("btn-success");

            FontIcon playIcon = new FontIcon(FontAwesomeSolid.PLAY);
            playIcon.setIconSize(14);
            playIcon.setIconColor(Color.WHITE);
            btnStart.setGraphic(playIcon);
        }
    }
}
