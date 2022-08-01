package ru.smartech.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.smartech.app.entity.Email;

@Repository
public interface EmailRepository extends JpaRepository<Email, Long> {
}
