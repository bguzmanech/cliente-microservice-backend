package com.pinapp.cliente_microservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pinapp.cliente_microservice.dto.ClienteRequestDTO;
import com.pinapp.cliente_microservice.dto.KpiClientesDTO;
import com.pinapp.cliente_microservice.entity.Cliente;
import com.pinapp.cliente_microservice.service.ClienteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(ClienteController.class)
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClienteService clienteService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCrearClienteValido() throws Exception {
        ClienteRequestDTO clienteRequest = new ClienteRequestDTO();
        clienteRequest.setNombre("Lionel");
        clienteRequest.setApellido("Messi");
        clienteRequest.setEdad(38);
        clienteRequest.setFechaNacimiento(LocalDate.of(1987, 6, 24));

        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombre("Lionel");
        cliente.setApellido("Messi");
        cliente.setEdad(38);
        cliente.setFechaNacimiento(LocalDate.of(1987, 6, 24));

        when(clienteService.crearCliente(any(ClienteRequestDTO.class))).thenReturn(cliente);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/creacliente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteRequest)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nombre").value("Lionel"));
    }

    @Test
    void testObtenerKpiClientes() throws Exception {
        KpiClientesDTO kpi = new KpiClientesDTO(30.0, 5.5);
        when(clienteService.obtenerKpiClientes()).thenReturn(kpi);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/kpideclientes"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.promedioEdad").value(30.0));
    }

}