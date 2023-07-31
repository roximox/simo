package com.example.slinkerappeasy.s_security.controller;


import com.example.slinkerappeasy.s_security.entity.JwtRequest;
import com.example.slinkerappeasy.s_security.entity.JwtResponse;
import com.example.slinkerappeasy.s_security.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping
public class JwtController {

    @Autowired
    private JwtService jwtService;

    @PostMapping("/authenticate")
    public JwtResponse createJwtToken(@RequestBody JwtRequest jwtRequest) throws Exception {
        return jwtService.createJwtToken(jwtRequest);
    }
}
