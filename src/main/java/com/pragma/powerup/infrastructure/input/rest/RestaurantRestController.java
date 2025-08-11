package com.pragma.powerup.infrastructure.input.rest;

import com.pragma.powerup.application.dto.request.RestaurantRequestDto;
import com.pragma.powerup.application.dto.response.RestaurantResponseDto;
import com.pragma.powerup.application.handler.IRestaurantHandler;
import com.pragma.powerup.domain.model.RestaurantModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/restaurant")
@AllArgsConstructor
public class RestaurantRestController {
    private final IRestaurantHandler restaurantHandler;

    @Operation(summary = "Create restaurant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Restaurant created", content = @Content),
            @ApiResponse(responseCode = "409", description = "Restaurant already exists", content = @Content)
    })
    @PostMapping("/")
    public ResponseEntity<Void> saveRestaurant(@RequestBody RestaurantRequestDto restaurantRequestDto) {
        restaurantHandler.saveRestaurant(restaurantRequestDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "Get restaurant by ID", description = "Returns a restaurant's information given its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Restaurant found",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Restaurant not found",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponseDto> getRestaurant(@PathVariable int id) {
        return ResponseEntity.ok(restaurantHandler.getRestaurantById(id));
    }

    @GetMapping("/ownership")
    public ResponseEntity<Boolean> getOwnership(@RequestParam int id, @RequestParam int ownerId){
        return ResponseEntity.ok(restaurantHandler.getOwnership(id,ownerId));
    }

    @GetMapping("/admin_access")
    public String isOwner(){
        return "Yes indeed";
    }
}
