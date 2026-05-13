package com.bartz.analyzer.ui;

import javafx.scene.layout.BorderPane;

// VBox: layout vertical (filhos empilhados)
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

// Priority: controle de crescimento
import javafx.scene.layout.Priority;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.ColumnConstraints;
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
import com.bartz.analyzer.service.AnalyserService.AnaliseTags;

public class MainLayout extends BorderPane {
    // MainLayout herda de BorderPane.
    // BorderPane é o layout ideal para telas com "cabeçalho + conteúdo".

    // Referências aos componentes (para o backend usar futuramente)
    private final HeaderBar headerBar;
    private final ScrollPane dashboardContent;
    private final SettingsLayout settingsContent;
    private boolean showingSettings = false;
    private boolean isMonitoring = false;

    private final KpiCard kpiTodos;
    private final KpiCard kpiCorretos;
    private final KpiCard kpiInconformidades;
    private final KpiCard kpiFerragens;
    private final KpiCard kpiMuxarabi;
    private final KpiCard kpiCoringa;
    private final KpiCard kpiDuplado;
    private final KpiCard kpiSemCodigo;
    private final KpiCard kpiAutoFixed;
    
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
        headerBar = new HeaderBar();
        this.setTop(headerBar);

        filterBar = new FilterBar();

        // === 2. KPI CARDS (indicadores numéricos) ===
        kpiTodos = new KpiCard("Todos", 0, FontAwesomeSolid.FILTER, "#3498DB");
        kpiTodos.setActive(true); // Começa ativo

        kpiCorretos = new KpiCard("Corretos", 0, FontAwesomeSolid.CHECK_CIRCLE, "#27AE60");
        kpiInconformidades = new KpiCard("Inconformidades", 0, FontAwesomeSolid.TIMES_CIRCLE, "#E74C3C");
        kpiFerragens = new KpiCard("Ferragens-Only", 0, FontAwesomeSolid.BOX, "#F39C12");
        kpiMuxarabi = new KpiCard("Muxarabi", 0, FontAwesomeSolid.TH, "#9B59B6");
        
        kpiCoringa = new KpiCard("Cor Coringa", 0, FontAwesomeSolid.TH_LARGE, "#F39C12");
        kpiDuplado = new KpiCard("Duplado 37mm", 0, FontAwesomeSolid.EXCLAMATION_TRIANGLE, "#E67E22");
        kpiSemCodigo = new KpiCard("Sem Código", 0, FontAwesomeSolid.EXCLAMATION_CIRCLE, "#E74C3C");
        kpiAutoFixed = new KpiCard("Auto-Fixed", 0, FontAwesomeSolid.BOLT, "#1ABC9C");

        // Agrupando cards para lógica de seleção
        KpiCard[] allCards = {kpiTodos, kpiCorretos, kpiInconformidades, kpiFerragens, kpiMuxarabi, 
                             kpiCoringa, kpiDuplado, kpiSemCodigo, kpiAutoFixed};

        for (KpiCard card : allCards) {
            card.setOnMouseClicked(e -> {
                for (KpiCard c : allCards) c.setActive(false);
                card.setActive(true);
                
                // Filtra a tabela pelo título do card
                String filterValue = card.isActive() ? getFilterValueFromTitle(allCards, card) : "Todos";
                filterBar.getStatusFilter().setValue(filterValue);
            });
        }

        // GridPane para organizar 2 linhas de 5 colunas
        GridPane kpiGrid = new GridPane();
        kpiGrid.setHgap(16);
        kpiGrid.setVgap(16);

        // Primeira Linha
        kpiGrid.add(kpiTodos, 0, 0);
        kpiGrid.add(kpiCorretos, 1, 0);
        kpiGrid.add(kpiInconformidades, 2, 0);
        kpiGrid.add(kpiFerragens, 3, 0);
        kpiGrid.add(kpiMuxarabi, 4, 0);

        // Segunda Linha
        kpiGrid.add(kpiCoringa, 0, 1);
        kpiGrid.add(kpiDuplado, 1, 1);
        kpiGrid.add(kpiSemCodigo, 2, 1);
        kpiGrid.add(kpiAutoFixed, 3, 1);

        // Força colunas a terem o mesmo tamanho
        for (int i = 0; i < 5; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(20);
            kpiGrid.getColumnConstraints().add(cc);
        }

