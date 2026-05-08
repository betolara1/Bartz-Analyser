package com.bartz.analyzer.ui;

// ============================================================
// MainLayout.java - Layout principal do Dashboard
// ============================================================
// Este é o componente "raiz" que monta toda a tela do dashboard.
// Ele usa um BorderPane como estrutura principal:
//
//   - TOP:    HeaderBar (barra superior)
//   - CENTER: ScrollPane contendo KPIs + Filtros + Tabela
//
// CONCEITO: BorderPane
// --------------------
// BorderPane divide a tela em 5 regiões:
//   ┌──────────────────────┐
//   │        TOP           │
//   ├──────┬───────┬───────┤
//   │ LEFT │ CENTER│ RIGHT │
//   ├──────┴───────┴───────┤
//   │       BOTTOM         │
//   └──────────────────────┘
//
// Neste app, usamos apenas TOP e CENTER.
// ============================================================

// --- IMPORTS ---

// BorderPane: layout com 5 regiões (top, center, left, right, bottom)
import javafx.scene.layout.BorderPane;

// VBox: layout vertical (filhos empilhados)
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
// HBox: layout horizontal
import javafx.scene.layout.HBox;

// Priority: controle de crescimento
import javafx.scene.layout.Priority;
// ScrollPane: container com scroll (barra de rolagem)
// Usado quando o conteúdo pode ser maior que a janela
import javafx.scene.control.ScrollPane;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
// Insets: padding nos 4 lados
import javafx.geometry.Insets;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// FontAwesomeSolid: pack de ícones FontAwesome 5
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import com.bartz.analyzer.service.AnalyserService;
import com.bartz.analyzer.service.ArquivoService;
import com.bartz.analyzer.service.ConfigService;

/**
 * MainLayout — Monta a tela inteira do dashboard.
 *
 * Estrutura visual completa:
 * ┌──────────────────────────────────────────────────────────┐
 * │ [B] Bartz Verificador XML [▶ Iniciar] [⟳] [⚙] │ ← HeaderBar
 * ├──────────────────────────────────────────────────────────┤
 * │ │
 * │ ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐ │ ← KPI Cards
 * │ │ 156 │ │ 142 │ │ 12 │ │ 2 │ │
 * │ │Receb.│ │Corr. │ │Incon.│ │Ferr. │ │
 * │ └──────┘ └──────┘ └──────┘ └──────┘ │
 * │ │
 * │ [▼ Todos] [🔍 Buscar...] 📅 Últimas 24h │ ← FilterBar
 * │ │
 * │ ┌────────┬────────┬──────┬──────┬──────┬──────┐ │ ← FileTable
 * │ │Arquivo │Status │Erros │Fix │Data │Ações │ │
 * │ ├────────┼────────┼──────┼──────┼──────┼──────┤ │
 * │ │ ... │ ... │ ... │ ... │ ... │ ... │ │
 * │ └────────┴────────┴──────┴──────┴──────┴──────┘ │
 * │ │
 * └──────────────────────────────────────────────────────────┘
 */
public class MainLayout extends BorderPane {
    // MainLayout herda de BorderPane.
    // BorderPane é o layout ideal para telas com "cabeçalho + conteúdo".

    // Referências aos componentes (para o backend usar futuramente)
    private final HeaderBar headerBar;
    private final ScrollPane dashboardContent;
    private final SettingsLayout settingsContent;
    private boolean showingSettings = false;
    private boolean isMonitoring = false;

    private final KpiCard kpiRecebidos;
    private final KpiCard kpiCorretos;
    private final KpiCard kpiInconformidades;
    private final KpiCard kpiFerragens;
    private final FilterBar filterBar;
    private final FileTable fileTable;

    private final AnalyserService analyserService;
    private final ArquivoService arquivoService;
    private final FilteredList<FileTable.FileRow> filteredList;

