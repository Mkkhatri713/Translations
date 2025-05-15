package com.service.translations.controller;


import com.service.translations.dto.AuthRequestDTO;
import com.service.translations.dto.AuthResponseDTO;
import com.service.translations.serviceImplementation.UserDetailsServiceImpl;
import com.service.translations.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoints for user login and JWT token generation")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Operation(
            summary = "Authenticate user and return JWT token",
            description = "Provide valid username and password to get JWT token for authenticated access",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = AuthRequestDTO.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully authenticated",
                            content = @Content(schema = @Schema(implementation = AuthResponseDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request. Missing or invalid parameters"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Authentication failed (invalid credentials)"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Access forbidden – user is authenticated but not authorized",
                            content = @Content(schema = @Schema(example = "{\"error\": \"Forbidden\"}"))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found"
                    )
            }
    )
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequestDTO request) {
        // Validate request (e.g., check for missing fields)
        if (request.getUsername() == null || request.getPassword() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request. Missing username or password.");
        }

        try {
            // Authenticate user
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (BadCredentialsException ex) {
            // Return Unauthorized error if credentials are incorrect
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }

        // Load user details from the database
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());

        // If user does not exist, return 404 Not Found
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(userDetails.getUsername());

        // Return successful response with JWT token
        return ResponseEntity.ok(new AuthResponseDTO(token));
    }
}
