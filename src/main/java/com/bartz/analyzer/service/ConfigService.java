package com.bartz.analyzer.service;

import java.util.prefs.Preferences;

public class ConfigService {

    // 1. Criamos uma instância de Preferences associada a esta classe
    private static final Preferences prefs = Preferences.userNodeForPackage(ConfigService.class);

    // 2. Método para SALVAR um valor
    public static void saveValue(String key, String value){
        prefs.put(key, value);
    }

    // 3. Método para RECUPERAR um valor (se não existir, retorna vazio "")
    public static String getValue(String key){
        return prefs.get(key, "");
    }

    // 4. Chaves constantes: Isso evita que você erre o nome do "crachá" do dado
    public static final String PATH_INPUT = "path_input";
    public static final String PATH_EXPORT = "path_export";
    public static final String PATH_OK = "path_ok";
    public static final String PATH_ERROR = "path_error";
    public static final String PATH_DRAW = "path_draw";
    public static final String PATH_LOG_ERROR = "path_log_error";
    public static final String PATH_LOG_OK = "path_log_ok";
}


/*
- Preferences: É como um "banco de dados" minúsculo de chave e valor.
- put(key, value): Salva a informação.
- get(key, defaultValue): Tenta ler. Se o usuário nunca salvou nada, ele usa o "valor padrão" que você definiu 
    (no caso, uma string vazia "").
 */