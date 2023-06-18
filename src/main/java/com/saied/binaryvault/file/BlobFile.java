package com.saied.binaryvault.file;

import com.saied.binaryvault.appuser.AppUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity(name = "BlobFile")
@Table(
    name = "file",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "path",
            columnNames = "path"
        )
    }
)
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter @Builder
public class BlobFile {

    @Id
    @SequenceGenerator(
        name = "file_id_seq",
        sequenceName = "file_id_seq",
        allocationSize = 1
    )
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "file_id_seq"
    )
    private Long id;

    @Column(
        name = "path",
        nullable = false,
        columnDefinition = "TEXT"
    )
    private String path;

    @ManyToOne
    @JoinColumn(
        name = "app_user_id",
        nullable = false,
        referencedColumnName = "id",
        foreignKey = @ForeignKey(
            name = "fk_app_user_id"
        )
    )
    private AppUser owner;

    @CreationTimestamp
    @Column(
        name = "updated_at",
        nullable = false,
        columnDefinition = "TIMESTAMP WITHOUT TIME ZONE"
    )
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(
        name = "created_at",
        nullable = false,
        columnDefinition = "TIMESTAMP WITHOUT TIME ZONE"
    )
    private LocalDateTime updatedAt;

}
