package com.bartz.analyzer.service;

import java.io.File;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

@Service
public class AutofixService {
    private ArquivoService arquivoService;

    public AutofixService(ArquivoService arquivoService){
        this.arquivoService = arquivoService;
    }

    public String temAutofix(Document doc, File file) {
        try {
            int qtyFixCount = 0;
            int priceFixCount = 0;
            boolean changed = false;

            // Busca todas as tags <ITEM> 
            NodeList items = doc.getElementsByTagName("ITEM");

            for (int i = 0; i < items.getLength(); i++) {
                org.w3c.dom.Element item = (org.w3c.dom.Element) items.item(i);

                // --- LÓGICA DE QUANTIDADE ---
                String qty = item.getAttribute("QUANTIDADE");

                // Se tem referência e a quantidade é zero (0, 0.0, 0.00...)
                if (isZero(qty)) {
                    item.setAttribute("QUANTIDADE", "1");
                    qtyFixCount++;
                    changed = true;
                }

                // --- LÓGICA DE PRECO_TOTAL ---
                String price = item.getAttribute("PRECO_TOTAL");
                if (isZero(price)) {
                    item.setAttribute("PRECO_TOTAL", "0.10");
                    priceFixCount++;
                    changed = true;
                }
            }

            if (changed) {
                arquivoService.salvarArquivo(doc, file);
                java.util.List<String> msgs = new java.util.ArrayList<>();
                if (qtyFixCount > 0) msgs.add("QUANTIDADE");
                if (priceFixCount > 0) msgs.add("PREÇO");
                
                return String.join(" | ", msgs);
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Método auxiliar para verificar se o valor é zero (0, 0.0, 0.00, etc)
    private boolean isZero(String val) {
        if (val == null || val.trim().isEmpty()) return false;

        try {
            return Double.parseDouble(val.trim()) == 0;
        } 
        catch (NumberFormatException e) {
            return false;
        }
    }
}