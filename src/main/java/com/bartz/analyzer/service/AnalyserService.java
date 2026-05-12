package com.bartz.analyzer.service;

import java.io.File;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

@Service
public class AnalyserService {

    private final CoringaService coringa;
    private final ArquivoService arquivo;
    private final AutofixService autofix;
    private final ItemVazioService itemVazio;
    private final FerragensService ferragens;
    private final MuxarabiService muxarabi;
    private final ImportKeyService importKey;
    private final EspeciaisService especiais;
    private final DupladosService duplados;
    private final ItemSemCodigoService itemSemCodigo;
    private String error;

    public AnalyserService( CoringaService coringa, ArquivoService arquivo, AutofixService autofix, ItemVazioService itemVazio, 
                            FerragensService ferragens, MuxarabiService muxarabi, ImportKeyService importKey, EspeciaisService especiais,
                            DupladosService duplados, ItemSemCodigoService itemSemCodigo){
        this.coringa = coringa;
        this.arquivo = arquivo;
        this.autofix = autofix;
        this.itemVazio = itemVazio;
        this.ferragens = ferragens;
        this.muxarabi = muxarabi;
        this.importKey = importKey;
        this.especiais = especiais;
        this.duplados = duplados;
        this.itemSemCodigo = itemSemCodigo;
    }

    public class AnaliseTags{
        public String status = "OK";
        public String error = "";
        public String autofix = "";
        public String erpKey;
    }

    public AnaliseTags processarTags(File file){
        AnaliseTags analise = new AnaliseTags();

        try{
            Document doc = arquivo.carregarArquivo(file);

            // ------------------- VERIFICA CORINGA -------------------
            if(coringa.temCoringa(doc)){
                analise.status = "ERRO";
                analise.error = "CORINGA";
            }

            // ------------------- Verifica e Corrige Autofix -------------------
            String resultadoAutofix = autofix.temAutofix(doc, file);
            if (resultadoAutofix != null) {
                analise.autofix = resultadoAutofix; // Aqui agora vai aparecer "QUANTIDADE (1) | PREÇO (2)"
            }

            // ------------------- VERIFICA SEM ITEM FILHO -------------------
            if (itemVazio.temItemVazio(doc)) {
                analise.status = "ERRO";
                if (analise.error.isEmpty()) {
                    analise.error = "SEM ITEM FILHO";
                } 
                else if (!analise.error.contains("SEM ITEM FILHO")) {
                    analise.error += "; SEM ITEM FILHO";
                }
            }

            // ------------------- VERIFICA MÁQUINAS (FERRAGENS) -------------------
            if(ferragens.temFerragem(doc)){
                if (analise.error.isEmpty()) {
                    analise.error = "FERRAGENS";
                } else if (!analise.error.contains("FERRAGENS")) {
                    analise.error += "; FERRAGENS";
                }
            }

            // ------------------- VERIFICA OS MUXARABIS -------------------
            if(muxarabi.temMuxarabi(doc)) {
                if (analise.error.isEmpty()) {
                    analise.error = "MUXARABI";
                } else if (!analise.error.contains("MUXARABI")) {
                    analise.error += "; MUXARABI";
                }
                analise.status = "OK";
            }

            // ------------------- VERIFICA O IMPORTKEY -------------------
            analise.erpKey = importKey.getImportKey(doc);


            // ------------------- VERIFICA OS ITENS ESPECIAIS -------------------
            especiais.temEspeciais(doc);


            // ------------------- VERIFICA OS ITENS DUPLADOS -------------------
            if(duplados.temDuplados(doc)){
                if (analise.error.isEmpty()) {
                    analise.error = "DUPLADOS";
                } else if (!analise.error.contains("DUPLADOS")) {
                    analise.error += "; DUPLADOS";
                }
            }

            // ------------------- VERIFICA OS ITENS SEM CODIGOS -------------------
            if(itemSemCodigo.temItemSemCodigo(doc)){
                if (analise.error.isEmpty()) {
                    analise.error = "SEM CODIGO";
                } else if (!analise.error.contains("SEM CODIGO")) {
                    analise.error += "; SEM CODIGO";
                }
            }

        }
        catch(Exception e){
            analise.status = "FALHA";
        }
        return analise;
    }
}
