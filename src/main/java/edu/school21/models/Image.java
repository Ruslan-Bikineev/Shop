package edu.school21.models;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "images")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    private byte[] image;

    @OneToOne(mappedBy = "image", fetch = FetchType.LAZY)
    private Product product;

    public Image(byte[] image) {
        this.image = image;
    }

    public Image(UUID id, byte[] image) {
        this.id = id;
        this.image = image;
    }
}
