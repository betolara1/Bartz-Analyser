package com.bartz.analyzer.service;

import java.io.File;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

@Service
public class AnalyserService {

    private final CoringaService coringa;
    private final ArquivoService arquivo;
    private String error;

    public AnalyserService(CoringaService coringa, ArquivoService arquivo){
        this.coringa = coringa;
        this.arquivo = arquivo;
    }

    public void processarTudo(File file){
        processarErro(file);
        processarStatus(file);
    }

    public String processarStatus(File file){
        try{
            // buscar documento
            Document doc = arquivo.carregarArquivo(file);

            // analisar documento
            if(coringa.temCoringa(doc)){
                System.out.print(doc + "possui coringas!");
                return "ERRO";
            }
            else{
                return "OK";
            }
        }
        catch(Exception e){
            e.printStackTrace();
            return "FALHA";
        }
    }

    public String processarErro(File file){
        try{
            // buscar documento
            Document doc = arquivo.carregarArquivo(file);

            // analisar documento
            if(coringa.temCoringa(doc)){
                System.out.print(doc + "possui coringas!");
                return "CORINGA";
            }
            else{
                return "";
            }
        }
        catch(Exception e){
            e.printStackTrace();
            return "FALHA";
        }
    }

}
