package com.virtual.store.catalog.adapter.out.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "product_category")
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    protected CategoryEntity(){

    }

    public CategoryEntity(String name){
        this.name = name;
    }

    public Long getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

}
