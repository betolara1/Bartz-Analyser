package com.bartz.analyzer.service;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

@Service
public class CoringaService {

    private static final String[] corCoringa = { "_CG1", "_CG2", "CG1", "CG2", "CORINGA1", "CORINGA2", "_CG1_", "_CG2_" };

    public boolean temCoringa(Document doc) {
        try {
            // XPath é o buscador dentro do xml
            XPath xPath = XPathFactory.newInstance().newXPath();

            for (String coringa : corCoringa) {
                // Expressão: "//*" significa "em qualquer lugar",
                // "[text()='...']" significa "onde o texto dentro da tag seja exatamente este"
                // Esta busca diz: "Ache qualquer tag onde o texto OU qualquer atributo (@*)
                // contenha o coringa"
                String busca = "//*[contains(text(), '" + coringa + "') or @*[contains(., '" + coringa + "')]]";

                // Executar a busca
                NodeList lista = (NodeList) xPath.compile(busca).evaluate(doc, XPathConstants.NODESET);

                if (lista.getLength() > 0) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void trocarCoringa(Document doc, String cor) {
        try {
            // XPath é o buscador dentro do xml
            XPath xPath = XPathFactory.newInstance().newXPath();

            for (String coringa : corCoringa) {
                // Expressão: "//*" significa "em qualquer lugar",
                // "[text()='...']" significa "onde o texto dentro da tag seja exatamente este"
                String busca = "//*[contains(text(), '" + coringa + "') or @*[contains(., '" + coringa + "')]]";

                // Executar a busca
                NodeList lista = (NodeList) xPath.compile(busca).evaluate(doc, XPathConstants.NODESET);

                for (int i = 0; i < lista.getLength(); i++) {
                    lista.item(i).setTextContent(cor);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
