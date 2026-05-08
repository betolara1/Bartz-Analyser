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

    public void processarTudo(File file){
        processarErro(file);
        processarStatus(file);
    }

    public String processarStatus(File file){
        try{
            Document doc = arquivo.carregarArquivo(file);
            boolean temCoringa = coringa.temCoringa(doc);
            boolean temAutofix = autofix.temAutofix(doc, file);

            if(temCoringa){return "ERRO";}
            if(temAutofix){return "OK";}
            return "OK";
        }
        catch(Exception e){
            return "FALHA";
        }
    }

    public String processarErro(File file){
        try{
            Document doc = arquivo.carregarArquivo(file);
            boolean temCoringa = coringa.temCoringa(doc);
            boolean temAutofix = autofix.temAutofix(doc, file);

            if(temCoringa){return "CORINGA";}
            if(temAutofix){return "AUTOFIX";}
            return "";
        }
        catch(Exception e){
            return "FALHA";
        }
    }
}
