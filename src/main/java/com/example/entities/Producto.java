package com.example.entities;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
@Table(name = "productos")

public class Producto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private int id;

    @NotNull(message = "El nombre no puede ser nulo")
    @NotEmpty(message = "El nombre del producto no puede estar vacío")
    @Size(min = 4, max = 25 , message = "El nombre no puede tener menos de 4 caracteres ni más de 25")
    private String name;

    @NotBlank(message = "La descripción del producto es requerida")
    private String description;

    @Min(value = 0, message = "El stock no puede ser menos de cero")
    private int stock;

    @Min(value = 0, message = "El precio no puede ser negatovo")
    private double price;

    private String imagen;


    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    //@JsonIgnore
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    // https://stackoverflow.com/questions/67353793/what-does-jsonignorepropertieshibernatelazyinitializer-handler-do
    private Presentacion presentacion;


}
