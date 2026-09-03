package com.virtual.store.catalog.adapter.out.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Entity
@Table(name = "product_variant")
public class ProductVariantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "sku")
    private String sku;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "attributes")
    private Map<String, String> attributes;

    @Column(name = "price")
    private Long price;

    @Column(name = "stock")
    private Long stock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    ProductEntity productEntity;

    protected ProductVariantEntity(){

    }

    public ProductVariantEntity(String sku, Map<String, String> attributes, Long price, Long stock, ProductEntity productEntity){
        this.sku = sku;
        this.attributes = attributes;
        this.price = price;
        this.stock = stock;
        this.productEntity = productEntity;
    }

    public Long getId(){
        return id;
    }

    public String getSku() {
        return sku;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Long getStock() {
        return stock;
    }

    public void setStock(Long stock) {
        this.stock = stock;
    }

    public ProductEntity getProductEntity() {
        return productEntity;
    }
}
