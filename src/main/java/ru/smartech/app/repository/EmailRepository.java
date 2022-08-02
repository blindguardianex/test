package ru.smartech.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.smartech.app.entity.Email;

import java.util.Optional;
import java.util.Set;

@Repository
public interface EmailRepository extends JpaRepository<Email, Long> {

    @Query(nativeQuery = true,
            value = "SELECT * FROM email_data e WHERE e.email = :email")
    Optional<Email> findByEmail(@Param("email") String email);

    @Query(nativeQuery = true,
            value = "SELECT * FROM email_data e WHERE e.user_id = :userId")
    Set<Email> findByUserId(@Param("userId") long userId);
}
