package com.supercode.controller;

import com.supercode.entity.User;
import com.supercode.service.UserService;
import io.smallrye.jwt.build.Jwt;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Collections;
import java.util.Map;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthController {

    @Inject
    UserService userService;


    @POST
    @Path("/login")
    public Response login(Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        User user = userService.authenticate(username, password);
        if (user != null) {
            // Sign token using HMAC with the secret key
            long now = System.currentTimeMillis() / 1000; // detik
            long exp = now + (100 * 60); // 100 menit dari sekarang

            // Buat token dengan masa berlaku 100 menit
            String token = Jwt.issuer("supercode-auth")
                    .upn(user.getUser())
                    .groups(Collections.singleton(user.getRole()))
                    .claim("exp", exp) // set waktu kedaluwarsa
                    .sign();

            return Response.ok(Map.of(
                    "token", token,
                    "user", user.getUser(),
                    "status", 200,
                    "result", "Login successfully"
            )).build();

        } else {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("status", 500,
                            "result", "Login Failed"))
                    .build();
        }
    }
}
