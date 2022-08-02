package ru.smartech.app.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@Entity
@NoArgsConstructor
@Table(name = "email_data")
public class Email {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotNull
    @ManyToOne(optional = false, targetEntity = User.class)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
