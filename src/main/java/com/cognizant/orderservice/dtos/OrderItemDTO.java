package com.cognizant.orderservice.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(
        description="OrderItemDTO model information"
)
public class OrderItemDTO {
    @Schema(
            description="Order Item Id"
    )
    private Long id;

    @NotNull(message = "Product_Id is required")
    @Positive(message = "Product_Id must be a positive number")
    @Schema(
            description = "Product Id"
    )
    private Long productId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Schema(
            description = "Quantity"
    )
    private int quantity;

    @Positive(message="Price should be positive")
    @Schema(
            description = "Price"
    )
    private double price;

    @NotNull(message = "Order_Id is required")
    @Schema(
            description="Order Id"
    )
    private Long orderId;

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

    @Override
    public String toString() {
        return "OrderItemDTO{" +
                "id=" + id +
                ", productId=" + productId +
                ", quantity=" + quantity +
                ", price=" + price +
                ", orderId=" + orderId +
                '}';
    }
}
