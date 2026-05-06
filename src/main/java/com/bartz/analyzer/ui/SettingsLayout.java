package com.bartz.analyzer.ui;

// ============================================================
// SettingsLayout.java - Tela de Configuração de Caminhos
// ============================================================
// Esta tela replica exatamente o visual solicitado:
// - Seções separadas por cores (Azul, Verde, Roxo)
// - Campos de entrada de caminho com botão de selecionar pasta
// - Rodapé com botões de Salvar, Auto-fix e Iniciar
// ============================================================

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.DirectoryChooser;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;

import com.bartz.analyzer.service.ConfigService;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import java.io.File;

/**
 * Tela de Configurações de Caminhos.
 * Comentada linha a linha para aprendizado.
 */
public class SettingsLayout extends VBox {
    private TextField campoInput;
    private TextField campoExport;
    private TextField campoOk;
    private TextField campoError;
    private TextField campoDraw;
    private TextField campoLogError;
    private TextField campoLogOk;

    public SettingsLayout() {
        // 1. Configuração do container principal (VBox)
        this.setSpacing(20); // Espaço entre as seções principais
        this.setPadding(new Insets(30)); // Margem interna de 30px
        this.setStyle("-fx-background-color: #111111;"); // Fundo escuro total

        // --- HEADER DA TELA ---
        HBox topHeader = new HBox();
        Label title = new Label("Caminhos de Rede");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox statusBox = new HBox(8);
        statusBox.setAlignment(Pos.CENTER);
        Circle dot = new Circle(4, Color.web("#E74C3C"));
        Label statusLabel = new Label("Monitoramento Parado");
        statusLabel.getStyleClass().add("text-muted");
        statusBox.getChildren().addAll(dot, statusLabel);

        topHeader.getChildren().addAll(title, spacer, statusBox);
        this.getChildren().add(topHeader);

        // --- SEÇÃO 1: ORIGEM & DESTINO (AZUL) ---
        VBox section1 = createSection("ORIGEM & DESTINO", FontAwesomeSolid.EXCHANGE_ALT, "section-blue");

        GridPane grid1 = createGrid();
        campoInput = addPathField(grid1, "Pasta de Entrada", ConfigService.getValue(ConfigService.PATH_INPUT), 0);
        campoExport = addPathField(grid1, "Pasta de Exportação", ConfigService.getValue(ConfigService.PATH_EXPORT), 1);

        section1.getChildren().add(grid1);

        // --- SEÇÃO 2: RESULTADOS & DESENHOS (VERDE) ---
        VBox section2 = createSection("RESULTADOS & DESENHOS", FontAwesomeSolid.CHECK_CIRCLE, "section-green");

        GridPane grid2 = createGrid();
        campoOk = addPathField(grid2, "Pasta Final - OK", ConfigService.getValue(ConfigService.PATH_OK), 0);
        campoError = addPathField(grid2, "Pasta Final - Erro", ConfigService.getValue(ConfigService.PATH_ERROR), 1);
        campoDraw = addPathField(grid2, "Pasta de Desenhos", ConfigService.getValue(ConfigService.PATH_DRAW), 2);

        section2.getChildren().add(grid2);

        // --- SEÇÃO 3: REGISTROS (LOGS) (ROXO) ---
        VBox section3 = createSection("REGISTROS (LOGS)", FontAwesomeSolid.LIST_UL, "section-purple");

        GridPane grid3 = createGrid();
        campoLogError = addPathField(grid3, "Logs - Errors", ConfigService.getValue(ConfigService.PATH_LOG_ERROR), 0);
        campoLogOk = addPathField(grid3, "Logs - Processed", ConfigService.getValue(ConfigService.PATH_LOG_OK), 1);

        section3.getChildren().add(grid3);

        // Adiciona as seções ao layout
        this.getChildren().addAll(section1, section2, section3);

        // --- RODAPÉ (FOOTER) ---
        HBox footer = new HBox(15);
        footer.setPadding(new Insets(10, 0, 0, 0));
        footer.setAlignment(Pos.CENTER_LEFT);

        Button btnSave = new Button("Salvar");
        btnSave.getStyleClass().add("btn-outline");
        btnSave.setGraphic(new FontIcon(FontAwesomeSolid.SAVE));
        btnSave.setOnAction(event -> {
            // Cria o alerta de confirmação
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmar");
            alert.setHeaderText("Deseja salvar as alterações?");

            // Cria os botões de salvar e cancelar
            ButtonType btnSalvar = new ButtonType("Salvar");
            ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(btnSalvar, btnCancelar);

            // Cria a janela e salva

            alert.showAndWait().ifPresent(resposta -> {
                if(resposta == btnSalvar){
                    // Pega o caminho
                    String caminhoInput = campoInput.getText();
                    String caminhoExport = campoExport.getText();
                    String caminhoOk = campoOk.getText();
                    String caminhoError = campoError.getText();
                    String caminhoDraw = campoDraw.getText();
                    String caminhoLogError = campoLogError.getText();
                    String caminhoLogOk = campoLogOk.getText();

                    // Salva o caminho
                    ConfigService.saveValue(ConfigService.PATH_INPUT, caminhoInput);
                    ConfigService.saveValue(ConfigService.PATH_EXPORT, caminhoExport);
                    ConfigService.saveValue(ConfigService.PATH_OK, caminhoOk);
                    ConfigService.saveValue(ConfigService.PATH_ERROR, caminhoError);
                    ConfigService.saveValue(ConfigService.PATH_DRAW, caminhoDraw);
                    ConfigService.saveValue(ConfigService.PATH_LOG_ERROR, caminhoLogError);
                    ConfigService.saveValue(ConfigService.PATH_LOG_OK, caminhoLogOk);

                    MainLayout main = (MainLayout) this.getScene().getRoot();
                    main.getHeaderBar().getCaminhosButton().fire();
                }
                else{
                    // Por enquanto não faz nada
                }
            });
        });

        Region footerSpacer = new Region();
        HBox.setHgrow(footerSpacer, Priority.ALWAYS);

        footer.getChildren().addAll(btnSave, footerSpacer);
        this.getChildren().add(footer);
    }

