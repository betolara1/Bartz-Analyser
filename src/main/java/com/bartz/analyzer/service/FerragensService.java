package com.bartz.analyzer.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

@Service
public class FerragensService {

    private ArquivoService arquivo;
    public FerragensService(ArquivoService arquivo){
        this.arquivo = arquivo;
    }

    public boolean temFerragem(Document doc){
        try{
            NodeList maquinas = doc.getElementsByTagName("MAQUINA");

            //Cria uma lista única (Set) para guardar os IDs que existem no xml
            Set<String> idsFerragem = new HashSet<>();

            for (int i = 0; i < maquinas.getLength(); i++) {
                Element m = (Element) maquinas.item(i);

                // Percorre as tags encontradas e extrai o valor do atributo ID_PLUGIN.
                idsFerragem.add(m.getAttribute("ID_PLUGIN"));
            }

            // Lista de máquinas obrigatórias conforme o seu JS
            String[] obrigatorios = {"2530", "2534", "2341", "2525"};

            for (String id : obrigatorios) {
                if (!idsFerragem.contains(id)) {
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
