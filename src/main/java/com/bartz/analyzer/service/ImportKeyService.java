package com.bartz.analyzer.service;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

@Service
public class ImportKeyService {
    private final ArquivoService arquivo;

    public ImportKeyService(ArquivoService arquivo){
        this.arquivo = arquivo;
    }

    public void mostrarImportKey(Document doc){
        NodeList importKey = doc.getElementsByTagName("IMPORTKEY");

        Element keyElement = (Element) importKey.item(0);

        String codImportKey = keyElement.getAttribute("CODIGO");
        System.out.println(codImportKey);
    }
}
