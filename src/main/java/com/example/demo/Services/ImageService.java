package com.example.demo.Services;

import com.example.demo.Models.Image;
import com.example.demo.Repositories.ImageRepository;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Logger;

@Service
public class ImageService {

    private static final Logger LOGGER = Logger.getLogger(ImageService.class.getName());

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFsOperations operations;

    public List<Image> getAllImages() {
        List<Image> images = new ArrayList<>();
        operations.find(new org.springframework.data.mongodb.core.query.Query())
                .forEach(file -> images.add(convertToImage(file)));
        return images;
    }

    public List<Image> getAllUserImages(String login, String category) {
        List<Image> images = new ArrayList<>();
        operations.find(new org.springframework.data.mongodb.core.query.Query().addCriteria(
                org.springframework.data.mongodb.core.query.Criteria.where("metadata.login").is(login)
                        .and("metadata.category").is(category)))
                .forEach(file -> images.add(convertToImage(file)));
        return images;
    }

    public String saveImage(MultipartFile file, String login, String category) throws IOException {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("login", login);
        metadata.put("category", category);

        GridFSUploadOptions options = new GridFSUploadOptions()
                .metadata(new org.bson.Document(metadata));

        LOGGER.info("Saving image to GridFS with filename: " + file.getOriginalFilename());
        ObjectId id = gridFsTemplate.store(file.getInputStream(), file.getOriginalFilename(), file.getContentType(), new org.bson.Document(metadata));
        LOGGER.info("Image saved with ID: " + id.toString());
        return id.toString();
    }

    public boolean deleteImage(String id) {
        GridFSFile gridFsFile = gridFsTemplate.findOne(new org.springframework.data.mongodb.core.query.Query().addCriteria(
                org.springframework.data.mongodb.core.query.Criteria.where("_id").is(id)));

        if (gridFsFile != null) {
            gridFsTemplate.delete(new org.springframework.data.mongodb.core.query.Query().addCriteria(
                    org.springframework.data.mongodb.core.query.Criteria.where("_id").is(id)));
            return true;
        }
        return false;
    }

    public boolean editFilename(String id, String newFilename) throws IOException {
        GridFSFile gridFsFile = gridFsTemplate.findOne(new org.springframework.data.mongodb.core.query.Query().addCriteria(
                org.springframework.data.mongodb.core.query.Criteria.where("_id").is(id)));

        if (gridFsFile != null) {
            GridFsResource resource = operations.getResource(gridFsFile);
            byte[] data = readAllBytes(resource.getInputStream());

            gridFsTemplate.delete(new org.springframework.data.mongodb.core.query.Query().addCriteria(
                    org.springframework.data.mongodb.core.query.Criteria.where("_id").is(id)));

            Map<String, Object> metadata = gridFsFile.getMetadata();
            ObjectId newId = gridFsTemplate.store(new ByteArrayInputStream(data), newFilename, resource.getContentType(), metadata);
            return newId != null;
        }
        return false;
    }

    public Optional<Image> getImageById(String id) throws IOException {
        GridFSFile gridFsFile = gridFsTemplate.findOne(new org.springframework.data.mongodb.core.query.Query().addCriteria(
                org.springframework.data.mongodb.core.query.Criteria.where("_id").is(id)));

        if (gridFsFile != null) {
            GridFsResource resource = operations.getResource(gridFsFile);
            byte[] data = readAllBytes(resource.getInputStream());

            Image image = new Image(gridFsFile.getFilename(), data, (String) gridFsFile.getMetadata().get("login"), (String) gridFsFile.getMetadata().get("category"));
            image.setId(id);

            return Optional.of(image);
        }

        return Optional.empty();
    }

    private Image convertToImage(GridFSFile file) {
        try {
            GridFsResource resource = operations.getResource(file);
            byte[] data = readAllBytes(resource.getInputStream());
            return new Image(file.getFilename(), data, (String) file.getMetadata().get("login"), (String) file.getMetadata().get("category"));
        } catch (IOException e) {
            LOGGER.severe("Error converting GridFSFile to Image: " + e.getMessage());
            return null;
        }
    }

    private byte[] readAllBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }
}
