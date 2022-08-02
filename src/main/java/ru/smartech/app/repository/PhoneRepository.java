package ru.smartech.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.smartech.app.entity.Email;
import ru.smartech.app.entity.Phone;

import java.util.Optional;

@Repository
public interface PhoneRepository extends JpaRepository<Phone, Long> {

    @Query(nativeQuery = true,
            value = "SELECT (*) FROM phone_data e WHERE e.phone = :phone")
    Optional<Phone> findByPhone(@Param("phone") String phone);
}
