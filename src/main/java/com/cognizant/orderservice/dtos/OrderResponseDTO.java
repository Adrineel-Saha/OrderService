package com.cognizant.orderservice.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(
        description="OrderResponseDTO model information"
)
@Data
public class OrderResponseDTO {
    @Schema(
            description="Order Id"
    )
    private Long id;

    @Schema(
            description="User Id"
    )
    private Long userId;

    @Schema(
            description="Status"
    )
    private String status;

    @Schema(
            description="Order Created At"
    )
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(
            description="User Name"
    )
    private String userName;

    @Schema(
            description="User Email Address"
    )
    private String email;

}
