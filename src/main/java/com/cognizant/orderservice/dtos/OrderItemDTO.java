package com.cognizant.orderservice.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Schema(
        description="OrderItemDTO model information"
)
@Data
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

    @NotNull(message = "Order_Id is required")
    @Schema(
            description="Order Id"
    )
    private Long orderId;

}
