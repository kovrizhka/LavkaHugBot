package ru.lavka.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode(exclude = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "app_photo")
@Convert(attributeName = "jsonb", converter = JsonBinaryType.class)
public class AppPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String telegramFileId;

    @OneToOne
    private BinaryContent binaryContent;
    private Integer fileSize;
}
