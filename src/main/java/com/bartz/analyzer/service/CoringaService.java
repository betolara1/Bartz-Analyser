package com.bartz.analyzer.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
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

    public List<String> listarSiglas(Document doc) {
        List<String> siglas = new ArrayList<>();
        try {
            XPath xPath = XPathFactory.newInstance().newXPath();

            for (String coringa : corCoringa) {
                String busca = "//*[contains(text(), '" + coringa + "') or @*[contains(., '" + coringa + "')]]";
                NodeList lista = (NodeList) xPath.compile(busca).evaluate(doc, XPathConstants.NODESET);

                for (int i = 0; i < lista.getLength(); i++) {
                    Node node = lista.item(i);
                    
                    // Verificar no texto da tag
                    String text = node.getTextContent();
                    if (text != null && text.contains(coringa)) {
                        adicionarSeValido(siglas, text);
                    }
                    
                    // Verificar nos atributos
                    NamedNodeMap attributes = node.getAttributes();
                    if (attributes != null) {
                        for (int j = 0; j < attributes.getLength(); j++) {
                            String attrValue = attributes.item(j).getNodeValue();
                            if (attrValue != null && attrValue.contains(coringa)) {
                                adicionarSeValido(siglas, attrValue);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return siglas;
    }

    private void adicionarSeValido(List<String> siglas, String valor) {
        String upper = valor.trim().toUpperCase();
        if (upper.contains("CHAPA") || upper.contains("FITA") || upper.contains("TAPAFURO") || upper.contains("PAINEL")) {
            if (!siglas.contains(upper)) {
                siglas.add(upper);
            }
        }
    }

    public void substituirSiglaEspecifica(Document doc, String siglaAtual, String novoCodigo) {
        try {
            XPath xPath = XPathFactory.newInstance().newXPath();

            for (String coringa : corCoringa) {
                String busca = "//*[contains(text(), '" + coringa + "') or @*[contains(., '" + coringa + "')]]";
                NodeList lista = (NodeList) xPath.compile(busca).evaluate(doc, XPathConstants.NODESET);

                for (int i = 0; i < lista.getLength(); i++) {
                    Node node = lista.item(i);
                    
                    // Verificar no texto da tag
                    String text = node.getTextContent();
                    if (text != null && text.toUpperCase().contains(siglaAtual.toUpperCase())) {
                        node.setTextContent(text.replaceAll("(?i)" + Pattern.quote(siglaAtual), novoCodigo));
                    }
                    
                    // Verificar nos atributos
                    NamedNodeMap attributes = node.getAttributes();
                    if (attributes != null) {
                        for (int j = 0; j < attributes.getLength(); j++) {
                            Node attr = attributes.item(j);
                            String attrValue = attr.getNodeValue();
                            if (attrValue != null && attrValue.toUpperCase().contains(siglaAtual.toUpperCase())) {
                                attr.setNodeValue(attrValue.replaceAll("(?i)" + Pattern.quote(siglaAtual), novoCodigo));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
