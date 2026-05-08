package com.bartz.analyzer.service;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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

            // VERIFICA SEM ITEM FILHO
            NodeList todosItens = doc.getElementsByTagName("ITEM");
            for (int i = 0; i < todosItens.getLength(); i++) {
                Element item = (Element) todosItens.item(i);
                String preco = item.getAttribute("PRECO_TOTAL");

                // Verifica se o preço é exatamente 0.01
                if ("0.01".equals(preco) || "0,01".equals(preco)) {
                    
                    // Procura pela tag <ITEMS> que deve conter os filhos
                    NodeList itemsTags = item.getElementsByTagName("ITEMS");
                    boolean temFilho = false;

                    if (itemsTags.getLength() > 0) {
                        Element itemsContainer = (Element) itemsTags.item(0);

                        // Verifica se existe algum <ITEM> dentro de <ITEMS>
                        // Note: Usamos uma busca que olha apenas descendentes diretos se necessário, 
                        // mas getElementsByTagName("ITEM") dentro do container já resolve.
                        NodeList filhos = itemsContainer.getElementsByTagName("ITEM");

                        if (filhos.getLength() > 0) {
                            temFilho = true;
                        }
                    }

                    if (!temFilho) {
                        analise.status = "ERRO";
                        if (analise.error.isEmpty()) {
                            analise.error = "SEM ITEM FILHO";
                        } 
                        else if (!analise.error.contains("SEM ITEM FILHO")) {
                            analise.error += "; SEM ITEM FILHO";
                        }
                        break;
                    }
                }
            }

            // VERIFICA MÁQUINAS (FERRAGENS)
            NodeList maquinas = doc.getElementsByTagName("MAQUINA");
            Set<String> idsEncontrados = new HashSet<>();
            for (int i = 0; i < maquinas.getLength(); i++) {
                Element m = (Element) maquinas.item(i);
                idsEncontrados.add(m.getAttribute("ID_PLUGIN"));
            }
            // Lista de máquinas obrigatórias conforme o seu JS
            String[] obrigatorios = {"2530", "2534", "2341", "2525"};
            for (String id : obrigatorios) {
                if (!idsEncontrados.contains(id)) {
                    // Se faltar alguma máquina obrigatória, adicionamos o erro "FERRAGENS"
                    // O status permanece "OK" conforme solicitado
                    if (analise.error.isEmpty()) {
                        analise.error = "FERRAGENS";
                    } else if (!analise.error.contains("FERRAGENS")) {
                        analise.error += "; FERRAGENS";
                    }
                    break;
                }
            }
        }
        catch(Exception e){
            analise.status = "FALHA";
        }
        return analise;
    }
}
