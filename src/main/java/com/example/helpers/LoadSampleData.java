package com.example.helpers;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.entities.Presentacion;
import com.example.entities.Producto;
import com.example.services.PresentacionService;
import com.example.services.ProductoService;

@Configuration // cuando spring levante el contexto, busca todas las clases con configuration y lo ejecutará en el momento adecuado
public class LoadSampleData {

   // Lo que es de tipo commandLineRunner se ejecuta después
   @Bean // cuando se cargue el contexto generará un bean con el código de este método para que lo inyecte cuando considere
   public CommandLineRunner saveSampleData(ProductoService productoService, PresentacionService presentacionService) {

    return datos -> { // esto es el cuerpo del metodo run de commandLineRunner

            presentacionService.save(Presentacion.builder()
            .name("unidad")
            .build());

            presentacionService.save(Presentacion.builder()
            .name("docena")
            .build());

            //---------------------------------------------//

            productoService.save(Producto.builder()
            .name("papel")
            .description("Papel reciclado")
            .stock(10)
            .price(3.75)
            .presentacion(presentacionService.findById(2))
            .build());


            productoService.save(Producto.builder()
            .name("Pelota baloncesto")
            .description("Pelotas de reglamento")
            .stock(12)
            .price(35.50)
            .presentacion(presentacionService.findById(1))
            .build());

            productoService.save(Producto.builder()
            .name("Bolígrafos")
            .description("Bolígrafos de colores fantasía")
            .stock(120)
            .price(2.25)
            .presentacion(presentacionService.findById(2))
            .build());

            productoService.save(Producto.builder()
            .name("Zapatillas danza moderna")
            .description("Zapatillas de planta partida")
            .stock(15)
            .price(175.25)
            .presentacion(presentacionService.findById(1))
            .build());

            productoService.save(Producto.builder()
            .name("Sudaderas")
            .description("Sudaderas de flores")
            .stock(5)
            .price(45.00)
            .presentacion(presentacionService.findById(1))
            .build());

            productoService.save(Producto.builder()
            .name("Refresco energético")
            .description("Reflesco en latas")
            .stock(14)
            .price(5.25)
            .presentacion(presentacionService.findById(2))
            .build());

            productoService.save(Producto.builder()
            .name("Gomas de borrar")
            .description("Gomas de borrar con formas de animales")
            .stock(6)
            .price(1.50)
            .presentacion(presentacionService.findById(2))
            .build());

            productoService.save(Producto.builder()
            .name("Bufandas")
            .description("Bufandas hechas a mano")
            .stock(7)
            .price(25.00)
            .presentacion(presentacionService.findById(1))
            .build());

            productoService.save(Producto.builder()
            .name("Ordenador")
            .description("Portátiles 14 pulgadas")
            .stock(3)
            .price(980.00)
            .presentacion(presentacionService.findById(1))
            .build());

            productoService.save(Producto.builder()
            .name("Ratón")
            .description("Ratón inalámbrico de colores")
            .stock(2)
            .price(20.00)
            .presentacion(presentacionService.findById(2))
            .build());

            productoService.save(Producto.builder()
            .name("Pecera")
            .description("Pecera de gran tamaño con purificador de agua")
            .stock(7)
            .price(120.75)
            .presentacion(presentacionService.findById(1))
            .build());
    };

   }

}
