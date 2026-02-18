package com.cognizant.orderservice.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

@Schema(
        description="OrderDTO model information"
)
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
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
