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
@Table(name = "app_document")
@Convert(attributeName = "jsonb", converter = JsonBinaryType.class)
public class AppDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String telegramFileId;
    private String docName;

    @OneToOne
    private BinaryContent binaryContent;
    private String mimeType;
    private Long fileSize;
}
