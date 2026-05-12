package com.bartz.analyzer.service;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

@Service
public class ItemSemCodigoService {
    private final ArquivoService arquivo;

    public ItemSemCodigoService(ArquivoService arquivo){
        this.arquivo = arquivo;
    }

    public boolean temItemSemCodigo(Document doc){
        try{
            NodeList todosItens = doc.getElementsByTagName("ITEM");

            for (int i = 0; i < todosItens.getLength(); i++) {
                Element item = (Element) todosItens.item(i);
                String referencia = item.getAttribute("REFERENCIA");
                String itemBase = item.getAttribute("ITEM_BASE");

                if ("".equals(referencia) && "".equals(itemBase)) {
                    return true;
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
