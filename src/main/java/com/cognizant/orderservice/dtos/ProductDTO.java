package com.cognizant.orderservice.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(
        description="UserDTO model information"
)
@Data
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

}
