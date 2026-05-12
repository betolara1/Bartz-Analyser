package com.bartz.analyzer.service;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

@Service
public class EspeciaisService {

    private final ArquivoService arquivo;

    public EspeciaisService(ArquivoService arquivo){
        this.arquivo = arquivo;
    }

    public boolean temEspeciais(Document doc){
        try{
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
                    System.out.println("Desenho: " +desenho + "\n"+ largura +"\n" + altura + "\n" + profundidade + "\n" + descricao);
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
