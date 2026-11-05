package pe.edu.solweb.contracts.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class StorageService {

    private final Path storageLocation = Paths.get("./external-storage-bucket");

    public String storeFile(MultipartFile file, String prefix) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("No se puede almacenar un archivo vacío.");
            }

            if (!Files.exists(storageLocation)) {
                Files.createDirectories(storageLocation);
            }

            String originalFileName = file.getOriginalFilename();
            String fileName = prefix + "_" + UUID.randomUUID().toString() + "_" + originalFileName;

            Path destinationFile = storageLocation.resolve(Paths.get(fileName))
                    .normalize().toAbsolutePath();

            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

            return destinationFile.toString();

        } catch (IOException e) {
            throw new RuntimeException("Fallo al almacenar el archivo en el servicio externo.", e);
        }
    }

    public String getFileUrl(String prefix) {
        return storageLocation.resolve(prefix).toAbsolutePath().toString();
    }
}