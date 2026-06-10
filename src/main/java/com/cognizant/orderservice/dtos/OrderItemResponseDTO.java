package com.cognizant.orderservice.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(
        description="OrderItemResponseDTO model information"
)
@Data
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

}
