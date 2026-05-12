package com.bartz.analyzer.service;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

@Service
public class DupladosService {
    private final ArquivoService arquivo;

    public DupladosService(ArquivoService arquivo){
        this.arquivo = arquivo;
    }

    public boolean temDuplados(Document doc){
        try{
            NodeList duplados = doc.getElementsByTagName("ITEM");

            for (int i = 0; i < duplados.getLength(); i++){
                Element duplElement = (Element) duplados.item(i);
                String ibDuplado = duplElement.getAttribute("ITEM_BASE");
                String refDuplado = duplElement.getAttribute("REFERENCIA");

                if(ibDuplado.startsWith("ES08") || refDuplado.startsWith("ES08")){
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
