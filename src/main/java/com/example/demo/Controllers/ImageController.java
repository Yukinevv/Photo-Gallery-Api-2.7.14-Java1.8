package com.example.demo.Controllers;

import com.example.demo.Models.Image;
import com.example.demo.Services.ImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

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
                                                         @RequestBody MultipartFile image) {
        try {
            return imageService.saveImage(image.getOriginalFilename(), image.getBytes(), login, category);
        } catch (Exception e) {
            System.out.println("Blad: " + e.getLocalizedMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, String>> deleteImage(@PathVariable("id") String id) {
        return imageService.deleteImage(id);
    }

    @PutMapping("/editFilename/{id}/{newFilename}")
    public ResponseEntity<Map<String, String>> editFilename(@PathVariable("id") String id, @PathVariable("newFilename") String newFilename) {
        return imageService.editFilename(id, newFilename);
    }
}
