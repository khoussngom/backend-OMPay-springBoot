package com.khouss.UsersMicroservice.annotations;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation meta pour standardiser la documentation des réponses 201 Created.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created - ressource créée avec succès"),
        @ApiResponse(responseCode = "400", description = "Bad Request - paramètres invalides"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error")
})
public @interface ApiResponseCreated {
}

