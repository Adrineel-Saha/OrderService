package com.cognizant.orderservice.controllers;

import com.cognizant.orderservice.dtos.OrderDTO;
import com.cognizant.orderservice.dtos.OrderItemDTO;
import com.cognizant.orderservice.dtos.OrderItemResponseDTO;
import com.cognizant.orderservice.dtos.OrderResponseDTO;
import com.cognizant.orderservice.services.OrderItemService;
import com.cognizant.orderservice.services.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/orders")
@Tag(
        name="CRUD REST APIs for Order Service",
        description="CRUD REST APIs - Create Order, Get Order Update Order, Delete Order"
)
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderItemService orderItemService;

    @PostMapping
    @Operation(
            summary="Create Order REST API",
            description="Used to save an order to the database"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description="Order Created"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description="Bad Request"
            )
    })
    public ResponseEntity<OrderResponseDTO> createOrder(@Valid @RequestBody OrderDTO orderDTO){
        OrderResponseDTO orderResponseDTO= orderService.createOrder(orderDTO);

        if(orderResponseDTO!=null){
            return new ResponseEntity<>(orderResponseDTO, HttpStatus.CREATED);
        }
        else{
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("{orderId}")
    @Operation(
            summary="Get Order By Id REST API",
            description="Used to get a single order from the database"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description="Order Found"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description="Order Not Found"
            )
    })
    public ResponseEntity<OrderResponseDTO> getOrder(@PathVariable("orderId") Long orderId){
        OrderResponseDTO orderResponseDTO= orderService.getOrder(orderId);

        if(orderResponseDTO!=null){
            return new ResponseEntity<>(orderResponseDTO, HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    @Operation(
            summary="Get All Orders REST API",
            description="Used to get all orders from the database"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description="Orders Found"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description="Bad Request"
            )
    })
    public ResponseEntity<List<OrderResponseDTO>> listOrders(){
        List<OrderResponseDTO> orderResponseDTOList= orderService.listOrders();

        if(orderResponseDTOList!=null && !orderResponseDTOList.isEmpty()){
            return new ResponseEntity<>(orderResponseDTOList, HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("user/{userId}")
    @Operation(
            summary="Get All Orders By User Id REST API",
            description="Used to get all orders by User Id from the database"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description="Orders Found"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description="Orders Not Found"
            )
    })
    public ResponseEntity<List<OrderResponseDTO>> listOrdersByUser(@PathVariable("userId") Long userId){
        List<OrderResponseDTO> orderResponseDTOList= orderService.listOrdersByUser(userId);

        if(orderResponseDTOList!=null && !orderResponseDTOList.isEmpty()){
            return new ResponseEntity<>(orderResponseDTOList, HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("{orderId}/status/{status}")
    @Operation(
            summary="Update Order REST API",
            description="Used to update an order in the database"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "202",
                    description="Order Updated"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description="Order Not Found"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description="Bad Request"
            )
    })
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(@PathVariable("orderId") Long orderId,@PathVariable("status") @Pattern(regexp = "CREATED|PAID|SHIPPED|CANCELLED", message = "Status must be one of: CREATED, PAID, SHIPPED, CANCELLED") String status){
        OrderResponseDTO orderResponseDTO= orderService.updateOrderStatus(orderId,status);

        if(orderResponseDTO!=null){
            return new ResponseEntity<>(orderResponseDTO, HttpStatus.ACCEPTED);
        }
        else{
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("{orderId}")
    @Operation(
            summary="Delete Order REST API",
            description="Used to delete an order from the database"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description="Order Deleted"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description="Order Not Found"
            ),
    })
    public ResponseEntity<String> deleteOrder(@PathVariable("orderId") Long orderId){
        String result= orderService.deleteOrder(orderId);

        if (result != null) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("items")
    @Operation(
            summary="Create Order Item REST API",
            description="Used to save an order item to the database"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description="Order Item Created"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description="Bad Request"
            )
    })
    public ResponseEntity<OrderItemResponseDTO> addItem(@Valid @RequestBody OrderItemDTO orderItemDTO){
        OrderItemResponseDTO orderItemResponseDTO= orderItemService.addItem(orderItemDTO);

        if(orderItemResponseDTO!=null){
            return new ResponseEntity<>(orderItemResponseDTO, HttpStatus.CREATED);
        }
        else{
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("items/{orderId}")
    @Operation(
            summary="Get All Orders Items By Order REST API",
            description="Used to get all order items by order from the database"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description="Order Items Found"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description="Order Items Not Found"
            )
    })
    public ResponseEntity<List<OrderItemResponseDTO>> listItemsByOrder(@PathVariable("orderId") Long orderId){
        List<OrderItemResponseDTO> orderItemResponseDTOlist= orderItemService.listItemsByOrder(orderId);

        if(orderItemResponseDTOlist!=null && !orderItemResponseDTOlist.isEmpty()){
            return new ResponseEntity<>(orderItemResponseDTOlist, HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("items/{itemId}")
    @Operation(
            summary="Update Order Item REST API",
            description="Used to update an order item in the database"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "202",
                    description="Order Item Updated"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description="Order Item Not Found"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description="Bad Request"
            )
    })
    public ResponseEntity<OrderItemResponseDTO> updateItem(@PathVariable("itemId") Long itemId,@Valid @RequestBody OrderItemDTO orderItemDTO){
        OrderItemResponseDTO orderItemResponseDTO= orderItemService.updateItem(itemId, orderItemDTO);

        if(orderItemResponseDTO!=null){
            return new ResponseEntity<>(orderItemResponseDTO, HttpStatus.ACCEPTED);
        }
        else{
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("items/{itemId}")
    @Operation(
            summary="Delete Order REST API",
            description="Used to delete an order item from the database"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description="Order Item Deleted"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description="Order Item Not Found"
            ),
    })
    public ResponseEntity<String> deleteItem(@PathVariable("itemId") Long itemId){
        String result= orderItemService.deleteItem(itemId);

        if (result != null) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
