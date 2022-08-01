package ru.smartech.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.smartech.app.entity.Phone;

@Repository
public interface PhoneRepository extends JpaRepository<Phone, Long> {
}
