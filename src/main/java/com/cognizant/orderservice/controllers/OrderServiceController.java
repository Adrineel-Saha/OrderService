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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class OrderServiceController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderItemService orderItemService;

    private static final Logger log = LoggerFactory.getLogger(OrderServiceController.class);

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
        log.info("Adding new Order: " + orderDTO);
        OrderResponseDTO orderResponseDTO= orderService.createOrder(orderDTO);
        log.info("Created Order: " + orderResponseDTO);

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
        log.info("Getting Order with Id: " +orderId);
        OrderResponseDTO orderResponseDTO= orderService.getOrder(orderId);
        log.info("Found Order: " + orderResponseDTO);

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
        log.info("Getting All Orders");
        List<OrderResponseDTO> orderResponseDTOList= orderService.listOrders();
        log.info("Orders list: " + orderResponseDTOList);

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
        log.info("Getting All Products By User");
        List<OrderResponseDTO> orderResponseDTOList= orderService.listOrdersByUser(userId);
        log.info("Orders list: " + orderResponseDTOList);

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
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(@PathVariable("orderId") Long orderId,@Pattern(regexp = "CREATED|PAID|SHIPPED|CANCELLED") @PathVariable("status") String status){
        log.info("Updating Order with Id: " + orderId + " and status: " + status);
        OrderResponseDTO orderResponseDTO= orderService.updateOrderStatus(orderId,status);
        log.info("Updated Order: " + orderResponseDTO);

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
        log.info("Deleting Order with Id: " + orderId);
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
            summary="Create Item REST API",
            description="Used to save an item to the database"
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
        log.info("Adding new Item: " + orderItemDTO);
        OrderItemResponseDTO orderItemResponseDTO= orderItemService.addItem(orderItemDTO);
        log.info("Created Item: " + orderItemResponseDTO);

        if(orderItemResponseDTO!=null){
            return new ResponseEntity<>(orderItemResponseDTO, HttpStatus.CREATED);
        }
        else{
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("items/{itemId}")
    @Operation(
            summary="Get Item By Id REST API",
            description="Used to get a single item from the database"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description="Item Found"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description="Item Not Found"
            )
    })
    public ResponseEntity<OrderItemResponseDTO> getItem(@PathVariable("itemId") Long itemId){
        log.info("Getting Item with Id: " +itemId);
        OrderItemResponseDTO orderItemResponseDTO= orderItemService.getItem(itemId);
        log.info("Found Item: " + orderItemResponseDTO);

        if(orderItemResponseDTO!=null){
            return new ResponseEntity<>(orderItemResponseDTO, HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("items")
    @Operation(
            summary="Get All Items REST API",
            description="Used to get all items from the database"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description="Items Found"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description="Bad Request"
            )
    })
    public ResponseEntity<List<OrderItemResponseDTO>> listItems(){
        log.info("Getting All Items");
        List<OrderItemResponseDTO> orderItemResponseDTOList= orderItemService.listItems();
        log.info("Items list: " + orderItemResponseDTOList);

        if(orderItemResponseDTOList!=null && !orderItemResponseDTOList.isEmpty()){
            return new ResponseEntity<>(orderItemResponseDTOList, HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("items/product/{productId}")
    @Operation(
            summary="Get All Items By Product REST API",
            description="Used to get all items by Product from the database"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description="Items Found"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description="Items Not Found"
            )
    })
    public ResponseEntity<List<OrderItemResponseDTO>> listItemsByProduct(@PathVariable("productId") Long productId){
        log.info("Getting All Items By Product");
        List<OrderItemResponseDTO> orderItemResponseDTOlist= orderItemService.listItemsByProduct(productId);
        log.info("Items list: " + orderItemResponseDTOlist);

        if(orderItemResponseDTOlist!=null && !orderItemResponseDTOlist.isEmpty()){
            return new ResponseEntity<>(orderItemResponseDTOlist, HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("items/order/{orderId}")
    @Operation(
            summary="Get All Items By Order REST API",
            description="Used to get all items by order from the database"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description="Items Found"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description="Items Not Found"
            )
    })
    public ResponseEntity<List<OrderItemResponseDTO>> listItemsByOrder(@PathVariable("orderId") Long orderId){
        log.info("Getting All Items By Order");
        List<OrderItemResponseDTO> orderItemResponseDTOlist= orderItemService.listItemsByOrder(orderId);
        log.info("Items list: " + orderItemResponseDTOlist);

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
            description="Used to update an item in the database"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "202",
                    description="Item Updated"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description="Item Not Found"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description="Bad Request"
            )
    })
    public ResponseEntity<OrderItemResponseDTO> updateItem(@PathVariable("itemId") Long itemId,@Valid @RequestBody OrderItemDTO orderItemDTO){
        log.info("Updating Item with Id: " + itemId + " and details: " + orderItemDTO);
        OrderItemResponseDTO orderItemResponseDTO= orderItemService.updateItem(itemId, orderItemDTO);
        log.info("Updated Order: " + orderItemResponseDTO);

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
            description="Used to delete an item from the database"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description="Item Deleted"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description="Item Not Found"
            ),
    })
    public ResponseEntity<String> deleteItem(@PathVariable("itemId") Long itemId){
        log.info("Deleting Item with Id: " + itemId);
        String result= orderItemService.deleteItem(itemId);

        if (result != null) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
