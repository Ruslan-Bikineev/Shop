package edu.school21.services;

import edu.school21.dto.MessageDto;
import edu.school21.models.Image;
import edu.school21.repositories.ImageRepository;
import edu.school21.utils.ImageUtils;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ImageService {
    private ImageUtils imageUtils;
    private ImageRepository imageRepository;

    public ImageService(ImageUtils imageUtils,
                        ImageRepository imageRepository) {
        this.imageUtils = imageUtils;
        this.imageRepository = imageRepository;
    }

    public Image findById(UUID id) {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Image with id: %s not found".formatted(id)));
        image.setImage(imageUtils.decompressImage(image.getImage()));
        return image;
    }

    public MessageDto saveImage(Image image) {
        image.setImage(imageUtils.compressImage(image.getImage()));
        imageRepository.save(image);
        return new MessageDto(image.getId(), "Image saved successfully");
    }

    public MessageDto putUpdateImage(Image image) {
        try {
            existsById(image.getId());
            image.setImage(imageUtils.compressImage(image.getImage()));
            imageRepository.save(image);
            return new MessageDto(image.getId(), "Image updated successfully");
        } catch (EntityNotFoundException e) {
            image.setId(null);
            return saveImage(image);
        }
    }

    public void deleteById(UUID id) {
        existsById(id);
        imageRepository.deleteById(id);
    }

    private void existsById(UUID id) {
        if (!imageRepository.existsById(id)) {
            throw new EntityNotFoundException("Image with id: %s not found".formatted(id));
        }
    }
}
