package com.cognizant.orderservice.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(
        description="OrderDTO model information"
)
@Data
public class OrderDTO {
    @Schema(
            description="Order Id"
    )
    private Long id;

    @NotNull(message = "User_Id is required")
    @Positive(message = "User_Id must be a positive number")
    @Schema(
            description="User Id"
    )
    private Long userId;

    @Pattern(
            regexp = "CREATED|PAID|SHIPPED|CANCELLED",
            message = "Status must be one of: CREATED, PAID, SHIPPED, CANCELLED"
    )
    @Schema(
            description="Status"
    )
    private String status;

    @Schema(
            description="Order Created At"
    )
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime createdAt;

}
