package com.pinapp.cliente_microservice.controller;

import com.pinapp.cliente_microservice.dto.ClienteRequestDTO;
import com.pinapp.cliente_microservice.dto.ClienteResponseDTO;
import com.pinapp.cliente_microservice.dto.KpiClientesDTO;
import com.pinapp.cliente_microservice.entity.Cliente;
import com.pinapp.cliente_microservice.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Cliente", description = "API para gestión de clientes")
@CrossOrigin(origins = "http://localhost:4200")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @PostMapping("/creacliente")
    @Operation(
            summary = "Crea nuevo cliente",
            description = "Crea un nuevo cliente con nombre, apellido, edad y fecha de nacimiento"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Cliente creado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Cliente.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Error en datos de entrada"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    public ResponseEntity<?> crearCliente(@Valid @RequestBody ClienteRequestDTO clienteRequest) {
        try {
            Cliente clienteCreado = clienteService.crearCliente(clienteRequest);
            return new ResponseEntity<>(clienteCreado, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al crear cliente: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/kpideclientes")
    @Operation(
            summary = "Obtiene KPIs de clientes",
            description = "Devuelve el promedio de edad y la desviación estándar de todas las edades de los clientes"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "KPIs obtenidos exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = KpiClientesDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    public ResponseEntity<?> obtenerKpiClientes() {
        try {
            KpiClientesDTO kpis = clienteService.obtenerKpiClientes();
            return new ResponseEntity<>(kpis, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener KPIs: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/listclientes")
    @Operation(
            summary = "Lista todos los clientes",
            description = "Devuelve una lista de todos los clientes con sus datos y fecha probable de muerte calculada"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de clientes obtenida exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ClienteResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    public ResponseEntity<?> listarClientes() {
        try {
            List<ClienteResponseDTO> clientes = clienteService.obtenerListaClientes();
            return new ResponseEntity<>(clientes, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener lista de clientes: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/cliente/{id}")
    @Operation(
            summary = "Devuelve cliente por ID",
            description = "Devuelve un cliente específico basado en su ID"
    )
    public ResponseEntity<?> obtenerClientePorId(@PathVariable Long id) {
        try {
            Cliente cliente = clienteService.obtenerClientePorId(id);
            return new ResponseEntity<>(cliente, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Cliente no encontrado", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener cliente: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}