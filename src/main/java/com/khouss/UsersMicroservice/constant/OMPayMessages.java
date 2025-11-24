package com.khouss.UsersMicroservice.constant;

public enum OMPayMessages {
    // Erreurs
    COMPTE_INEXISTANT("Compte inexistant"),
    COMPTE_DEJA_EXISTANT("Compte déjà existant"),
    COMPTE_DESTINATAIRE_INEXISTANT("Compte destinataire inexistant"),
    CODE_MARCHAND_INEXISTANT("Code marchand inexistant"),
    SOLDE_INSUFFISANT("Solde insuffisant"),
    CLIENT_INEXISTANT("Client inexistant, veuillez passer par la création d'utilisateur"),
    NUMERO_INVALIDE("Numéro invalide: non lié à ce client"),

    // Succès
    DEPOT_SUCCES("Dépôt effectué avec succès"),
    TRANSFERT_SUCCES("Transfert effectué avec succès"),
    PAIEMENT_SUCCES("Paiement effectué avec succès"),
    COMPTE_CREE_SUCCES("Compte créé avec succès"),
    LISTE_COMPTES("Liste des comptes récupérée avec succès"),
    LISTE_TRANSFERTS("Liste des transferts récupérée avec succès");

    private final String message;

    OMPayMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
