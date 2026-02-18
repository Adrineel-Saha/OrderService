package com.cognizant.orderservice.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(
        description="OrderResponseDTO model information"
)
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
    private LocalDateTime createdAt;

    @Schema(
            description="User Name"
    )
    private String userName;

    @Schema(
            description="User Email Address"
    )
    private String email;

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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
