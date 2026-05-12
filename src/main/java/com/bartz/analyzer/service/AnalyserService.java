package com.bartz.analyzer.service;

import java.io.File;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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
    private String error;

    public AnalyserService( CoringaService coringa, ArquivoService arquivo, AutofixService autofix, ItemVazioService itemVazio, 
                            FerragensService ferragens, MuxarabiService muxarabi, ImportKeyService importKey, EspeciaisService especiais){
        this.coringa = coringa;
        this.arquivo = arquivo;
        this.autofix = autofix;
        this.itemVazio = itemVazio;
        this.ferragens = ferragens;
        this.muxarabi = muxarabi;
        this.importKey = importKey;
        this.especiais = especiais;
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
                analise.error = "MUXARABI";
                analise.status = "ERRO";
            }

            // ------------------- VERIFICA O IMPORTKEY -------------------
            importKey.mostrarImportKey(doc);


            // ------------------- VERIFICA OS ITENS ESPECIAIS -------------------
            especiais.temEspeciais(doc);


            // ------------------- VERIFICA OS ITENS DUPLADOS -------------------
            NodeList duplados = doc.getElementsByTagName("ITEM");

            for (int i = 0; i < duplados.getLength(); i++){
                Element duplElement = (Element) duplados.item(i);
                String ibDuplado = duplElement.getAttribute("ITEM_BASE");
                String refDuplado = duplElement.getAttribute("REFERENCIA");

                if(ibDuplado.startsWith("ES08") || refDuplado.startsWith("ES08")){
                    // analise.error = "37 MM DUPLADO";
                    // analise.status = "ERRO";
                    System.out.println(ibDuplado + "\n" + refDuplado);
                }

                break;
            }

        }
        catch(Exception e){
            analise.status = "FALHA";
        }
        return analise;
    }
}
