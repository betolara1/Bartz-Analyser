package com.bartz.analyzer.service;

import org.springframework.stereotype.Service;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

@Service
public class ItemVazioService {
    private ArquivoService arquivo;

    public ItemVazioService(ArquivoService arquivo){
        this.arquivo = arquivo;
    }

    public boolean temItemVazio(Document doc){
        try {
            NodeList todosItens = doc.getElementsByTagName("ITEM");
            for (int i = 0; i < todosItens.getLength(); i++) {
                Element item = (Element) todosItens.item(i);
                String preco = item.getAttribute("PRECO_TOTAL");
                if ("0.01".equals(preco) || "0,01".equals(preco)) {
                    NodeList itemsTags = item.getElementsByTagName("ITEMS");
                    // Se não tem a tag <ITEMS> ou se a tag <ITEMS> está vazia, o item está VAZIO
                    if (itemsTags.getLength() == 0) {
                        return true; 
                    }
                    Element itemsContainer = (Element) itemsTags.item(0);
                    NodeList filhos = itemsContainer.getElementsByTagName("ITEM");
                    if (filhos.getLength() == 0) {
                        return true; // ENCONTROU UM ITEM VAZIO
                    }
                }
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
