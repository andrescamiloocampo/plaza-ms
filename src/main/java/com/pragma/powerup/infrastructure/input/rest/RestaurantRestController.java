package com.pragma.powerup.infrastructure.input.rest;

import com.pragma.powerup.application.dto.request.RestaurantRequestDto;
import com.pragma.powerup.application.dto.response.RestaurantPartialResponseDto;
import com.pragma.powerup.application.dto.response.RestaurantResponseDto;
import com.pragma.powerup.application.handler.IRestaurantHandler;
import com.pragma.powerup.application.mapper.response.IRestaurantResponseMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/restaurants")
@AllArgsConstructor
public class RestaurantRestController {
    private final IRestaurantHandler restaurantHandler;
    private final IRestaurantResponseMapper restaurantResponseMapper;

    @Operation(summary = "Get restaurants")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Restaurants found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden authentication required", content = @Content),
            @ApiResponse(responseCode = "400", description = "Illegal request params provided", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<RestaurantPartialResponseDto>> getRestaurants(@RequestParam(name = "page") int page, @RequestParam(name = "size") int size) {
        return ResponseEntity.ok(restaurantHandler.getRestaurants(page, size));
    }

    @Operation(summary = "Create restaurant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Restaurant created", content = @Content),
            @ApiResponse(responseCode = "409", description = "Restaurant already exists", content = @Content)
    })
    @PreAuthorize("hasAuthority('ADMIN')")
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
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('OWNER')")
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponseDto> getRestaurant(@PathVariable int id) {
        return ResponseEntity.ok(restaurantHandler.getRestaurantById(id));
    }

    @Operation(summary = "Get ownership", description = "Returns true if the restaurant belongs to the current owner")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "True or false response",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Invalid role or credentials",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(mediaType = "application/json"))
    })
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('OWNER')")
    @GetMapping("/ownership")
    public ResponseEntity<Boolean> getOwnership(@RequestParam int id, @RequestParam int ownerId) {
        return ResponseEntity.ok(restaurantHandler.getOwnership(id, ownerId));
    }

    @Operation(summary = "Get restaurant by ownerId", description = "Returns a restaurant's information given its ownerId.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Restaurant found",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Restaurant not found",
                    content = @Content)
    })
    @PreAuthorize("hasAuthority('OWNER')")
    @GetMapping("/byOwner/{ownerId}")
    public ResponseEntity<List<RestaurantResponseDto>> getRestaurantsByOwnerId(@PathVariable int ownerId) {
        return ResponseEntity.ok(restaurantHandler.getRestaurantsByOwnerId(ownerId));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/admin_access")
    public String isOwner() {
        return "Yes indeed";
    }
}
