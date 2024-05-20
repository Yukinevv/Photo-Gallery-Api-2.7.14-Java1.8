package com.example.demo.Controllers;

import com.example.demo.Models.Image;
import com.example.demo.Services.ImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/images")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping
    public List<Image> fetchAllImages() {
        return imageService.getAllImages();
    }

    @GetMapping("/{login}/{category}")
    public List<Image> fetchAllUserImages(@PathVariable("login") String login, @PathVariable("category") String category) {
        return imageService.getAllUserImages(login, category);
    }

    @PostMapping("/upload/{login}/{category}")
    public ResponseEntity<Map<String, String>> saveImage(@PathVariable("login") String login, @PathVariable String category,
                                                         @RequestParam("image") MultipartFile image) {
        try {
            String imageId = imageService.saveImage(image, login, category);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Image uploaded successfully");
            response.put("id", imageId);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("Blad: " + e.getLocalizedMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, String>> deleteImage(@PathVariable("id") String id) {
        try {
            boolean deleted = imageService.deleteImage(id);
            if (deleted) {
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

    @PutMapping("/editFilename/{id}/{newFilename}")
    public ResponseEntity<Map<String, String>> editFilename(@PathVariable("id") String id, @PathVariable("newFilename") String newFilename) {
        try {
            boolean updated = imageService.editFilename(id, newFilename);
            if (updated) {
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

    @GetMapping("/{id}")
    public ResponseEntity<Image> getImage(@PathVariable String id) {
        try {
            Optional<Image> image = imageService.getImageById(id);
            if (image.isPresent()) {
                return ResponseEntity.ok(image.get());
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (IOException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
