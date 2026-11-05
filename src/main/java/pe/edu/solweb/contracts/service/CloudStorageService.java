package pe.edu.solweb.contracts.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Primary
@Service
public class CloudStorageService extends StorageService {

    @Autowired
    private Cloudinary cloudinary;

    @Override
    public String storeFile(MultipartFile file, String prefix) {
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "public_id", prefix,
                    "resource_type", "raw"
            ));

            return uploadResult.get("secure_url").toString();

        } catch (IOException e) {
            throw new RuntimeException("Error al subir el archivo firmado a Cloudinary: " + e.getMessage());
        }
    }

    @Override
    public String getFileUrl(String prefix) {
        return cloudinary.url()
                .resourceType("raw")
                .secure(true)
                .generate(prefix);
    }
}