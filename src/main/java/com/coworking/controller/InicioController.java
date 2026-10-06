package com.coworking.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InicioController {

    @GetMapping("/")
    public String inicio() {
        return "API de reservas de coworking activa. Rutas: /api/clientes, /api/recursos y /api/reservas";
    }
}
