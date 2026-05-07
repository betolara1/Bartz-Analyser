package com.bartz.analyzer;

// ============================================================
// StageInitializer.java - Inicializador da Janela JavaFX
// ============================================================
// Este é o "ponto de entrada visual" da aplicação.
// Quando o Spring Boot termina de iniciar, ele dispara o evento
// StageReadyEvent, e este componente escuta esse evento para
// montar e exibir a janela.
//
// CONCEITOS IMPORTANTES:
//
// 1. Stage = a "janela" do sistema operacional (como um <window>)
//    É o container de mais alto nível do JavaFX.
//
// 2. Scene = o "conteúdo" dentro da janela.
//    Contém a árvore de nós (Scene Graph) que define a UI.
//
// 3. Scene Graph = hierarquia de componentes visuais.
//    Ex: BorderPane → HBox → Label
//    É como a árvore DOM do HTML.
//
// 4. CSS = JavaFX suporta CSS para estilização!
//    Você pode carregar arquivos .css e aplicar classes,
//    parecido com HTML/CSS.
// ============================================================

// --- IMPORTS ---

// Scene: o conteúdo de uma janela (contém o Scene Graph)
import javafx.scene.Scene;

// Application: classe base do JavaFX — usada aqui para aplicar temas
import javafx.application.Application;

// Stage: a janela do sistema operacional
import javafx.stage.Stage;

// ApplicationListener: interface do Spring que escuta eventos.
// Aqui escutamos o StageReadyEvent que é disparado quando a janela está pronta.
import org.springframework.context.ApplicationListener;

// @Component: marca esta classe como um "bean" do Spring.
// O Spring cria automaticamente uma instância e gerencia seu ciclo de vida.
import org.springframework.stereotype.Component;

// PrimerDark: tema escuro do AtlantaFX — biblioteca de temas premium para JavaFX.
// Ele aplica um visual moderno e profissional a todos os componentes.
import atlantafx.base.theme.PrimerDark;

import com.bartz.analyzer.service.AnalyserService;
import com.bartz.analyzer.service.ArquivoService;
// MainLayout: nosso componente customizado que monta toda a tela
import com.bartz.analyzer.ui.MainLayout;

/**
 * StageInitializer — Escuta o evento StageReadyEvent e monta a janela.
 *
 * Fluxo de execução:
 * 1. BartzAnalyserApplication.main() → chama Application.launch()
 * 2. JavafxApplication.init() → inicia o Spring Boot
 * 3. JavafxApplication.start() → publica StageReadyEvent
 * 4. StageInitializer.onApplicationEvent() → MONTA A JANELA (estamos aqui!)
 *
 * @Component = o Spring gerencia esta classe automaticamente
 * implements ApplicationListener<...> = escuta um tipo específico de evento
 */
@Component
public class StageInitializer implements ApplicationListener<JavafxApplication.StageReadyEvent> {

    private final AnalyserService analyserService;
    private final ArquivoService arquivoService;
    private StageInitializer (AnalyserService analyserService){
        this.analyserService = analyserService;
        this.arquivoService = new ArquivoService();
    }

    /**
     * Método chamado quando o evento StageReadyEvent é disparado.
     * Aqui montamos toda a interface da aplicação.
     *
     * @param event contém o Stage (janela) pronto para uso
     */
    @Override
    public void onApplicationEvent(JavafxApplication.StageReadyEvent event) {

        // 1. Obter o Stage (janela) do evento.
        // O Stage foi criado pelo JavaFX e passado via evento.
        Stage stage = event.getStage();

        // 2. Aplicar o tema PrimerDark do AtlantaFX.
        // setUserAgentStylesheet() aplica um CSS global a TODA a aplicação.
        // É como colocar um <link rel="stylesheet"> no <head> do HTML.
        // PrimerDark dá um visual moderno e escuro a todos os componentes.
        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());

        // 3. Criar o layout principal (nosso componente customizado).
        // MainLayout é um BorderPane que contém:
        // - HeaderBar (topo)
        // - KPI Cards + FilterBar + FileTable (centro)
        MainLayout mainLayout = new MainLayout(analyserService);

        // 4. Criar a Scene (o conteúdo da janela).
        // Scene recebe:
        //   - O nó raiz do Scene Graph (mainLayout)
        //   - Largura inicial da janela (1200 pixels)
        //   - Altura inicial da janela (800 pixels)
        Scene scene = new Scene(mainLayout, 1200, 800);

        // 5. Carregar o CSS customizado do Bartz.
        // getResource() busca um arquivo dentro de src/main/resources/
        // O caminho "/styles/bartz-dark.css" corresponde ao arquivo:
        // src/main/resources/styles/bartz-dark.css
        //
        // toExternalForm() converte o path para um formato URL que o JavaFX entende.
        //
        // getStylesheets().add() adiciona o CSS à Scene.
        // Este CSS é aplicado DEPOIS do tema AtlantaFX, então pode sobrescrever estilos.
        String css = getClass().getResource("/styles/bartz-dark.css").toExternalForm();
        scene.getStylesheets().add(css);

        // 6. Aplicar a Scene ao Stage.
        // setScene() define qual conteúdo a janela vai mostrar.
        stage.setScene(scene);

        // 7. Definir o título da janela (aparece na barra de título do OS).
        stage.setTitle("Bartz Analyser");

        // 8. Definir tamanho mínimo da janela.
        // O usuário não pode redimensionar menor que isso.
        stage.setMinWidth(900);
        stage.setMinHeight(600);

        // 9. Exibir a janela!
        // show() torna a janela visível na tela.
        // Sem chamar show(), a janela existe mas fica invisível.
        stage.show();
    }
}
