package com.feniksovich.bankcards.controller;

import com.feniksovich.bankcards.dto.user.UserData;
import com.feniksovich.bankcards.dto.user.UserUpdateRequest;
import com.feniksovich.bankcards.service.user.UserService;
import com.feniksovich.bankcards.util.RegexPatterns;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Validated
@RestController
@RequestMapping("/users")
@Tag(name = "User Management", description = "API for managing users (admin only)")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all users", description = "Returns paginated list of all users in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users list successfully retrieved"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public Page<UserData> getAllUsers(@PageableDefault Pageable pageable) {
        return userService.getAll(pageable);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Search user by phone number", description = "Returns user data by phone number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User data successfully retrieved"),
            @ApiResponse(responseCode = "400", description = "Invalid phone number format"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public UserData searchUserByPhoneNumber(
            @RequestParam @Pattern(regexp = RegexPatterns.PHONE_NUMBER) String phoneNumber
    ) {
        return userService.getByPhoneNumber(phoneNumber);
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get user by ID", description = "Returns data of a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User data successfully retrieved"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public UserData getUser(@PathVariable UUID userId) {
        return userService.getById(userId);
    }

    @PutMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Update user", description = "Updates data of a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User data successfully updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public void updateUser(@PathVariable UUID userId, @RequestBody @Valid UserUpdateRequest request) {
        userService.updateById(userId, request);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete user", description = "Deletes a user from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User successfully deleted"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public void deleteUser(@PathVariable UUID userId) {
        userService.deleteById(userId);
    }

}
