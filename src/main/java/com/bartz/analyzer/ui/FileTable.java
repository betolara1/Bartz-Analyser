package com.bartz.analyzer.ui;

// ============================================================
// FileTable.java - Tabela de arquivos processados
// ============================================================
// Este componente cria uma TableView (tabela) que exibe os
// arquivos XML processados, com colunas para nome, status,
// erros, auto-fix, data/hora e ações.
//
// Equivale ao <Table> do EnhancedDashboard.tsx no React.
//
// CONCEITO IMPORTANTE: TableView no JavaFX
// -----------------------------------------
// No JavaFX, uma tabela precisa de:
// 1. Um "modelo de dados" (classe que representa uma linha)
// 2. Colunas (TableColumn) que dizem qual dado mostrar
// 3. Uma lista observável (ObservableList) com os dados
//
// Quando você adiciona/remove itens da ObservableList,
// a tabela se atualiza automaticamente! Isso é "data binding".
// ============================================================

// --- IMPORTS ---

// TableView: o componente de tabela principal
import javafx.scene.control.TableView;

// TableColumn: uma coluna da tabela
import javafx.scene.control.TableColumn;

// TableCell: uma célula customizada da tabela
import javafx.scene.control.TableCell;

// Button: botão clicável
import javafx.scene.control.Button;

// Label: texto
import javafx.scene.control.Label;

// Tooltip: dica ao passar o mouse
import javafx.scene.control.Tooltip;

// HBox: layout horizontal (para botões de ação e badges)
import javafx.scene.layout.HBox;

// FlowPane: layout que "quebra linha" quando não cabe — perfeito
// para exibir múltiplos badges de erro que podem variar em quantidade
import javafx.scene.layout.FlowPane;

// VBox: layout vertical (container externo)
import javafx.scene.layout.VBox;

// Priority: controle de crescimento
import javafx.scene.layout.Priority;

// Pos: alinhamento
import javafx.geometry.Pos;

// FXCollections: fábrica para criar listas observáveis
import javafx.collections.FXCollections;

// ObservableList: lista que notifica a tabela quando muda
import javafx.collections.ObservableList;

// PropertyValueFactory: conecta uma coluna a uma propriedade da classe modelo
// É o "binding" entre dados e UI
import javafx.scene.control.cell.PropertyValueFactory;

// SimpleStringProperty: uma propriedade String observável
import javafx.beans.property.SimpleStringProperty;

// FontIcon: ícone do Ikonli
import org.kordamp.ikonli.javafx.FontIcon;

// FontAwesomeSolid: pack de ícones
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

// Color: cor para ícones
import javafx.scene.paint.Color;

/**
 * FileTable — Tabela de arquivos processados.
 *
 * Estrutura visual:
 * ┌────────────┬──────────┬───────────────────┬──────────┬──────────────┬────────┐
 * │ Arquivo │ Status │ Erros │ Auto-fix │ Data/Hora │ Ações │
 * ├────────────┼──────────┼───────────────────┼──────────┼──────────────┼────────┤
 * │ PED_123.xml│ [OK] │ — │ QTD 0→1 │ 15/01 14:30 │ 👁 📂 │
 * │ PED_456.xml│ [ERRO] │ [ItemSemPreco] │ — │ 15/01 14:28 │ 👁 📂 │
 * └────────────┴──────────┴───────────────────┴──────────┴──────────────┴────────┘
 *
 * Usa dados mock (fictícios) para demonstração visual.
 */
public class FileTable extends VBox {
    // FileTable herda de VBox — é um container vertical que
    // envolve a TableView com estilo.

    // A tabela em si
    private final TableView<FileRow> table;

    // Lista observável de dados — quando esta lista muda,
    // a tabela atualiza automaticamente
    private final ObservableList<FileRow> data;

