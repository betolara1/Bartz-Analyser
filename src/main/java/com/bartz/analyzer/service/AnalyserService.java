package com.bartz.analyzer.service;

import java.io.File;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

@Service
public class AnalyserService {

    private final CoringaService coringa;
    private final ArquivoService arquivo;
    private final AutofixService autofix;
    private String error;

    public AnalyserService(CoringaService coringa, ArquivoService arquivo, AutofixService autofix){
        this.coringa = coringa;
        this.arquivo = arquivo;
        this.autofix = autofix;
    }

    public class AnaliseTags{
        public String status = "OK";
        public String error = "";
        public String autofix = "";
    }

    public AnaliseTags processarTags(File file){
        AnaliseTags analise = new AnaliseTags();

        try{
            Document doc = arquivo.carregarArquivo(file);

            // VERIFICA CORINGA
            if(coringa.temCoringa(doc)){
                analise.status = "ERRO";
                analise.error = "CORINGA";
            }

            // Verifica e Corrige Autofix 
            String resultadoAutofix = autofix.temAutofix(doc, file);
            if (resultadoAutofix != null) {
                analise.autofix = resultadoAutofix; // Aqui agora vai aparecer "QUANTIDADE (1) | PREÇO (2)"
            }
        }
        catch(Exception e){
            analise.status = "FALHA";
        }
        return analise;
    }
}
