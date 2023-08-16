package com.example.demo.Repositories;

import com.example.demo.Models.Image;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ImageRepository extends MongoRepository<Image, String> {
    List<Image> findAllByLoginAndCategory(String login, String category);
}

