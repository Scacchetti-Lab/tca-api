package com.api.tca.config.security;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class PasswordGenerator {

    private static final String LOWER_CASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPER_CASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMBERS = "0123456789";
    private static final String SPECIAL_CHARS = "£$&()*+[]@#^-_!?";

    private static final String[] CHAR_CATEGORIES = {LOWER_CASE, UPPER_CASE, NUMBERS, SPECIAL_CHARS};

    private static final int DEFAULT_LENGTH = 8;

    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Gera uma senha com o tamanho padrão (8 caracteres),
     * garantindo pelo menos um caractere de cada categoria.
     */
    public String generate() {
        return generate(DEFAULT_LENGTH);
    }

    /**
     * Gera uma senha aleatória com o tamanho informado.
     *
     * @param length tamanho desejado da senha (mínimo = número de categorias)
     * @return senha gerada
     */
    public String generate(int length) {
        if (length < CHAR_CATEGORIES.length) {
            throw new IllegalArgumentException(
                    "O tamanho da senha deve ser >= " + CHAR_CATEGORIES.length
                            + " para incluir todas as categorias de caracteres."
            );
        }

        List<Character> passwordChars = new ArrayList<>(length);

        // Garante pelo menos um caractere de cada categoria
        for (String category : CHAR_CATEGORIES) {
            passwordChars.add(randomCharFrom(category));
        }

        // Preenche o restante com categorias aleatórias
        for (int i = CHAR_CATEGORIES.length; i < length; i++) {
            String category = CHAR_CATEGORIES[secureRandom.nextInt(CHAR_CATEGORIES.length)];
            passwordChars.add(randomCharFrom(category));
        }

        // Embaralha para não deixar os primeiros caracteres em ordem fixa de categoria
        Collections.shuffle(passwordChars, secureRandom);

        StringBuilder password = new StringBuilder(length);
        for (char c : passwordChars) {
            password.append(c);
        }

        return password.toString();
    }

    private char randomCharFrom(String category) {
        int index = secureRandom.nextInt(category.length());
        return category.charAt(index);
    }
}