        // === 3. FILTROS (barra de busca e filtros) ===
        
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
                kpiGrid, // Topo: KPI grid (2 linhas)
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

        fileTable.setOnViewDetails(row -> {
            // Quando clicarem no olho, cria a tela de detalhes
            FileDetailsView details = new FileDetailsView(row, () -> {
                // Ação de "Voltar": coloca o dashboard de volta no centro
                this.setCenter(dashboardContent);
            });
            
            // Troca o centro da tela para os Detalhes
            this.setCenter(details);
        });

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
                            AnaliseTags analise = analyserService.processarTags(file);

                            fileTable.getData().add(new FileTable.FileRow(
                                    file.getName(),
                                    analise.status,
                                    analise.error,
                                    analise.autofix,
                                    analise.tags,
                                    LocalDateTime.now().format(formatter),
                                    file.getAbsolutePath(),
                                    analise.erpKey));
                        }
                        updateKpis();
                        applyFilters();
                        arquivoService.monitorarArquivos(inputPath);
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

    /** Retorna o KPI de "Todos" */
    public KpiCard getKpiTodos() {
        return kpiTodos;
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

    private String getFilterValueFromTitle(KpiCard[] cards, KpiCard selected) {
        if (selected == kpiTodos) return "Todos";
        if (selected == kpiCorretos) return "Ok";
        if (selected == kpiInconformidades) return "Erro";
        if (selected == kpiFerragens) return "Ferragens";
        if (selected == kpiMuxarabi) return "Muxarabi";
        if (selected == kpiCoringa) return "Coringa";
        if (selected == kpiDuplado) return "Duplado";
        if (selected == kpiSemCodigo) return "Sem Código";
        return "Todos";
    }

    private void applyFilters(){
        String searchText = filterBar.getSearchField().getText().toLowerCase();
        String statusFilter = filterBar.getStatusFilter().getValue();

        filteredList.setPredicate(row -> {
            boolean matchesSearch = searchText == null || searchText.isEmpty() || 
                                    row.getFilename().toLowerCase().contains(searchText);

            boolean matchesStatus = statusFilter == null || statusFilter.equals("Todos") ||
                                    row.getStatus().equalsIgnoreCase(statusFilter);

            return matchesSearch && matchesStatus;
        });

        // Atualiza o contador na barra de filtros
        filterBar.updateCount(filteredList.size(), fileTable.getData().size());
        
        // Sempre que aplicar filtros, opcionalmente atualizar KPIs se quiser que eles mostrem o filtrado
        // mas geralmente KPIs mostram o total global. Se o usuário quiser global:
        updateKpis();
    }

    private void updateKpis() {
        int todos = fileTable.getData().size();
        int corretos = 0;
        int erros = 0;
        int ferragens = 0;
        int muxarabi = 0;
        int coringa = 0;
        int duplado = 0;
        int semCodigo = 0;
        int autoFixed = 0;

        for (FileTable.FileRow row : fileTable.getData()) {
            String status = row.getStatus().toUpperCase();
            String errorTags = row.getErrors().toUpperCase();
            String autofixTags = row.getAutoFix().toUpperCase();
            String infoTags = row.getTags().toUpperCase();

            if (status.equals("OK")) corretos++;
            if (status.equals("ERRO")) erros++;
            if (status.contains("FERRAGENS") || infoTags.contains("FERRAGENS")) ferragens++;

            if (errorTags.contains("MUXARABI") || infoTags.contains("MUXARABI")) muxarabi++;
            if (errorTags.contains("CORINGA")) coringa++;
            if (errorTags.contains("DUPLADO") || errorTags.contains("DUPLADOS")) duplado++;
            if (errorTags.contains("SEM CÓDIGO") || errorTags.contains("SEM CODIGO")) semCodigo++;

            if (autofixTags != null && !autofixTags.isBlank()) autoFixed++;
        }

        kpiTodos.setValue(todos);
        kpiCorretos.setValue(corretos);
        kpiInconformidades.setValue(erros);
        kpiFerragens.setValue(ferragens);
        kpiMuxarabi.setValue(muxarabi);
        kpiCoringa.setValue(coringa);
        kpiDuplado.setValue(duplado);
        kpiSemCodigo.setValue(semCodigo);
        kpiAutoFixed.setValue(autoFixed);
    }
}
