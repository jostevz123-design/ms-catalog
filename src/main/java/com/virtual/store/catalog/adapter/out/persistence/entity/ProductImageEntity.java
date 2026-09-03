package com.virtual.store.catalog.adapter.out.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "product_images")
public class ProductImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "url_image")
    private String urlImage;

    @Column(name = "is_primary")
    private boolean isPrimary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_product", nullable = false)
    private ProductEntity productEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = true)
    private ProductVariantEntity productVariantEntity;

    protected ProductImageEntity(){

    }

    public ProductImageEntity(String urlImage, boolean isPrimary, ProductEntity productEntity, ProductVariantEntity productVariant){
        this.urlImage = urlImage;
        this.isPrimary = isPrimary;
        this.productEntity = productEntity;
        this.productVariantEntity = productVariant;
    }

    public Long getId() {
        return id;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }

    public ProductEntity getProductEntity() {
        return productEntity;
    }

    public ProductVariantEntity getProductVariantEntity() {
        return productVariantEntity;
    }

    public void setProductVariantEntity(ProductVariantEntity productVariantEntity) {
        this.productVariantEntity = productVariantEntity;
    }
}
