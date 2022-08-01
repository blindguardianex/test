package ru.smartech.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.smartech.app.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
