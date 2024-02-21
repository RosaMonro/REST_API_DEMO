package com.example.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.entities.Producto;
import com.example.helpers.FileDownloadUtil;
import com.example.helpers.FileUploadUtil;
import com.example.model.FileUploadResponse;
import com.example.services.ProductoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController // para aceptar y generar json
@RequestMapping("/productos") // a dnd va a responder (web.bind. annotation)
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;
    private final FileUploadUtil fileUploadUtil;
    private final FileDownloadUtil fileDownloadUtil;

// el método responde a una petición (request) del tipo http://localhost:8080/productos?page=0&size=3
// (le estoy pidiendo que me de 3 productos con size)
// si no se especifica pade y size, que devuelva los productos ordenados por el nombre, por ej:
    @GetMapping
    public ResponseEntity<List<Producto>> findAll(@RequestParam(name = "page", required = false) Integer page, 
                                                    @RequestParam(name = "size", required = false) Integer size) {
        // El ResponseEnitity es para que nos “devuelva” una confirmación del envío del json

        ResponseEntity<List<Producto>> responseEntity = null;
        Sort sortByName = Sort.by("name"); // lo sacamos fuera para poder usarlo en el else sin repetir código
        List<Producto> productos = new ArrayList<>(); // para poder usarlo en el else

        // comprobamos si han enviado page y size
        if (page !=null && size !=null) {
            // queremos devolver los datos paginados
            Pageable pageable = PageRequest.of(page, size, sortByName); // data.domain
            Page<Producto> pageProductos = productoService.findAll(pageable);
            productos = pageProductos.getContent();
            responseEntity = new ResponseEntity<List<Producto>>(productos, HttpStatus.OK);

        } else {
            // solo ordenamiento
            productos = productoService.findAll(sortByName);
            responseEntity = new ResponseEntity<List<Producto>>(productos, HttpStatus.OK);
        }

        return responseEntity;    

    }



 /**
     * Persiste un producto en la base de datos
     * @throws IOException
     * 
     * */
    // Guardar (Persistir), un producto, con su presentacion en la base de datos
    // Para probarlo con POSTMAN: Body -> form-data -> producto -> CONTENT TYPE ->
    // application/json
    // no se puede dejar el content type en Auto, porque de lo contrario asume
    // application/octet-stream
    // y genera una exception MediaTypeNotSupported

// Persisitir un producto (con validación)
    @PostMapping(consumes = "multipart/form-data") 
    @Transactional //de springframework
    public ResponseEntity<Map<String, Object>> saveProduct(
                                @Valid 
                                @RequestPart(name = "producto", required = true) Producto producto, 
                                BindingResult validationResults,
                                @RequestPart(name = "file", required = false) MultipartFile file) {

        Map<String, Object> responseAsMap = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity = null;

        // Comprobar si el porducto tiene errores
        if(validationResults.hasErrors()) {

            List<String> errores = new ArrayList<>();

            List<ObjectError> objectErrors = validationResults.getAllErrors();

            //recorremos objectError para sacar el manesaje de cada uno
            objectErrors.forEach(objectError -> {
                errores.add(objectError.getDefaultMessage());
            });

            responseAsMap.put("errores", errores);
            responseAsMap.put("Producto Mal Formado", producto);
            responseEntity = new ResponseEntity<Map<String,Object>>(responseAsMap, HttpStatus.BAD_REQUEST);

            return responseEntity;

        }

        // Comprobamos si nos hay imagen para el producto
        if (file != null) {

            try {
                String fileName = file.getOriginalFilename();
                String fileCode = fileUploadUtil.saveFile(fileName, file);
                producto.setImagen(fileCode + "-" + fileName);

                // Devolvemos la información relativa al archivo guardado
                // para lo cual, en una capa model, vamos a crear un record con la info del archivo q vamos a devolver
                 FileUploadResponse fileUploadResponse = FileUploadResponse
                       .builder()
                       .fileName(fileCode + "-" + fileName)
                       .downloadURI("/productos/downloadFile/" 
                                 + fileCode + "-" + fileName)
                       .size(file.getSize())
                       .build();
            
                responseAsMap.put("info de la imagen: ", fileUploadResponse);     

            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        // Este return se dará solo cuando no haya errores en el producto
        // por tanto persisitmos el producto
        try {

            Producto productoPersistido = productoService.save(producto);
            String succesMessage = "El producto se ha guardado correctamente";
            responseAsMap.put("Succes Message: ", succesMessage);
            responseAsMap.put("Producto guardado", productoPersistido);
            responseEntity = new ResponseEntity<Map<String,Object>>(responseAsMap, HttpStatus.CREATED);
            
        } catch (DataAccessException e) {
            // por ejemplo, el producto está bien formado, pero se cae el servidor

            String error = "Se ha producido un error al guardar y la causa más probable es: " + e.getMostSpecificCause();
            responseAsMap.put("Error: ", error);
            responseAsMap.put("Producto que se ha intentado guardar", producto);
            responseEntity = new ResponseEntity<Map<String,Object>>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);

        }

        return responseEntity;

    }




// Actualizar un producto (con validación)
    @PutMapping("/{id}") // le mando un endpoint con el id que se va a actualizar
    public ResponseEntity<Map<String, Object>> updateProduct(@Valid 
                                                    @RequestBody Producto producto, BindingResult validationResults, 
                                                    @PathVariable(name = "id", required = true) Integer idProducto) {

        Map<String, Object> responseAsMap = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity = null;

        // Comprobar si el porducto tiene errores
        if(validationResults.hasErrors()) {

            List<String> errores = new ArrayList<>();

            List<ObjectError> objectErrors = validationResults.getAllErrors();

            //recorremos objectError para sacar el manesaje de cada uno
            objectErrors.forEach(objectError -> {
                errores.add(objectError.getDefaultMessage());
            });

            responseAsMap.put("errores", errores);
            responseAsMap.put("Producto Mal Formado", producto);
            responseEntity = new ResponseEntity<Map<String,Object>>(responseAsMap, HttpStatus.BAD_REQUEST);

            return responseEntity;

        }

        // Este return se dará solo cuando no haya errores en el producto
        // por tanto actualizar el producto
        try {

            producto.setId(idProducto);
            Producto productoActualizado = productoService.save(producto);
            String succesMessage = "El producto se ha actualizado correctamente";
            responseAsMap.put("Succes Message: ", succesMessage);
            responseAsMap.put("Producto actualizado", productoActualizado);
            responseEntity = new ResponseEntity<Map<String,Object>>(responseAsMap, HttpStatus.OK);
            
        } catch (DataAccessException e) {
            // por ejemplo, el producto está bien formado, pero se cae el servidor

            String error = "Se ha producido un error al actualizar y la causa más probable es: " + e.getMostSpecificCause();
            responseAsMap.put("Error: ", error);
            responseAsMap.put("Producto que se ha intentado actualizar", producto);
            responseEntity = new ResponseEntity<Map<String,Object>>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);

        }

        return responseEntity;

    }