    // --- MÉTODOS AUXILIARES PARA LIMPEZA DE CÓDIGO ---

    /** Cria o container de uma seção (o box com título colorido) */
    private VBox createSection(String title, FontAwesomeSolid icon, String colorClass) {
        VBox container = new VBox(15);
        container.getStyleClass().add("settings-section");

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        FontIcon fontIcon = new FontIcon(icon);
        fontIcon.setIconSize(14);
        fontIcon.getStyleClass().add(colorClass);

        Label label = new Label(title.toUpperCase());
        label.getStyleClass().addAll("section-header", colorClass);

        header.getChildren().addAll(fontIcon, label);
        container.getChildren().add(header);

        return container;
    }

    /** Cria um grid para organizar os campos lado a lado */
    private GridPane createGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(20); // Espaço horizontal entre colunas
        grid.setVgap(10); // Espaço vertical entre linhas

        // Define que as colunas do grid devem ter largura igual e crescer
        ColumnConstraints col = new ColumnConstraints();
        col.setPercentWidth(50);
        grid.getColumnConstraints().addAll(col, col);

        return grid;
    }

    /** Adiciona um campo de caminho (Label + Input + Botão) ao grid */
    private TextField addPathField(GridPane grid, String labelText, String defaultPath, int columnIndex) {
        VBox group = new VBox(8);

        Label label = new Label(labelText);
        label.setStyle("-fx-text-fill: #A7A7A7; -fx-font-size: 11px;");

        HBox inputRow = new HBox(8);
        TextField field = new TextField(defaultPath);
        field.getStyleClass().add("path-field");
        HBox.setHgrow(field, Priority.ALWAYS);

        Button btnFolder = new Button();
        btnFolder.getStyleClass().add("folder-btn");
        FontIcon folderIcon = new FontIcon(FontAwesomeSolid.FOLDER_OPEN);
        folderIcon.setIconColor(Color.web("#A7A7A7"));
        btnFolder.setGraphic(folderIcon);

        // --- AÇÃO DE ABRIR O SELETOR DE PASTA ---
        btnFolder.setOnAction(e -> {
            DirectoryChooser dc = new DirectoryChooser();
            dc.setTitle("Selecionar " + labelText);
            File selected = dc.showDialog(this.getScene().getWindow());
            if (selected != null) {
                field.setText(selected.getAbsolutePath());
            }
        });

        inputRow.getChildren().addAll(field, btnFolder);
        group.getChildren().addAll(label, inputRow);

        grid.add(group, columnIndex % 2, columnIndex / 2);

        return field;
    }
}

// Classe auxiliar para o ponto do status (usado no header)
class Circle extends javafx.scene.shape.Circle {
    public Circle(double radius, Color color) {
        super(radius, color);
    }
}
