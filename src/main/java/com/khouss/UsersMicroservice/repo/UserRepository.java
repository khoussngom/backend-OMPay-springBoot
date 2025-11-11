package com.khouss.UsersMicroservice.repo;

import com.khouss.UsersMicroservice.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, UUID> {
    User findByUsername(String username);
    User findByTelephone(String telephone);

    boolean existsByUsername(String username);

    // Requête native qui récupère les colonnes users + clients + admins en jointure
    @Query(value = "SELECT u.id as u_id, u.username as u_username, u.password as u_password, u.enabled as u_enabled, " +
            "c.id as c_id, c.email as c_email, c.prenom as c_prenom, c.nom as c_nom, c.adresse as c_adresse, c.telephone as c_telephone, " +
            "a.id as a_id, a.prenom as a_admin_prenom, a.nom as a_admin_nom " +
            "FROM users u " +
            "LEFT JOIN clients c ON c.user_id = u.id " +
            "LEFT JOIN admins a ON a.user_id = u.id", nativeQuery = true)
    List<Object[]> findAllUsersWithClientAndAdminNative();

    @Query(value = "SELECT u.id as u_id, u.username as u_username, u.password as u_password, u.enabled as u_enabled, " +
            "c.id as c_id, c.email as c_email, c.prenom as c_prenom, c.nom as c_nom, c.adresse as c_adresse, c.telephone as c_telephone, " +
            "a.id as a_id, a.prenom as a_admin_prenom, a.nom as a_admin_nom " +
            "FROM users u " +
            "LEFT JOIN clients c ON c.user_id = u.id " +
            "LEFT JOIN admins a ON a.user_id = u.id " +
            // comparaison via CAST en varchar pour éviter mismatch type character vs uuid
            "WHERE CAST(u.id AS varchar) = CAST(:id AS varchar)", nativeQuery = true)
    Optional<Object[]> findUserWithClientAndAdminById(@Param("id") UUID id);

}