    /**
     * Construtor — monta a tabela completa.
     */
    public FileTable() {

        // 1. CRIAR A LISTA DE DADOS (Vazia)
        data = FXCollections.observableArrayList();

        // === 2. CRIAR A TABLEVIEW ===

        // TableView<FileRow> — tabela onde cada linha é um FileRow.
        // O generic <FileRow> diz ao JavaFX o tipo de dado de cada linha.
        table = new TableView<>(data);
        // Passamos "data" ao construtor — a tabela já sabe quais dados exibir.

        // Adiciona classe CSS "dark-table" para estilizar
        table.getStyleClass().add("dark-table");

        // === 3. CRIAR AS COLUNAS ===

        // --- Coluna "Arquivo" ---
        // TableColumn<FileRow, String> = coluna de uma tabela de FileRows que mostra
        // String
        TableColumn<FileRow, String> colFilename = new TableColumn<>("Arquivo");

        // setCellValueFactory() conecta a coluna a um dado do FileRow.
        // PropertyValueFactory("filename") vai chamar getFilename() do FileRow.
        colFilename.setCellValueFactory(new PropertyValueFactory<>("filename"));

        // setPrefWidth() define a largura preferida da coluna
        colFilename.setPrefWidth(280);

        // setStyle() aplica CSS inline — aqui usamos fonte monospace para nomes de
        // arquivo
        colFilename.setStyle("-fx-font-family: 'Consolas', 'Courier New', monospace;");

        // --- Coluna "Status" ---
        TableColumn<FileRow, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setPrefWidth(120);

        // setCellFactory() permite customizar COMO a célula é renderizada.
        // Aqui, ao invés de texto puro, mostramos um badge colorido.
        colStatus.setCellFactory(column -> new TableCell<>() {
            // Este bloco é uma "classe anônima" — uma classe sem nome
            // que herda de TableCell. É como um "componente inline".

            @Override
            protected void updateItem(String status, boolean empty) {
                // updateItem() é chamado pelo JavaFX toda vez que a célula
                // precisa se redesenhar (scroll, atualização de dados, etc.)

                // SEMPRE chame super.updateItem() primeiro!
                super.updateItem(status, empty);

                if (empty || status == null) {
                    // Se a célula está vazia (sem dados), limpa o conteúdo
                    setGraphic(null);
                    setText(null);
                } else {
                    // Cria um Label com o texto do status
                    Label badge = new Label(status);

                    // Adiciona a classe CSS base "badge"
                    badge.getStyleClass().add("badge");

                    // Adiciona a classe CSS específica baseada no status
                    switch (status) {
                        case "OK":
                            badge.getStyleClass().add("badge-ok");
                            break;
                        case "ERRO":
                            badge.getStyleClass().add("badge-erro");
                            break;
                        case "FERRAGENS":
                            badge.setText("FERRAGENS-ONLY");
                            badge.getStyleClass().add("badge-ferragens");
                            break;
                    }

                    // setGraphic() coloca um Node (componente visual) na célula
                    // ao invés de texto puro
                    setGraphic(badge);

                    // Remove o texto padrão (usamos o badge no lugar)
                    setText(null);
                }
            }
        });

        // --- Coluna "Erros" ---
        TableColumn<FileRow, String> colErrors = new TableColumn<>("Erros");
        colErrors.setCellValueFactory(new PropertyValueFactory<>("errors"));
        colErrors.setPrefWidth(250);

        // Customiza a renderização para mostrar badges de erro
        colErrors.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String errors, boolean empty) {
                super.updateItem(errors, empty);

                if (empty || errors == null || errors.isBlank()) {
                    setGraphic(null);
                    setText(null);
                } else {
                    // FlowPane: layout que organiza filhos em "fluxo".
                    // Se não cabe na linha, quebra para a próxima.
                    // Perfeito para badges que variam em quantidade.
                    FlowPane flow = new FlowPane();
                    flow.setHgap(4); // espaço horizontal entre badges
                    flow.setVgap(4); // espaço vertical entre linhas

                    // Separa a string de erros por ";" e cria um badge para cada
                    String[] errorList = errors.split(";");
                    for (String error : errorList) {
                        String trimmed = error.trim();
                        if (!trimmed.isEmpty()) {
                            Label badge = new Label(trimmed);
                            badge.getStyleClass().addAll("badge", "badge-error-tag");
                            flow.getChildren().add(badge);
                        }
                    }

                    setGraphic(flow);
                    setText(null);
                }
            }
        });

        // --- Coluna "Auto-fix" ---
        TableColumn<FileRow, String> colAutoFix = new TableColumn<>("Auto-fix");
        colAutoFix.setCellValueFactory(new PropertyValueFactory<>("autoFix"));
        colAutoFix.setPrefWidth(180);

        // Renderiza badges de auto-fix (verde/teal)
        colAutoFix.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String autoFix, boolean empty) {
                super.updateItem(autoFix, empty);

                if (empty || autoFix == null || autoFix.isBlank()) {
                    setGraphic(null);
                    setText(null);
                } else {
                    FlowPane flow = new FlowPane();
                    flow.setHgap(4);
                    flow.setVgap(4);

                    String[] fixes = autoFix.split(";");
                    for (String fix : fixes) {
                        String trimmed = fix.trim();
                        if (!trimmed.isEmpty()) {
                            Label badge = new Label(trimmed);
                            badge.getStyleClass().addAll("badge", "badge-autofix");
                            flow.getChildren().add(badge);
                        }
                    }

                    setGraphic(flow);
                    setText(null);
                }
            }
        });

        // --- Coluna "Data/Hora" ---
        TableColumn<FileRow, String> colTimestamp = new TableColumn<>("Data/Hora");
        colTimestamp.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        colTimestamp.setPrefWidth(150);

        // Estilo inline para texto cinza
        colTimestamp.setStyle("-fx-text-fill: #A7A7A7;");

        // --- Coluna "Ações" ---
        TableColumn<FileRow, Void> colActions = new TableColumn<>("Ações");
        colActions.setPrefWidth(100);

        // Coluna de ações: não tem dados, só botões.
        // O tipo é <FileRow, Void> porque não existe um "valor" para esta coluna.
        colActions.setCellFactory(column -> new TableCell<>() {
            // Cria os botões uma vez (não recria a cada updateItem)
            private final Button btnView = new Button();
            private final Button btnFolder = new Button();
            private final HBox actionBox = new HBox(4);

            // Bloco de inicialização — roda quando a célula é criada
            {
                // Ícone "olho" para ver detalhes
                FontIcon eyeIcon = new FontIcon(FontAwesomeSolid.EYE);
                eyeIcon.setIconSize(14);
                eyeIcon.setIconColor(Color.web("#A7A7A7"));
                btnView.setGraphic(eyeIcon);
                btnView.getStyleClass().add("btn-ghost");
                btnView.setTooltip(new Tooltip("Ver detalhes"));

                // Ícone "pasta" para abrir no explorador
                FontIcon folderIcon = new FontIcon(FontAwesomeSolid.FOLDER_OPEN);
                folderIcon.setIconSize(14);
                folderIcon.setIconColor(Color.web("#A7A7A7"));
                btnFolder.setGraphic(folderIcon);
                btnFolder.getStyleClass().add("btn-ghost");
                btnFolder.setTooltip(new Tooltip("Abrir na pasta"));

                // Agrupa botões lado a lado
                actionBox.setAlignment(Pos.CENTER);
                actionBox.getChildren().addAll(btnView, btnFolder);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(actionBox);
                }
            }
        });

        // === 4. ADICIONAR COLUNAS À TABELA ===

        // getColumns() retorna a lista de colunas da tabela.
        // addAll() adiciona todas as colunas de uma vez.
        // A ORDEM aqui define a ordem visual das colunas.
        table.getColumns().addAll(
                colFilename, // 1ª coluna
                colStatus, // 2ª coluna
                colErrors, // 3ª coluna
                colAutoFix, // 4ª coluna
                colTimestamp, // 5ª coluna
                colActions // 6ª coluna
        );

        // COLUMN_RESIZE_POLICY define como as colunas se redimensionam:
        // CONSTRAINED_RESIZE_POLICY = colunas se ajustam para preencher a largura total
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // === 5. MONTAR O CONTAINER ===

        // O VBox externo aplica o estilo "table-container" (borda, radius)
        this.getChildren().add(table);
        this.getStyleClass().add("table-container");

        // Faz a tabela crescer verticalmente para preencher o espaço
        VBox.setVgrow(table, Priority.ALWAYS);

        // Define um placeholder para quando a tabela está vazia
        Label emptyLabel = new Label("Nenhum arquivo processado ainda");
        emptyLabel.setStyle("-fx-text-fill: #A7A7A7; -fx-font-size: 14px;");
        table.setPlaceholder(emptyLabel);
    }

    // === MÉTODOS PÚBLICOS ===

    /**
     * Retorna a TableView para manipulação direta.
     * Futuramente, o backend pode usar table.getItems().add(...)
     * para adicionar novas linhas.
     */
    public TableView<FileRow> getTable() {
        return table;
    }

    /**
     * Retorna a lista observável de dados.
     * Use getData().add(new FileRow(...)) para adicionar uma linha.
     * A tabela se atualiza automaticamente!
     */
    public ObservableList<FileRow> getData() {
        return data;
    }

    // ================================================================
    // CLASSE INTERNA: FileRow — Modelo de dados de uma linha da tabela
    // ================================================================
    //
    // CONCEITO: No JavaFX, cada linha da TableView precisa de uma classe
    // com "properties" (propriedades observáveis). Usamos SimpleStringProperty
    // porque, no futuro, se o valor mudar, a tabela se atualiza sozinha.
    //
    // É como um "DTO" (Data Transfer Object) do Spring, mas com
    // propriedades reativas do JavaFX.
    // ================================================================

    public static class FileRow {
        // SimpleStringProperty é uma propriedade String observável do JavaFX.
        // Diferente de uma String normal, ela pode ser "observada" —
        // quando o valor muda, quem estiver ouvindo é notificado.

        private final SimpleStringProperty filename;
        private final SimpleStringProperty status;
        private final SimpleStringProperty errors;
        private final SimpleStringProperty autoFix;
        private final SimpleStringProperty timestamp;

        /**
         * Construtor — cria uma linha com dados.
         *
         * @param filename  Nome do arquivo XML
         * @param status    Status: "OK", "ERRO", "FERRAGENS"
         * @param errors    Erros separados por ";" (ou "" se nenhum)
         * @param autoFix   Auto-fixes separados por ";" (ou "" se nenhum)
         * @param timestamp Data/hora do processamento
         */
        public FileRow(String filename, Object status, String errors,
                String autoFix, String timestamp) {
            // new SimpleStringProperty(valor) cria a property com valor inicial
            this.filename = new SimpleStringProperty(filename);
            this.status = new SimpleStringProperty(String.valueOf(status));
            this.errors = new SimpleStringProperty(errors);
            this.autoFix = new SimpleStringProperty(autoFix);
            this.timestamp = new SimpleStringProperty(timestamp);
        }

        // === GETTERS ===
        // O PropertyValueFactory("filename") procura um método chamado
        // getFilename() — por isso o nome precisa bater exatamente.

        /** Retorna o nome do arquivo */
        public String getFilename() {
            return filename.get();
        }

        /** Retorna o status (OK, ERRO, FERRAGENS) */
        public String getStatus() {
            return status.get();
        }

        /** Retorna os erros (separados por ";") */
        public String getErrors() {
            return errors.get();
        }

        /** Retorna os auto-fixes (separados por ";") */
        public String getAutoFix() {
            return autoFix.get();
        }

        /** Retorna a data/hora */
        public String getTimestamp() {
            return timestamp.get();
        }

        // === PROPERTY ACCESSORS ===
        // Estes métodos retornam a Property em si (não o valor).
        // São usados pelo JavaFX para "binding" (ligação reativa).
        // Convenção: nomeProperty() para cada property.

        /** Property do filename — para binding reativo */
        public SimpleStringProperty filenameProperty() {
            return filename;
        }

        /** Property do status — para binding reativo */
        public SimpleStringProperty statusProperty() {
            return status;
        }

        /** Property dos erros — para binding reativo */
        public SimpleStringProperty errorsProperty() {
            return errors;
        }

        /** Property do auto-fix — para binding reativo */
        public SimpleStringProperty autoFixProperty() {
            return autoFix;
        }

        /** Property do timestamp — para binding reativo */
        public SimpleStringProperty timestampProperty() {
            return timestamp;
        }

        // === SETTERS ===
        // Para atualizar os valores. Quando .set() é chamado,
        // a tabela se redesenha automaticamente!

        /** Atualiza o nome do arquivo */
        public void setFilename(String value) {
            filename.set(value);
        }

        /** Atualiza o status */
        public void setStatus(String value) {
            status.set(value);
        }

        /** Atualiza os erros */
        public void setErrors(String value) {
            errors.set(value);
        }

        /** Atualiza os auto-fixes */
        public void setAutoFix(String value) {
            autoFix.set(value);
        }

        /** Atualiza a data/hora */
        public void setTimestamp(String value) {
            timestamp.set(value);
        }
    }
}
