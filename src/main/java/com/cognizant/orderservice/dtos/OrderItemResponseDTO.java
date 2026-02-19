package com.cognizant.orderservice.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description="OrderItemResponseDTO model information"
)
public class OrderItemResponseDTO {
    @Schema(
            description="Order Item Id"
    )
    private Long id;

    @Schema(
            description = "Product Id"
    )
    private Long productId;

    @Schema(
            description = "Quantity"
    )
    private int quantity;

    @Schema(
            description = "Price"
    )
    private double price;

    @Schema(
            description="Order Id"
    )
    private Long orderId;

    @Schema(
            description = "Product Name"
    )
    private String name;

    @Schema(
            description = "Description"
    )
    private String description;

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

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
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

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    @Override
    public String toString() {
        return "OrderItemResponseDTO{" +
                "id=" + id +
                ", productId=" + productId +
                ", quantity=" + quantity +
                ", price=" + price +
                ", orderId=" + orderId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", stock=" + stock +
                '}';
    }
}
