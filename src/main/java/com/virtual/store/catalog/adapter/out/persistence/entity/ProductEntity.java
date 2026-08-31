package com.virtual.store.catalog.adapter.out.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "product_catalog")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "brand")
    private String brand;

    @Column(name = "active")
    private boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_category", nullable = false)
    private CategoryEntity categoryEntity;


    protected ProductEntity(){

    }

    public ProductEntity(String name, String description, String brand, boolean active, CategoryEntity categoryEntity){
        this.name = name;
        this.description = description;
        this.brand =brand;
        this.active = active;
        this.categoryEntity = categoryEntity;
    }

    //getter for read-only purpose
    public Long getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public String getDescription(){
        return description;
    }

    public String getBrand(){
        return brand;
    }

    public CategoryEntity getCategoryEntity(){
        return categoryEntity;
    }

    public boolean isActive(){
        return active;
    }

    //setters for admin update

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
