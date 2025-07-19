package com.example.bankcards.entity;

import com.example.bankcards.entity.enums.BlockRequestStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "block_requests")
public class BlockRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @Column(nullable = false, length = 512)
    private String reason;

    @ManyToOne
    @JoinColumn(name = "requester_user_id", nullable = false)
    private User requester;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private BlockRequestStatus status;

    @ManyToOne
    @JoinColumn(name = "handler_user_id")
    private User handler;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
