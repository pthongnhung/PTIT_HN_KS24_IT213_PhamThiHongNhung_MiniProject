package re.com.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "company_document")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "file_type")
    private String fileType;

    private Long size;

    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;

    @Column(nullable = false)
    @Builder.Default
    private String status = "PENDING";
}
