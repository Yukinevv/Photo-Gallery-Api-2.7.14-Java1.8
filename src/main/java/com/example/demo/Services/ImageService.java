package com.example.demo.Services;

import com.example.demo.Models.Image;
import com.example.demo.Repositories.ImageRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ImageService {

    private final ImageRepository imageRepository;

    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public List<Image> getAllImages() {
        return imageRepository.findAll();
    }

    public List<Image> getAllUserImages(String login, String category) {
        return imageRepository.findAllByLoginAndCategory(login, category);
    }

    public ResponseEntity<Map<String, String>> saveImage(String filename, byte[] data, String login, String category) {
        try {
            Image image = new Image(filename, data, login, category);
            imageRepository.save(image);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Image uploaded successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("Blad podczas zapisu obrazu do bazy: " + e.getLocalizedMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Map<String, String>> deleteImage(String id) {
        try {
            Optional<Image> image = imageRepository.findById(id);
            if (image.isPresent()) {
                imageRepository.delete((image.get()));

                Map<String, String> response = new HashMap<>();
                response.put("message", "Image deleted successfully");

                return ResponseEntity.ok(response);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            System.out.println("Error while deleting Image from database: " + e.getLocalizedMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Map<String, String>> editFilename(String id, String newFilename) {
        try {
            Optional<Image> image = imageRepository.findById(id);
            if (image.isPresent()) {
                Image _image = image.get();
                _image.setFilename(newFilename);
                imageRepository.save(_image);

                Map<String, String> response = new HashMap<>();
                response.put("message", "Image filename changed successfully");

                return ResponseEntity.ok(response);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            System.out.println("Error while changing Image filename: " + e.getLocalizedMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
