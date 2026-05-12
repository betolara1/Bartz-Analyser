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
    private String error;

    public AnalyserService(CoringaService coringa, ArquivoService arquivo, AutofixService autofix, ItemVazioService itemVazio, FerragensService ferragens, MuxarabiService muxarabi){
        this.coringa = coringa;
        this.arquivo = arquivo;
        this.autofix = autofix;
        this.itemVazio = itemVazio;
        this.ferragens = ferragens;
        this.muxarabi = muxarabi;
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
            NodeList importKey = doc.getElementsByTagName("IMPORTKEY");

            Element keyElement = (Element) importKey.item(0);

            String codImportKey = keyElement.getAttribute("CODIGO");
            //COLOCAR CODIGO DEPOIS
            //System.out.println(codImportKey);


            // ------------------- VERIFICA OS ITENS ESPECIAIS -------------------
            NodeList especias = doc.getElementsByTagName("ITEM");

            for(int i = 0; i < especias.getLength(); i++){
                Element espElement = (Element) especias.item(i);
                String refEspeciais = espElement.getAttribute("REFERENCIA");
                String ibEspeciais = espElement.getAttribute("ITEM_BASE");
                

                if(refEspeciais.startsWith("ES0") || ibEspeciais.startsWith("ES0")){
                    String desenho = espElement.getAttribute("DESENHO");
                    String largura = espElement.getAttribute("LARGURA");
                    String altura = espElement.getAttribute("ALTURA");
                    String profundidade = espElement.getAttribute("PROFUNDIDADE");
                    String descricao = espElement.getAttribute("DESCRICAO");

                    //COLOCAR CODIGO DEPOIS
                    //System.out.println("Desenho: " +desenho + "\n"+ largura +"\n" + altura + "\n" + profundidade + "\n" + descricao);
                }
            }


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
