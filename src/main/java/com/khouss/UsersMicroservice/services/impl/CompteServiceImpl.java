package com.khouss.UsersMicroservice.services.impl;

import com.khouss.UsersMicroservice.constant.OMPayMessages;
import com.khouss.UsersMicroservice.entities.Client;
import com.khouss.UsersMicroservice.entities.Compte;
import com.khouss.UsersMicroservice.entities.Transaction;
import com.khouss.UsersMicroservice.entities.Transaction.Type;
import com.khouss.UsersMicroservice.events.UserCreatedEvent;
import com.khouss.UsersMicroservice.exception.*;
import com.khouss.UsersMicroservice.repo.CompteRepository;
import com.khouss.UsersMicroservice.repo.TransactionRepository;
import com.khouss.UsersMicroservice.repo.ClientRepository;
import com.khouss.UsersMicroservice.repo.UserRepository;
import com.khouss.UsersMicroservice.services.ClientService;
import com.khouss.UsersMicroservice.services.CompteService;
import com.khouss.UsersMicroservice.services.SmsService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CompteServiceImpl implements CompteService {

    private final CompteRepository compteRepository;
    private final TransactionRepository transactionRepository;
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;


    private final List<String> codesMarchandsValides = List.of("MRC001", "MRC002");

    @Override
    public Compte creerCompte(Compte compte) {
        compteRepository.findByNumeroTelephone(compte.getNumeroTelephone())
                .ifPresent(c -> { throw new CompteAlreadyExistsException(OMPayMessages.COMPTE_DEJA_EXISTANT.getMessage()); });
        compte.setDateOuverture(LocalDate.now());
        Compte saved = compteRepository.save(compte);
        saved.setSolde(BigDecimal.ZERO);
        return saved;
    }

    @Override
    public Compte creationAutomatiquePourUser(UUID userId, String numeroTelephone, UUID clientId) {
        String numero = (numeroTelephone == null || numeroTelephone.isBlank()) ? generatePhoneForUser(userId) : numeroTelephone;
        compteRepository.findByNumeroTelephone(numero)
                .ifPresent(c -> { throw new CompteAlreadyExistsException(OMPayMessages.COMPTE_DEJA_EXISTANT.getMessage()); });
        Compte compte = new Compte();
        compte.setNumeroTelephone(numero);
        compte.setIdClient(clientId);
        compte.setIdUser(userId); // nouveau
        compte.setDateOuverture(LocalDate.now());
        Compte saved = compteRepository.save(compte);
        saved.setSolde(BigDecimal.ZERO);
        return saved;
    }


    @Override
    public Compte creerComptePourClient(UUID clientId, String numeroTelephone) {
        var client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException(OMPayMessages.CLIENT_INEXISTANT.getMessage()));

        if (client.getNumeroTelephone() != null && !client.getNumeroTelephone().equals(numeroTelephone)) {
            throw new NumeroInvalideException(OMPayMessages.NUMERO_INVALIDE.getMessage());
        }
        compteRepository.findByNumeroTelephone(numeroTelephone)
                .ifPresent(c -> { throw new CompteAlreadyExistsException(OMPayMessages.COMPTE_DEJA_EXISTANT.getMessage()); });
        Compte compte = new Compte();
        compte.setNumeroTelephone(numeroTelephone);
        compte.setIdClient(clientId);
        compte.setIdUser(client.getUserId()); // nouveau
        compte.setDateOuverture(LocalDate.now());
        Compte saved = compteRepository.save(compte);
        saved.setSolde(BigDecimal.ZERO);
        return saved;
    }

    @Override
    public Compte creerComptePourUsername(String username, String numeroTelephone) {
        var user = userRepository.findByUsername(username);
        if (user == null || user.getId() == null) {
            throw new ClientNotFoundException(OMPayMessages.CLIENT_INEXISTANT.getMessage());
        }
        var client = clientRepository.findByUserId(user.getId());
        if (client == null) {
            throw new ClientNotFoundException(OMPayMessages.CLIENT_INEXISTANT.getMessage());
        }
        if (client.getNumeroTelephone() != null && !client.getNumeroTelephone().equals(numeroTelephone)) {
            throw new NumeroInvalideException(OMPayMessages.NUMERO_INVALIDE.getMessage());
        }
        compteRepository.findByNumeroTelephone(numeroTelephone)
                .ifPresent(c -> { throw new CompteAlreadyExistsException(OMPayMessages.COMPTE_DEJA_EXISTANT.getMessage()); });
        Compte compte = new Compte();
        compte.setNumeroTelephone(numeroTelephone);
        compte.setIdClient(client.getId());
        compte.setIdUser(user.getId());
        compte.setDateOuverture(LocalDate.now());
        Compte saved = compteRepository.save(compte);
        saved.setSolde(BigDecimal.ZERO);
        return saved;
    }

    @Override
    public Compte creerCompteMajNumeroPourUsername(String username, String ancienNumero, String nouveauNumero) {
        var user = userRepository.findByUsername(username);
        if (user == null || user.getId() == null) {
            throw new ClientNotFoundException(OMPayMessages.CLIENT_INEXISTANT.getMessage());
        }
        var client = clientRepository.findByUserId(user.getId());
        if (client == null) {
            throw new ClientNotFoundException(OMPayMessages.CLIENT_INEXISTANT.getMessage());
        }

        if (StringUtils.hasText(client.getNumeroTelephone())) {
            if (!client.getNumeroTelephone().equals(ancienNumero)) {
                throw new NumeroInvalideException(OMPayMessages.NUMERO_INVALIDE.getMessage());
            }
        }

        String nouveau = StringUtils.hasText(nouveauNumero) ? nouveauNumero : ancienNumero;
        if (!StringUtils.hasText(nouveau)) {
            throw new IllegalArgumentException("Nouveau numéro manquant");
        }

        compteRepository.findByNumeroTelephone(nouveau)
                .ifPresent(c -> { throw new CompteAlreadyExistsException(OMPayMessages.COMPTE_DEJA_EXISTANT.getMessage()); });

        if (!nouveau.equals(client.getNumeroTelephone())) {
            client.setNumeroTelephone(nouveau);
            clientRepository.save(client);
        }
        Compte compte = new Compte();
        compte.setNumeroTelephone(nouveau);
        compte.setIdClient(client.getId());
        compte.setIdUser(user.getId()); // nouveau
        compte.setDateOuverture(LocalDate.now());
        Compte saved = compteRepository.save(compte);
        saved.setSolde(BigDecimal.ZERO);
        return saved;
    }

    private String generatePhoneForUser(UUID userId) {

        return "77" + userId.toString().replaceAll("-", "").substring(0, 7);
    }

    @Override
    public Compte deposer(UUID compteId, BigDecimal montant) {
        Compte compte = compteRepository.findById(compteId)
                .orElseThrow(() -> new CompteNotFoundException(OMPayMessages.COMPTE_INEXISTANT.getMessage()));
        if (montant.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Montant invalide");
        }
        Transaction tx = new Transaction();
        tx.setCompte(compte);
        tx.setCompteId(compte.getId());
        tx.setType(Type.DEPOT);
        tx.setMontant(montant);
        tx.setDate(LocalDateTime.now());
        transactionRepository.save(tx);
        compte.setSolde(calculerSolde(compte.getId()));
        return compte;
    }

    @Override
    public Compte deposerParNumero(String numeroTelephone, BigDecimal montant) {
        if (montant == null || montant.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Montant invalide");
        }
        Compte compte = compteRepository.findByNumeroTelephone(numeroTelephone)
                .orElseThrow(() -> new CompteNotFoundException(OMPayMessages.COMPTE_INEXISTANT.getMessage()));
        Transaction tx = new Transaction();
        tx.setCompte(compte);
        tx.setCompteId(compte.getId());
        tx.setType(Type.DEPOT);
        tx.setMontant(montant);
        tx.setDate(LocalDateTime.now());
        transactionRepository.save(tx);
        compte.setSolde(calculerSolde(compte.getId()));
        return compte;
    }

    @Override
    public Compte transfert(UUID compteSource, UUID compteDest, BigDecimal montant) {
        if (compteSource.equals(compteDest)) {
            throw new IllegalArgumentException("Compte source et destinataire identiques");
        }
        Compte source = compteRepository.findById(compteSource)
                .orElseThrow(() -> new CompteNotFoundException(OMPayMessages.COMPTE_INEXISTANT.getMessage()));
        Compte dest = compteRepository.findById(compteDest)
                .orElseThrow(() -> new DestinataireNotFoundException(OMPayMessages.COMPTE_DESTINATAIRE_INEXISTANT.getMessage()));

        BigDecimal soldeSource = calculerSolde(source.getId());
        if (soldeSource.compareTo(montant) < 0) {
            throw new SoldeInsuffisantException(OMPayMessages.SOLDE_INSUFFISANT.getMessage());
        }
        Transaction tx = new Transaction();
        tx.setCompte(source);
        tx.setCompteId(source.getId());
        tx.setCompteDestId(dest.getId());
        tx.setType(Type.TRANSFERT);
        tx.setMontant(montant);
        tx.setDate(LocalDateTime.now());
        transactionRepository.save(tx);
        source.setSolde(calculerSolde(source.getId()));
        return source;
    }

    @Override
    public Compte paiement(UUID compteId, String codeMarchand, BigDecimal montant) {
        Compte compte = compteRepository.findById(compteId)
                .orElseThrow(() -> new CompteNotFoundException(OMPayMessages.COMPTE_INEXISTANT.getMessage()));
        if (!codesMarchandsValides.contains(codeMarchand)) {
            throw new CodeMarchandNotFoundException(OMPayMessages.CODE_MARCHAND_INEXISTANT.getMessage());
        }
        BigDecimal solde = calculerSolde(compte.getId());
        if (solde.compareTo(montant) < 0) {
            throw new SoldeInsuffisantException(OMPayMessages.SOLDE_INSUFFISANT.getMessage());
        }
        Transaction tx = new Transaction();
        tx.setCompte(compte);
        tx.setCompteId(compte.getId());
        tx.setType(Type.PAIEMENT);
        tx.setMontant(montant);
        tx.setCodeMarchand(codeMarchand);
        tx.setDate(LocalDateTime.now());
        transactionRepository.save(tx);
        compte.setSolde(calculerSolde(compte.getId()));
        return compte;
    }

    @Override
    public Compte transfertParNumero(String sourceNumero, String destNumero, BigDecimal montant) {
        if (sourceNumero.equals(destNumero)) {
            throw new IllegalArgumentException("Compte source et destinataire identiques");
        }
        Compte source = compteRepository.findByNumeroTelephone(sourceNumero)
                .orElseThrow(() -> new CompteNotFoundException(OMPayMessages.COMPTE_INEXISTANT.getMessage()));
        Compte dest = compteRepository.findByNumeroTelephone(destNumero)
                .orElseThrow(() -> new DestinataireNotFoundException(OMPayMessages.COMPTE_DESTINATAIRE_INEXISTANT.getMessage()));
        BigDecimal soldeSource = calculerSolde(source.getId());
        if (soldeSource.compareTo(montant) < 0) {
            throw new SoldeInsuffisantException(OMPayMessages.SOLDE_INSUFFISANT.getMessage());
        }
        Transaction tx = new Transaction();
        tx.setCompte(source);
        tx.setCompteId(source.getId());
        tx.setCompteDestId(dest.getId());
        tx.setType(Type.TRANSFERT);
        tx.setMontant(montant);
        tx.setDate(LocalDateTime.now());
        transactionRepository.save(tx);
        source.setSolde(calculerSolde(source.getId()));
        return source;
    }

    @Override
    public Compte paiementParNumero(String numeroTelephone, String codeMarchand, BigDecimal montant) {
        Compte compte = compteRepository.findByNumeroTelephone(numeroTelephone)
                .orElseThrow(() -> new CompteNotFoundException(OMPayMessages.COMPTE_INEXISTANT.getMessage()));
        if (!codesMarchandsValides.contains(codeMarchand)) {
            throw new CodeMarchandNotFoundException(OMPayMessages.CODE_MARCHAND_INEXISTANT.getMessage());
        }
        BigDecimal solde = calculerSolde(compte.getId());
        if (solde.compareTo(montant) < 0) {
            throw new SoldeInsuffisantException(OMPayMessages.SOLDE_INSUFFISANT.getMessage());
        }
        Transaction tx = new Transaction();
        tx.setCompte(compte);
        tx.setCompteId(compte.getId());
        tx.setType(Type.PAIEMENT);
        tx.setMontant(montant);
        tx.setCodeMarchand(codeMarchand);
        tx.setDate(LocalDateTime.now());
        transactionRepository.save(tx);
        compte.setSolde(calculerSolde(compte.getId()));
        return compte;
    }

    @Override
    public BigDecimal calculerSolde(UUID compteId) {
        List<Transaction> sorties = transactionRepository.findByCompteId(compteId);
        List<Transaction> entrees = transactionRepository.findByCompteDestId(compteId);

        BigDecimal depots = sorties.stream()
                .filter(t -> t.getType() == Type.DEPOT)
                .map(Transaction::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal transfertsSortants = sorties.stream()
                .filter(t -> t.getType() == Type.TRANSFERT)
                .map(Transaction::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal paiements = sorties.stream()
                .filter(t -> t.getType() == Type.PAIEMENT)
                .map(Transaction::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal transfertsEntrants = entrees.stream()
                .filter(t -> t.getType() == Type.TRANSFERT)
                .map(Transaction::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return depots.add(transfertsEntrants).subtract(transfertsSortants).subtract(paiements);
    }

    @Override
    public List<Compte> listerComptes() {
        List<Compte> comptes = compteRepository.findAll();

        return comptes.stream().map(c -> {
            try {
                c.setSolde(calculerSolde(c.getId()));
            } catch (Exception ignored) {}
            return c;
        }).collect(Collectors.toList());
    }

    @Service
    public static class ClientServiceImpl implements ClientService {
    
        private static final Logger log = LoggerFactory.getLogger(ClientServiceImpl.class);
    
    
        @Autowired
        ClientRepository clientRepository;
    
    
    
        @Override
        public List<Client> findAll() {
            return clientRepository.findAll();
        }
    
        @Override
        public Client createClient(Client client) {
            return clientRepository.save(client);
        }
    
        @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public void onUserCreated(UserCreatedEvent userCreatedEvent) {
            log.info("Received UserCreatedEvent: {}", userCreatedEvent == null ? "null" : userCreatedEvent.getUser());
            if (userCreatedEvent == null || userCreatedEvent.getUser() == null) {
                log.warn("UserCreatedEvent or user is null, skipping client creation");
                return;
            }
    
            Client client = new Client();
    
            Object idObj = userCreatedEvent.getUser().getId();
            if (idObj != null) {
                UUID userIdUuid = null;
                if (idObj instanceof UUID) {
                    userIdUuid = (UUID) idObj;
                } else {
                    try {
                        userIdUuid = UUID.fromString(idObj.toString());
                    } catch (IllegalArgumentException e) {
                        log.warn("User id is not a valid UUID: {}", idObj);
                    }
                }
                client.setUserId(userIdUuid);
            }
    
    
            client.setEmail(userCreatedEvent.getUser().getEmail());
            client.setPrenom(userCreatedEvent.getUser().getPrenom() != null ? userCreatedEvent.getUser().getPrenom() : "");
            client.setNom(userCreatedEvent.getUser().getNom() != null ? userCreatedEvent.getUser().getNom() : "");
            client.setAdresse(userCreatedEvent.getUser().getAdresse() != null ? userCreatedEvent.getUser().getAdresse() : "");
            client.setTelephone(userCreatedEvent.getUser().getTelephone() != null ? userCreatedEvent.getUser().getTelephone() : "");
    
            try {
                Client saved = clientRepository.save(client);
                log.info("Client created for userId={} with clientId={}", client.getUserId(), saved.getId());
            } catch (Exception e) {
                log.error("Failed to save Client for userId={}: {}", client.getUserId(), e.getMessage(), e);
            }
        }
    
        @Override
        public Client findByUserId(String userId) {
            try {
                return clientRepository.findByUserId(UUID.fromString(userId));
            } catch (Exception e) {
                return null;
            }
        }
    
        @Override
        public Client findByEmail(String email) {
            return clientRepository.findByEmail(email);
        }
    }
}
