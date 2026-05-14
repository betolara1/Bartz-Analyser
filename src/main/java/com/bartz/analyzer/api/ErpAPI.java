package com.bartz.analyzer.api;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List; // Importante!
import java.util.stream.Collectors; // Importante!
import java.util.stream.Stream;

public class ErpAPI {

    
    

    // BUSCA EM API DE CORES (Se type === 'CORINGA')
    String corCod = "http://192.168.1.10:8085/cores/search?codigo=";
    String corDesc = "http://192.168.1.10:8085/cores/search?descricao=";
    String cor = "http://192.168.1.10:8085/cores";

    // 2. BUSCA PELO ITEM NO ERP (CODIGO E/OU DESCRIÇÃO)
    String itemCod = "http://192.168.1.10:8085/itens/search?codigo=";
    String itemDesc = "http://192.168.1.10:8085/itens/search?descricao=";

    // BUSCA NO CVS OS PAINEIS
    public static List<String> codigoPanel(String descricao){
        String csvPath = "\\\\192.168.1.10\\Promob\\codigos_paineis.csv";
        Path path = Paths.get(csvPath);

        
        // O 'try-with-resources' garante que o arquivo seja fechado automaticamente
        try(Stream<String> lines = Files.lines(path)){

            // Filtramos as linhas que contém o termo. 
            // Pulamos a primeira linha (cabeçalho) com .skip(1)
            String busca = (descricao == null) ? "" : descricao.trim();

            return lines.skip(1)
                        .filter(line -> line.toLowerCase().contains(busca.toLowerCase()))
                        .collect(Collectors.toList());
        }
        catch(IOException e){
            return List.of("ERRO AO LER CSV: " + e.getMessage());
        }

    }
}
