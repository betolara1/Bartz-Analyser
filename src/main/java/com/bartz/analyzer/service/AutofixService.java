package com.bartz.analyzer.service;

import java.io.File;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

@Service
public class AutofixService {
    private ArquivoService arquivoService;

    private AutofixService(ArquivoService arquivoService){
        this.arquivoService = arquivoService;
    }

    public boolean temAutofix(Document doc, File file) {
        try {
            XPath xPath = XPathFactory.newInstance().newXPath();

            // Esta busca procura por:
            // 1. Qualquer atributo chamado PRECO_TOTAL que tenha o valor EXATO "0.00"
            // 2. Qualquer tag chamada PRECO_TOTAL que tenha o conteúdo EXATO "0.00"
            String busca = "//*[@PRECO_TOTAL='0.00'] | //PRECO_TOTAL[text()='0.00']";

            NodeList lista = (NodeList) xPath.compile(busca).evaluate(doc, XPathConstants.NODESET);

            // Se a lista tiver mais de 0 itens, significa que ele encontrou
            if (lista.getLength() > 0) {
                for (int i = 0; i < lista.getLength(); i++){
                    lista.item(i).setTextContent("0.10");
                }

                arquivoService.salvarArquivo(doc, file);

                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return false;
    }
}
