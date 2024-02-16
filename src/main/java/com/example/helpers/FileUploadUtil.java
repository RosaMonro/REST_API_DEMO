package com.example.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component // pedimos que cuando levante el contexto cree un bean de esta clase
public class FileUploadUtil {

    // Método que guarda el archovo que recibe
    public String saveFile(String fileName, MultipartFile multipartFile) // lo que recibe el método
            throws IOException { // Todos los métodos de nio general errores de excepcion IO
        Path uploadPath = Paths.get("Files-Upload"); // lo va a tomar como ruta relativa

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            // si no existe, le pido que la cree la carpeta. 
        }

        String fileCode = RandomStringUtils.randomAlphanumeric(8); // ¿?

        // Try-with-resources
        // Los recursos que se pueden manejar son los que implementan la interfaz closeable
        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(fileCode + "-" + fileName); 
            // para concatenar el código de 8 carac aleatorios - pepito.jpeg
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING); 
            // como son aleatorios, así nos aseguramos que nohaya 2 iguales
        } catch (IOException ioe) {
            throw new IOException("Could not save file: " + fileName, ioe);
        }

        return fileCode;
    }

}
