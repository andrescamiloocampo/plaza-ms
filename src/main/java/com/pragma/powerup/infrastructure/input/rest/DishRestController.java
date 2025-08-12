package com.pragma.powerup.infrastructure.input.rest;

import com.pragma.powerup.application.dto.request.DishPartialUpdateDTO;
import com.pragma.powerup.application.dto.request.DishRequestDto;
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

@RestController
@RequestMapping("/api/v1/dish")
@RequiredArgsConstructor
public class DishRestController {

    private final DishHandler dishHandler;

    @PostMapping
    @Operation(summary = "Add a new dish")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Dish created", content = @Content),
            @ApiResponse(responseCode = "409", description = "Dish already exists", content = @Content)
    })
    public ResponseEntity<Void> saveDish(@RequestBody DishRequestDto dishRequestDto, Authentication authentication){
        int userId = Integer.parseInt((String) authentication.getPrincipal());
        dishHandler.saveDish(dishRequestDto,userId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update dish")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Dish updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid price or bad request data"),
            @ApiResponse(responseCode = "403", description = "Forbidden - user is not the owner of the restaurant"),
            @ApiResponse(responseCode = "404", description = "Dish not found")
    })
    public ResponseEntity<Void> updateDish(@PathVariable int id,@RequestBody DishPartialUpdateDTO dishPartialUpdateDTO, Authentication authentication){
        int userId = Integer.parseInt((String) authentication.getPrincipal());
        dishHandler.updateDish(id,userId,dishPartialUpdateDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/owner_access")
    public ResponseEntity<String> getOwnerAccess(Authentication authentication){
        String userId = (String) authentication.getPrincipal();
        return ResponseEntity.ok(String.format("Hello owner %s",userId));
    }
}
