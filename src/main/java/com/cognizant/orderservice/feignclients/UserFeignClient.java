package com.cognizant.orderservice.feignclients;

import com.cognizant.orderservice.dtos.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="UserService")
public interface UserFeignClient {
    @GetMapping("api/users/{id}")
    UserDTO getUser(@PathVariable("id") Long id);
}
