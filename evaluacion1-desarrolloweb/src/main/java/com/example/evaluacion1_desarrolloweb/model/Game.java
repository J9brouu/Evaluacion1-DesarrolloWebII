package com.example.evaluacion1_desarrolloweb.model;

public class Game {
    private Long id;
    private String titulo;
    private String genero;
    private String plataforma;
    private int stock;
    private Long precio; // en CLP
    private String imagen; // nombre de archivo en /assets/

    public Game() {}

    public Game(Long id, String titulo, String genero, String plataforma, int stock, Long precio, String imagen) {
        this.id = id;
        this.titulo = titulo;
        this.genero = genero;
        this.plataforma = plataforma;
        this.stock = stock;
        this.precio = precio;
        this.imagen = imagen;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }
    public String getPlataforma() { return plataforma; }
    public void setPlataforma(String plataforma) { this.plataforma = plataforma; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public Long getPrecio() { return precio; }
    public void setPrecio(Long precio) { this.precio = precio; }
    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }
}
