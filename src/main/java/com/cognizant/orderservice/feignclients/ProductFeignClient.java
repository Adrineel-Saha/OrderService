package com.cognizant.orderservice.feignclients;

import com.cognizant.orderservice.dtos.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name="ProductService")
public interface ProductFeignClient {
    @GetMapping("api/products/id/{id}")
    ProductDTO getProduct(@PathVariable("id") Long id);

    @PutMapping("api/products/{id}")
    ProductDTO updateProduct(@PathVariable("id") Long id,ProductDTO productDTO);
}
