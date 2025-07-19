package com.example.bankcards.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", length = 64, nullable = false, unique = true)
    private String email;

    @Column(name = "password", length = 128, nullable = false)
    private String password;

    @Column(name = "first_name", length = 64, nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 64)
    private String lastName;

    @ManyToMany
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"))
    private Collection<Role> roles;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Card> cards = new ArrayList<>();

    @Column(name = "non_locked", nullable = false)
    private Boolean nonLocked = true;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
