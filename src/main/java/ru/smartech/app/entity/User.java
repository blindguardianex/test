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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

//    @JsonIgnore
//    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
//    private Account account;

//    @JsonIgnore
//    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
//    private Set<Email> emails = new HashSet<>();

//    @JsonIgnore
//    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
//    private Set<Phone> phones = new HashSet<>();
}
