package com.routeapp.routebackend.user.repository;

import com.routeapp.routebackend.user.entity.User;
import com.routeapp.routebackend.user.entity.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

// Not: User üzerinde genel @SQLRestriction yok — admin banlı/deaktif kullanıcıları da görebilmeli.
public interface UserRepository extends JpaRepository<User, UUID> {

    // Login / Google girişi / uniqueness kontrolü
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByGoogleId(String googleId);

    // Kayıt (UC-1) ve profil güncelleme (UC-7, UC-8) benzersizlik kontrolleri
    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    // Email/username değiştirirken kendi kaydını çarpışma saymamak için
    boolean existsByEmailAndIdNot(String email, UUID id);

    boolean existsByUsernameAndIdNot(String username, UUID id);

    // UC-12: kullanıcı adı/isimle arama, sadece ACTIVE kullanıcılarda, email ile arama yok (gizlilik)
    @Query("""
            SELECT u FROM User u
            WHERE u.status = com.routeapp.routebackend.user.entity.UserStatus.ACTIVE
              AND (
                   LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :query, '%'))
              )
            """)
    Page<User> searchActiveUsers(@Param("query") String query, Pageable pageable);

    // Admin panel: durum/rol bazlı listeleme ve sayım (UC-29, UC-30, UC-31)
    Page<User> findByStatus(UserStatus status, Pageable pageable);

    long countByStatus(UserStatus status);

    // UC-11: 30 gün dolan, hard-delete edilmeyi bekleyen kullanıcılar (gerçek silme servis katmanında yapılır)
    List<User> findByStatusAndDeletionRequestedAtBefore(UserStatus status, Instant threshold);
}