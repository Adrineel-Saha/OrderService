package com.cognizant.orderservice.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(
        description="UserDTO model information"
)
@Data
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

}
