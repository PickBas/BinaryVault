package com.saied.binaryvault.appuser;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import static jakarta.persistence.GenerationType.SEQUENCE;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity(name = "app_user")
@Table(
    name = "app_user",
    uniqueConstraints = {
        @UniqueConstraint(name = "appuser_email_unique", columnNames = "email"),
        @UniqueConstraint(name = "app_user_username", columnNames = "username")
    }
)
@Builder
@Getter @Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class AppUser {
    @Id
    @SequenceGenerator(
        name = "appuser_id_sequence",
        sequenceName = "appuser_id_sequence"
    )
    @GeneratedValue(
        strategy = SEQUENCE,
        generator = "appuser_id_sequence"
    )
    @EqualsAndHashCode.Exclude
    private Long id;
    @Column(
        name = "username",
        nullable = false
    )
    private String username;
    @Column(
        name = "email",
        nullable = false
    )
    private String email;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(
        name = "password",
        nullable = false,
        columnDefinition = "TEXT"
    )
    @JsonIgnore
    private String password;
}