    /**
     * Construtor — monta o layout inteiro.
     */
    public MainLayout(AnalyserService analyserService) {
        this.analyserService = analyserService;
        this.arquivoService = new ArquivoService();

        // === 1. HEADER (topo da tela) ===

        // Cria a barra superior com logo, título e botões
        headerBar = new HeaderBar();

        // setTop() coloca o header na região TOP do BorderPane.
        // Ele fica fixo no topo, mesmo quando rola o conteúdo.
        this.setTop(headerBar);

        // === 2. KPI CARDS (indicadores numéricos) ===

        // Cria 4 cards de KPI com ícones e cores diferentes.
        // Os valores são mock (fictícios) — futuramente virão do backend.

        // Card "Recebidos": ícone de arquivo, cor azul (#3498DB)
        kpiRecebidos = new KpiCard(
                "Recebidos", // título
                0, // valor inicial real
                FontAwesomeSolid.FILE_ALT, // ícone
                "#3498DB" // cor azul
        );

        // Card "Corretos": ícone de check, cor verde (#27AE60)
        kpiCorretos = new KpiCard(
                "Corretos",
                0,
                FontAwesomeSolid.CHECK_CIRCLE,
                "#27AE60");

        // Card "Inconformidades": ícone de X, cor vermelha (#E74C3C)
        kpiInconformidades = new KpiCard(
                "Inconformidades",
                0,
                FontAwesomeSolid.TIMES_CIRCLE,
                "#E74C3C");

        // Card "Ferragens-only": ícone de caixa, cor laranja (#F39C12)
        kpiFerragens = new KpiCard(
                "Ferragens-only",
                0,
                FontAwesomeSolid.BOX,
                "#F39C12");

        // HBox para colocar os 4 cards lado a lado
        HBox kpiRow = new HBox(16);
        // 16px de espaço entre cards

        // Adiciona os 4 cards ao HBox
        kpiRow.getChildren().addAll(
                kpiRecebidos,
                kpiCorretos,
                kpiInconformidades,
                kpiFerragens);

        // Padding no HBox dos KPIs
        kpiRow.setPadding(new Insets(0));

        // === 3. FILTROS (barra de busca e filtros) ===

        filterBar = new FilterBar();
        
        // Escuta o campo de busca (cada letra digitada)
        filterBar.getSearchField().textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        // Escuta o ComboBox (cada mudança de seleção)
        filterBar.getStatusFilter().valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        // === 4. TABELA (lista de arquivos) ===

        fileTable = new FileTable();

        // VARIAVEIS PARA O FILTRO
        this.filteredList = new FilteredList<>(fileTable.getData(), p -> true);
        SortedList<FileTable.FileRow> sortedData = new SortedList<>(filteredList);
        sortedData.comparatorProperty().bind(fileTable.getTable().comparatorProperty());
        // AQUI Conecta a tabela à lista inteligente (filtrada e organizada)
        fileTable.getTable().setItems(sortedData);

        // Faz a tabela crescer verticalmente para ocupar todo o espaço restante
        VBox.setVgrow(fileTable, Priority.ALWAYS);

        // === 5. CONTENT AREA (área de conteúdo central) ===

        // VBox que empilha: KPIs → Filtros → Tabela
        VBox contentArea = new VBox(20);
        // 20px de espaço entre cada seção

        // Padding ao redor do conteúdo (24px em todos os lados)
        contentArea.setPadding(new Insets(24));

        // Adiciona os 3 componentes na ordem vertical
        contentArea.getChildren().addAll(
                kpiRow, // Topo: KPI cards
                filterBar, // Meio: filtros
                fileTable // Baixo: tabela (cresce)
        );

        // Faz o contentArea crescer dentro do ScrollPane
        VBox.setVgrow(fileTable, Priority.ALWAYS);

        // === 6. SCROLLPANE (permitir scroll no conteúdo) ===

        // ScrollPane adiciona barras de rolagem quando o conteúdo
        // é maior que a área visível (como overflow: auto no CSS)
        ScrollPane scrollPane = new ScrollPane(contentArea);

        // setFitToWidth(true) = o conteúdo se estica para preencher a largura.
        // Sem isso, o conteúdo ficaria na largura mínima e não preencheria.
        scrollPane.setFitToWidth(true);

        // setFitToHeight(true) = o conteúdo se estica para preencher a altura.
        scrollPane.setFitToHeight(true);

        // Remove a borda padrão do ScrollPane
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        // Configura o comportamento do scroll:
        // AS_NEEDED = mostra a scrollbar apenas quando necessário
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // nunca scroll horizontal
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // vertical quando necessário

        // setCenter() coloca o ScrollPane na região CENTER do BorderPane.
        // CENTER ocupa todo o espaço restante (depois de TOP, BOTTOM, LEFT, RIGHT).
        this.setCenter(scrollPane);
        this.dashboardContent = scrollPane;
        this.settingsContent = new SettingsLayout();

        // --- LÓGICA DE TROCA DE TELAS ---
        headerBar.getCaminhosButton().setOnAction(e -> {
            if (showingSettings) {
                // Se está nas configurações, volta pro Dashboard
                this.setCenter(dashboardContent);
                headerBar.getCaminhosButton().setText("Caminhos");
            } else {
                // Se está no Dashboard, vai pras Configurações
                this.setCenter(settingsContent);
                headerBar.getCaminhosButton().setText("Dashboard");
            }
            showingSettings = !showingSettings;
        });

        // LOGICA DO BOTÃO START INICIAL
        headerBar.getStartButton().setOnAction(e -> {
            // O botão start vai receber o botão pausar
            if (!isMonitoring) {
                isMonitoring = true;

                headerBar.getStartButton().setText("Parar");

                // 2. Muda a Cor (Tira o verde 'btn-success', coloca o vermelho 'btn-danger')
                headerBar.getStartButton().getStyleClass().remove("btn-success");
                headerBar.getStartButton().getStyleClass().add("btn-danger");

                // 3. Muda o Ícone (de Play para Stop)
                FontIcon stopIcon = new FontIcon(FontAwesomeSolid.STOP);
                stopIcon.setIconColor(Color.WHITE);
                headerBar.getStartButton().setGraphic(stopIcon);

                // Procura o caminho configurado
                String inputPath = ConfigService.getValue(ConfigService.PATH_INPUT);

                if (inputPath == null || inputPath.isEmpty()) {
                    System.out.print("Caminho não encontrado");
                    return;
                }

                File dir = new File(inputPath);

                // Verifica se a pasta existe
                if (dir.exists() && dir.isDirectory()) {
                    // Busca os arquivos .xml
                    File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".xml"));

                    if (files != null) {
                        // Limpa a tabela pra adicionar novos arquivos
                        fileTable.getData().clear();

                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM HH:mm");

                        for (File file : files) {
                            String status = analyserService.processarStatus(file);
                            String erro = analyserService.processarErro(file);
                            System.out.println(status);
                            System.out.println(erro);

                            fileTable.getData().add(new FileTable.FileRow(
                                    file.getName(),
                                    status,
                                    erro,
                                    "",
                                    LocalDateTime.now().format(formatter)));
                            arquivoService.monitorarArquivos(inputPath);
                        }
                    }
                } else {
                    System.out.println("Pasta não encontrada: " + inputPath);
                }
            } else {
                // --- AÇÃO: VAI PARAR ---
                isMonitoring = false;

                // Volta tudo ao original
                headerBar.getStartButton().setText("Iniciar");
                headerBar.getStartButton().getStyleClass().remove("btn-danger");
                headerBar.getStartButton().getStyleClass().add("btn-success");

                FontIcon playIcon = new FontIcon(FontAwesomeSolid.PLAY);
                playIcon.setIconColor(Color.WHITE);
                headerBar.getStartButton().setGraphic(playIcon);
            }

            // Define o fundo do layout principal
            this.setStyle("-fx-background-color: #111111;");
        });
    }

    // === MÉTODOS PÚBLICOS ===
    // Para o backend acessar os componentes e conectar lógica.

    /** Retorna o HeaderBar para acessar botões */
    public HeaderBar getHeaderBar() {
        return headerBar;
    }

    /** Retorna o KPI de "Recebidos" */
    public KpiCard getKpiRecebidos() {
        return kpiRecebidos;
    }

    /** Retorna o KPI de "Corretos" */
    public KpiCard getKpiCorretos() {
        return kpiCorretos;
    }

    /** Retorna o KPI de "Inconformidades" */
    public KpiCard getKpiInconformidades() {
        return kpiInconformidades;
    }

    /** Retorna o KPI de "Ferragens" */
    public KpiCard getKpiFerragens() {
        return kpiFerragens;
    }

    /** Retorna a FilterBar para acessar filtros */
    public FilterBar getFilterBar() {
        return filterBar;
    }

    /** Retorna o FileTable para acessar a tabela */
    public FileTable getFileTable() {
        return fileTable;
    }

    private void applyFilters(){
        String searchText = filterBar.getSearchField().getText().toLowerCase();
        String statusFilter = filterBar.getStatusFilter().getValue();

        filteredList.setPredicate(row -> {
            // filtro do texto
            boolean matchesSearch = searchText == null || searchText.isEmpty() || 
                                    row.getFilename().toLowerCase().contains(searchText);

             // Filtro de Status
            boolean matchesStatus = statusFilter == null || statusFilter.equals("Todos") ||
                                    row.getStatus().equalsIgnoreCase(statusFilter);

            // Retorna TRUE se passar nas duas regras
            return matchesSearch && matchesStatus;
        });
    }
}
