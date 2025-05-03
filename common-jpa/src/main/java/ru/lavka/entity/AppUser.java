package ru.lavka.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.lavka.entity.enums.UserState;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(exclude = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "app_user")
@Convert(attributeName = "jsonb", converter = JsonBinaryType.class)
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long telegramUserId;

    @CreationTimestamp
    private LocalDateTime firstLoginDateTime;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private Boolean isActivated;

    @Enumerated(EnumType.STRING)
    private UserState userState;
}
