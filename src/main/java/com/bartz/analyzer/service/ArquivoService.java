package com.bartz.analyzer.service;

import java.io.File;
import java.nio.file.*;
import static java.nio.file.StandardWatchEventKinds.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import javafx.application.Platform;

@Service
public class ArquivoService {

    @Autowired
    @Lazy
    private AnalyserService analyserService;

    public Document carregarArquivo(File file) throws Exception{
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        return factory.newDocumentBuilder().parse(file); 
    }

    public void salvarArquivo(Document doc, File file){
        try{
            // Aqui salva o arquivo xml 
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(new DOMSource(doc), new StreamResult(file));
            System.out.println("Arquivo salvo com sucesso: " + file.getName());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void monitorarArquivos(String caminhoPasta){
        // Criamos uma Thread separada para não travar a tela
        new Thread(() -> {
            try{
                WatchService watchService = FileSystems.getDefault().newWatchService();
                Path path = Paths.get(caminhoPasta);

                // Registramos a pasta para nos avisar quando um novo arquivo for CRIADO (ENTRY_CREATE)
                path.register(watchService, ENTRY_CREATE);

                // Loop infinito de vigília
                while(true){
                    WatchKey key = watchService.take(); // Espera até algo acontecer

                    for(WatchEvent<?> event : key.pollEvents()){
                        Path nomeArquivo = (Path) event.context();
                        // Verificamos se é um XML
                        if (nomeArquivo.toString().toLowerCase().endsWith(".xml")) {
                            File arquivoNovo = path.resolve(nomeArquivo).toFile();
                            
                            // IMPORTANTE: Como vamos mexer na tela (tabela),
                            // precisamos avisar o JavaFX para fazer isso com segurança
                            Platform.runLater(() -> {
                                // Aqui você chama a sua lógica de análise!
                                System.out.println("Novo arquivo detectado: " + nomeArquivo);
                                analyserService.processarTags(arquivoNovo);
                            });
                        }
                    }
                    key.reset(); // Volta para o estado de espera
                }
            } 
            catch (Exception e) {
                e.printStackTrace();
            }
        }).start(); // Inicia a thread
    }
}
