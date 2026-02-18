package com.cognizant.orderservice.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description="UserDTO model information"
)
public class UserDTO {
    @Schema(
            description="User Id"
    )
    private Long id;

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
