package ru.smartech.app.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.*;

@Data
@Accessors(chain = true)
@Entity
@NoArgsConstructor
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank
    @Column(name = "password", nullable = false)
    private String password;

    @NotNull
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "dd.MM.yyyy")
    @Column(name = "birth_date", unique = true, nullable = false)
    private LocalDate birthDate;

    @Transient
    private Account account;
    @Transient
    private Set<Email> emails;
    @Transient
    private Set<Phone> phones;

    public Optional<Account> loadAccount(){
        if (this.account == null)
            return Optional.empty();
        else
            return Optional.of(account);
    }

    public Set<Email> loadEmails(){
        return this.emails;
    }

    public Set<Phone> loadPhones(){
        return this.phones;
    }

    public User(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.password = user.getPassword();
        this.birthDate = user.getBirthDate();
        this.emails = user.getEmails();
        this.phones = user.getPhones();
    }
}
