package com.ventaslibros.application.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class LibroRequest {
    @NotBlank @Size(max = 20)
    private String isbn;
    @NotBlank @Size(max = 200)
    private String titulo;
    @NotBlank @Size(max = 150)
    private String autor;
    @NotBlank @Size(max = 150)
    private String editorial;
    @NotNull
    private Long categoriaId;
    @NotNull @DecimalMin(value = "0.01")
    private BigDecimal precioUnitario;
    @NotNull @Min(0)
    private Integer stock;
    @Size(max = 500)
    private String urlImagen;
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }
    public String getEditorial() { return editorial; }
    public void setEditorial(String editorial) { this.editorial = editorial; }
    public Long getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public String getUrlImagen() { return urlImagen; }
    public void setUrlImagen(String urlImagen) { this.urlImagen = urlImagen; }
}
