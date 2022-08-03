package ru.smartech.app.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.smartech.app.entity.User;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(nativeQuery = true,
            value = "select u.* from \"user\" u" +
                    "    join phone_data pd" +
                    "        on pd.user_id = u.id" +
                    "    where pd.phone = :phone")
    Optional<User> findByPhone(@Param("phone") String phone);

    @Query(nativeQuery = true,
            value = "select u.* from \"user\" u" +
                    "    join email_data ed" +
                    "        on ed.user_id = u.id" +
                    "    where ed.email = :email")
    Optional<User> findByEmail(@Param("email") String email);

    @Query(nativeQuery = true,
            value = "select * from \"user\" u where u.birth_date >= :dateFrom",
            countQuery = "select count(*) from \"user\" u where u.birth_date >= :dateFrom")
    Page<User> getByBirthDateAfter(@Param("dateFrom") LocalDate dateFrom, Pageable pageable);

    @Query(nativeQuery = true,
            value = "select * from \"user\" where name like :namePrefix%",
            countQuery = "select count(*) from \"user\" where name like :namePrefix%")
    Page<User> getByNameLike(@Param("namePrefix") String namePrefix, Pageable pageable);
}
