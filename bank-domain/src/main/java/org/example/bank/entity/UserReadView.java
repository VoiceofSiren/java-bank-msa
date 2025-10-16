package org.example.bank.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "user_read_views")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserReadView implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    // 아래의 필드들은 읽기 최적화를 위해 추가
    @Column(nullable = false)
    private Integer accountCount = 0;

    @Column(nullable = false)
    private BigDecimal totalBalance = BigDecimal.ZERO;
}
