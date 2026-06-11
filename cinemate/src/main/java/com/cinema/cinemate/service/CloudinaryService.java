package com.cinema.cinemate.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryService {

    private final Cloudinary cloudinary;

    /**
     * Upload a file to Cloudinary and return its secure URL.
     */
    public String uploadFile(MultipartFile file) throws IOException {
        try {
            // Generate a unique filename using UUID to prevent naming collisions
            String publicId = UUID.randomUUID().toString();
            
            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "public_id", publicId,
                    "folder", "cinemate/avatars"
            ));
            
            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            log.error("Error uploading file to Cloudinary: ", e);
            throw e;
        }
    }
}
