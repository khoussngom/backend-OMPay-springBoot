package com.khouss.UsersMicroservice.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

@Service
public class CloudinaryService {

    private static final Logger log = LoggerFactory.getLogger(CloudinaryService.class);
    private final Cloudinary cloudinary;

    public CloudinaryService(
            @Value("${CLOUDINARY_CLOUD_NAME:}") String cloudName,
            @Value("${CLOUDINARY_API_KEY:}") String apiKey,
            @Value("${CLOUDINARY_API_SECRET:}") String apiSecret
    ) {
        log.info("🔵 CloudinaryService initializing with:");
        log.info("  - cloud_name={}", cloudName == null || cloudName.isEmpty() ? "EMPTY" : "***");
        log.info("  - api_key={}", apiKey == null || apiKey.isEmpty() ? "EMPTY" : "***");
        log.info("  - api_secret={}", apiSecret == null || apiSecret.isEmpty() ? "EMPTY" : "***");

        if (cloudName == null || cloudName.isEmpty() ||
            apiKey == null || apiKey.isEmpty() ||
            apiSecret == null || apiSecret.isEmpty()) {
            throw new IllegalArgumentException("Cloudinary credentials are not configured. Please set CLOUDINARY_CLOUD_NAME, CLOUDINARY_API_KEY, and CLOUDINARY_API_SECRET.");
        }

        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
        log.info("✅ CloudinaryService initialized");
    }

    public String uploadImage(byte[] data, String publicId) throws Exception {
        if (data == null || data.length == 0) {
            log.error("❌ Cannot upload: data is null or empty");
            throw new Exception("Image data is null or empty");
        }

        try (InputStream is = new ByteArrayInputStream(data)) {
            log.info("🔵 Uploading image: {} bytes with publicId={}", data.length, publicId);
            Map uploadResult = cloudinary.uploader().upload(is, ObjectUtils.asMap(
                    "public_id", publicId,
                    "overwrite", true,
                    "resource_type", "image"
            ));
            String url = (String) uploadResult.get("secure_url");
            log.info("✅ Upload successful: {}", url);
            return url;
        } catch (Exception e) {
            log.error("❌ Cloudinary upload failed: {}", e.getMessage(), e);
            throw new Exception("Cloudinary upload failed", e);
        }
    }
}
