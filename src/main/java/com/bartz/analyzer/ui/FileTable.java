package com.bartz.analyzer.ui;

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

// HBox: layout horizontal (para botões de ação e badges)
import javafx.scene.layout.HBox;

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
import javafx.scene.shape.Circle;

import java.util.function.Consumer;
import javafx.scene.layout.FlowPane;

public class FileTable extends VBox {
    // FileTable herda de VBox — é um container vertical que
    // envolve a TableView com estilo.

    // A tabela em si
    private final TableView<FileRow> table;

    // Lista observável de dados — quando esta lista muda,
    // a tabela atualiza automaticamente
    private final ObservableList<FileRow> data;

    private Consumer<FileRow> onViewDetails;

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
        TableColumn<FileRow, String> colFilename = new TableColumn<>("ARQUIVO");
        colFilename.setCellValueFactory(new PropertyValueFactory<>("filename"));
        colFilename.setPrefWidth(280);
        
        colFilename.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String filename, boolean empty) {
                super.updateItem(filename, empty);
                if (empty || filename == null) {
                    setGraphic(null);
                } else {
                    FileRow row = getTableView().getItems().get(getIndex());
                    HBox box = new HBox(10);
                    box.setAlignment(Pos.CENTER_LEFT);
                    
                    Color dotColor = "OK".equals(row.getStatus()) ? Color.web("#27AE60") : Color.web("#E74C3C");
                    Circle dot = new Circle(3, dotColor); // Dot indicator
                    Label lbl = new Label(filename);
                    lbl.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-family: 'Inter';");
                    
                    box.getChildren().addAll(dot, lbl);
                    setGraphic(box);
                }
            }
        });

        // --- Coluna "Status" ---
        TableColumn<FileRow, String> colStatus = new TableColumn<>("STATUS");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setPrefWidth(100);
        colStatus.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setGraphic(null);
                } else {
                    Label badge = new Label(status);
                    badge.getStyleClass().add("badge");
                    if ("ERRO".equals(status)) badge.getStyleClass().add("badge-erro");
                    else if ("OK".equals(status)) badge.getStyleClass().add("badge-ok");
                    else badge.getStyleClass().add("badge-ferragens");
                    
                    setGraphic(badge);
                }
            }
        });

        // --- Coluna "Erros" ---
        TableColumn<FileRow, String> colErrors = new TableColumn<>("INCONFORMIDADES (ERROS)");
        colErrors.setCellValueFactory(new PropertyValueFactory<>("errors"));
        colErrors.setPrefWidth(250);
        colErrors.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String errors, boolean empty) {
                super.updateItem(errors, empty);
                if (empty || errors == null || errors.isBlank()) {
                    setGraphic(null);
                } else {
                    FlowPane flow = new FlowPane(4, 4);
                    String[] list = errors.split(";");
                    for (String err : list) {
                        Label lbl = new Label(err.trim().toUpperCase());
                        lbl.setStyle("-fx-background-color: rgba(231, 76, 60, 0.1); -fx-text-fill: #E74C3C; -fx-border-color: rgba(231, 76, 60, 0.2); -fx-padding: 2 8; -fx-font-size: 9px; -fx-font-weight: bold; -fx-background-radius: 2; -fx-border-radius: 2;");
                        flow.getChildren().add(lbl);
                    }
                    setGraphic(flow);
                }
            }
        });

        // --- Coluna "Auto-fix" ---
        TableColumn<FileRow, String> colAutoFix = new TableColumn<>("AUTO-FIX");
        colAutoFix.setCellValueFactory(new PropertyValueFactory<>("autoFix"));
        colAutoFix.setPrefWidth(150);
        colAutoFix.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String autoFix, boolean empty) {
                super.updateItem(autoFix, empty);
                if (empty || autoFix == null || autoFix.isBlank()) {
                    setGraphic(null);
                } else {
                    FlowPane flow = new FlowPane(4, 4);
                    String[] list = autoFix.split(";");
                    for (String fix : list) {
                        Label lbl = new Label(fix.trim().toUpperCase());
                        lbl.setStyle("-fx-background-color: rgba(26, 188, 156, 0.1); -fx-text-fill: #1ABC9C; -fx-border-color: rgba(26, 188, 156, 0.2); -fx-padding: 2 8; -fx-font-size: 9px; -fx-font-weight: bold; -fx-background-radius: 2; -fx-border-radius: 2;");
                        flow.getChildren().add(lbl);
                    }
                    setGraphic(flow);
                }
            }
        });

        // --- Coluna "Tags" ---
        TableColumn<FileRow, String> colTags = new TableColumn<>("TAGS");
        colTags.setCellValueFactory(new PropertyValueFactory<>("tags"));
        colTags.setPrefWidth(150);
        colTags.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String tags, boolean empty) {
                super.updateItem(tags, empty);
                if (empty || tags == null || tags.isBlank()) {
                    setGraphic(null);
                } else {
                    FlowPane flow = new FlowPane(4, 4);
                    String[] list = tags.split(";");
                    for (String tag : list) {
                        Label lbl = new Label(tag.trim().toUpperCase());
                        lbl.setStyle("-fx-background-color: rgba(52, 152, 219, 0.1); -fx-text-fill: #3498DB; -fx-border-color: rgba(52, 152, 219, 0.2); -fx-padding: 2 8; -fx-font-size: 9px; -fx-font-weight: bold; -fx-background-radius: 2; -fx-border-radius: 2;");
                        flow.getChildren().add(lbl);
                    }
                    setGraphic(flow);
                }
            }
        });

        // --- Coluna "Data/Hora" ---
        TableColumn<FileRow, String> colTimestamp = new TableColumn<>("DATA / HORA");
        colTimestamp.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        colTimestamp.setPrefWidth(150);
        colTimestamp.setStyle("-fx-text-fill: #A7A7A7; -fx-font-size: 11px;");

        // --- Coluna "Ações" ---
        TableColumn<FileRow, Void> colActions = new TableColumn<>("AÇÕES");
        colActions.setPrefWidth(120);
        colActions.setCellFactory(column -> new TableCell<>() {
            private final Button btnView = new Button();
            private final Button btnFolder = new Button();
            private final HBox actionBox = new HBox(8);

            {
                FontIcon eyeIcon = new FontIcon(FontAwesomeSolid.EYE);
                eyeIcon.setIconSize(14);
                eyeIcon.setIconColor(Color.web("#A7A7A7"));
                btnView.setGraphic(eyeIcon);
                btnView.getStyleClass().add("btn-ghost");

                FontIcon folderIcon = new FontIcon(FontAwesomeSolid.FOLDER_OPEN);
                folderIcon.setIconSize(14);
                folderIcon.setIconColor(Color.web("#A7A7A7"));
                btnFolder.setGraphic(folderIcon);
                btnFolder.getStyleClass().add("btn-ghost");

                actionBox.setAlignment(Pos.CENTER);
                actionBox.setStyle("-fx-background-color: #111111; -fx-background-radius: 6; -fx-padding: 4 8;");
                actionBox.getChildren().addAll(btnView, btnFolder);
                
                btnView.setOnAction(e -> {
                    FileRow row = getTableView().getItems().get(getIndex());
                    if (onViewDetails != null) onViewDetails.accept(row);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : actionBox);
            }
        });

        // === 4. ADICIONAR COLUNAS À TABELA ===

        // getColumns() retorna a lista de colunas da tabela.
        // addAll() adiciona todas as colunas de uma vez.
        // A ORDEM aqui define a ordem visual das colunas.
        table.getColumns().addAll(
                colFilename,
                colStatus,
                colErrors,
                colAutoFix,
                colTags,
                colTimestamp,
                colActions
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

    // este método é para o MainLayout se "inscrever"
    public void setOnViewDetails(Consumer<FileRow> handler) {
        this.onViewDetails = handler;
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
        private final SimpleStringProperty tags;
        private final SimpleStringProperty timestamp;
        private final SimpleStringProperty fullPath;
        private final SimpleStringProperty erpKey;


        /**
         * Construtor — cria uma linha com dados.
         *
         * @param filename  Nome do arquivo XML
         * @param status    Status: "OK", "ERRO", "FERRAGENS"
         * @param errors    Erros separados por ";" (ou "" se nenhum)
         * @param autoFix   Auto-fixes separados por ";" (ou "" se nenhum)
         * @param tags      Tags informativas (MUXARABI, FERRAGENS)
         * @param timestamp Data/hora do processamento
         * @param fullPath  Caminho completo da pasta 
         * @param erpKey    Chave de importação do ERP
         */
        public FileRow(String filename, Object status, String errors,
                String autoFix, String tags, String timestamp, String fullPath, String erpKey) {
            // new SimpleStringProperty(valor) cria a property com valor inicial
            this.filename = new SimpleStringProperty(filename);
            this.status = new SimpleStringProperty(String.valueOf(status));
            this.errors = new SimpleStringProperty(errors);
            this.autoFix = new SimpleStringProperty(autoFix);
            this.tags = new SimpleStringProperty(tags);
            this.timestamp = new SimpleStringProperty(timestamp);
            this.fullPath = new SimpleStringProperty(fullPath);
            this.erpKey = new SimpleStringProperty(erpKey);
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

        /** Retorna as tags (separadas por ";") */
        public String getTags() {
            return tags.get();
        }

        /** Retorna a data/hora */
        public String getTimestamp() {
            return timestamp.get();
        }

        /** Retorna o caminho completo da pasta */
        public String getFullPath(){
            return fullPath.get();
        }

        /** Retorna a chave do ERP */
        public String getErpKey() {
            return erpKey.get();
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

        /** Property das tags — para binding reativo */
        public SimpleStringProperty tagsProperty() {
            return tags;
        }

        /** Property do timestamp — para binding reativo */
        public SimpleStringProperty timestampProperty() {
            return timestamp;
        }

        /** Property do caminho — para binding reativo */
        public SimpleStringProperty fullPathProperty(){
            return fullPath;
        }

        /** Property da chave do ERP — para binding reativo */
        public SimpleStringProperty erpKeyProperty() {
            return erpKey;
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

        /** Atualiza o caminho da pasta */
        public void setFullPath(String value){
            fullPath.set(value);
        }

        /** Atualiza a chave do ERP */
        public void setErpKey(String value) {
            erpKey.set(value);
        }
    }
}
