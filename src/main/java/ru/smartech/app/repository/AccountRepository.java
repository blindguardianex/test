package ru.smartech.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.smartech.app.entity.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
}