// Metodo que recupera un producto por el id
@GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> findProductoById(@PathVariable(name = "id", required = true) Integer idProducto) throws IOException {

        Map<String, Object> responseAsMap = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity = null;

        try {
            
            Producto producto = productoService.findById(idProducto);
            // Podríamos dejarlo aquí,al ser una api rest, 
            // ademá stenemos que devolver un mensaje sobre el estado.

            if(producto != null) {
                String successMessage = "Producto con id " + idProducto + " encontrado.";
                responseAsMap.put("successMessage", successMessage);
                responseAsMap.put("producto", producto);
                responseEntity = new ResponseEntity<Map<String,Object>>(responseAsMap, HttpStatus.OK);

            } else {
                String errorMessage = "Producto con id " + idProducto + " no encontrado.";
                responseAsMap.put("errorMessage", errorMessage);
                responseEntity = new ResponseEntity<Map<String,Object>>(responseAsMap, HttpStatus.NOT_FOUND);
            }

        } catch (DataAccessException e) {

            String errorGrave = "Se ha producido un error al buscar el producto con id " 
                            + idProducto + ", y la causa más problales es " + e.getMostSpecificCause();

            responseAsMap.put("errorGrave", errorGrave);
            responseEntity = new ResponseEntity<Map<String,Object>>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);

        }

        return responseEntity;
    }




// Eliminar un procducto por el ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteProductoById(@PathVariable(name = "id", required = true) Integer idProducto) {

        Map<String, Object> responseAsMap = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity = null;

        try {
            
            productoService.delete(productoService.findById(idProducto));
            String successMessage = "Porducto con id " + idProducto + " eliminado correctamente.";
            responseAsMap.put("successMessage", successMessage);
            responseEntity = new ResponseEntity<Map<String,Object>>(responseAsMap, HttpStatus.OK);

        } catch (DataAccessException e) {

            String errorGrave = "Se ha producido un error al eliminar el producto con id " 
                                + idProducto + ", y la causa más problales es " + e.getMostSpecificCause();
            responseAsMap.put("errorGrave", errorGrave);
            responseEntity = new ResponseEntity<Map<String,Object>>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);

        }

        return responseEntity;
    }




// (IMAGEN) Método que va a respnder a la URI
        /**
     *  Implementa filedownnload end point API 
     **/    

    @GetMapping("/downloadFile/{fileCode}")
    public ResponseEntity<?> downloadFile(@PathVariable(name = "fileCode") String fileCode) {

        Resource resource = null;

        try {
            resource = fileDownloadUtil.getFileAsResource(fileCode);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }

        if (resource == null) {
            return new ResponseEntity<>("File not found ", HttpStatus.NOT_FOUND);
        }

        String contentType = "application/octet-stream";
        String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";

        return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(contentType))
        .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
        .body(resource);

    }  



}
