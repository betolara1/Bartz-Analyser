package com.bartz.analyzer.service;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

@Service
public class ImportKeyService {

    public String getImportKey(Document doc) {
        NodeList importKey = doc.getElementsByTagName("IMPORTKEY");

        if (importKey.getLength() > 0) {
            Element keyElement = (Element) importKey.item(0);
            return keyElement.getAttribute("CODIGO");
        }

        return null;
    }
}
