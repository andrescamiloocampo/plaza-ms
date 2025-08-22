package com.pragma.powerup.infrastructure.input.rest;

import com.pragma.powerup.application.dto.request.DishPartialUpdateDto;
import com.pragma.powerup.application.dto.request.DishRequestDto;
import com.pragma.powerup.application.dto.response.DishResponseDto;
import com.pragma.powerup.application.handler.impl.DishHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dishes")
@RequiredArgsConstructor
public class DishRestController {

    private final DishHandler dishHandler;

    @PostMapping
    @Operation(summary = "Add a new dish")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Dish created", content = @Content),
            @ApiResponse(responseCode = "409", description = "Dish already exists", content = @Content)
    })
    public ResponseEntity<Void> saveDish(@RequestBody DishRequestDto dishRequestDto, Authentication authentication) {
        int userId = Integer.parseInt((String) authentication.getPrincipal());
        dishHandler.saveDish(dishRequestDto, userId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Find dishes by restaurant and category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful petition", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad request, illegal arguments provided", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden authentication required", content = @Content)
    })
    public ResponseEntity<List<DishResponseDto>> getDishes(@RequestParam(name = "rid") int restaurantId,
                                                           @RequestParam(name = "page", defaultValue = "0") int page,
                                                           @RequestParam(name = "size", defaultValue = "10") int size,
                                                           @RequestParam(name = "category", defaultValue = "", required = false) String category
    ) {
        return ResponseEntity.ok(dishHandler.getDishes(restaurantId, page, size, category));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update dish")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Dish updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid price or bad request data"),
            @ApiResponse(responseCode = "403", description = "Forbidden - user is not the owner of the restaurant"),
            @ApiResponse(responseCode = "404", description = "Dish not found")
    })
    public ResponseEntity<Void> updateDish(@PathVariable int id, @RequestBody DishPartialUpdateDto dishPartialUpdateDTO, Authentication authentication) {
        int userId = Integer.parseInt((String) authentication.getPrincipal());
        dishHandler.updateDish(id, userId, dishPartialUpdateDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/{state}")
    @Operation(summary = "Update dish state")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Dish state updated successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - invalid ownership or invalid role"),
            @ApiResponse(responseCode = "404", description = "Dish not found")
    })
    public ResponseEntity<Void> updateDishState(@PathVariable int id, @PathVariable boolean state, Authentication authentication) {
        int userId = Integer.parseInt((String) authentication.getPrincipal());
        dishHandler.updateDishState(id, userId, state);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/owner_access")
    public ResponseEntity<String> getOwnerAccess(Authentication authentication) {
        String userId = (String) authentication.getPrincipal();
        return ResponseEntity.ok(String.format("Hello owner %s", userId));
    }
}
