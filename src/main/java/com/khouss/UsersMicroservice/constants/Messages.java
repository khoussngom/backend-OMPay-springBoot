package com.khouss.UsersMicroservice.constants;

public enum Messages {
    PASSWORD_EMPTY("mot de passe ne peut pas être vide"),
    RESPONSE_OK("ok"),
    ROLE_USER("USER"),
    ROLE_ADMIN("ADMIN"),
    USERNAME_EXISTS("le nom d'utilisateur existe déjà");

    private final String text;

    Messages(String text) {

        this.text = text;
    }

    public String getText() {
        return text;
    }
}

