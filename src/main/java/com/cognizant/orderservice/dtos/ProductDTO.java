package com.cognizant.orderservice.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description="UserDTO model information"
)
public class ProductDTO {
    @Schema(
            description = "Product Id"
    )
    private Long id;

    @Schema(
            description = "Product Name"
    )
    private String name;

    @Schema(
            description = "Description"
    )
    private String description;

    @Schema(
            description = "Price"
    )
    private double price;

    @Schema(
            description = "Stock"
    )
    private int stock;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
