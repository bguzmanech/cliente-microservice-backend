package com.pinapp.cliente_microservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pinapp.cliente_microservice.dto.ClienteRequestDTO;
import com.pinapp.cliente_microservice.entity.Cliente;
import com.pinapp.cliente_microservice.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureWebMvc
class ClienteIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        clienteRepository.deleteAll();
    }

    @Test
    void testCrearYListarClientes() throws Exception {
        ClienteRequestDTO clienteRequest = new ClienteRequestDTO();
        clienteRequest.setNombre("María");
        clienteRequest.setApellido("García");
        clienteRequest.setEdad(30);
        clienteRequest.setFechaNacimiento(LocalDate.of(1994, 5, 20));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/creacliente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteRequest)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.nombre").value("María"));

        assertEquals(1, clienteRepository.count());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/listclientes"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].nombre").value("María"));
    }

    @Test
    void testKpisConVariosClientes() throws Exception {
        Cliente cliente1 = new Cliente("Ana", "López", 25, LocalDate.of(1999, 1, 1));
        Cliente cliente2 = new Cliente("Carlos", "Ruiz", 35, LocalDate.of(1989, 1, 1));

        clienteRepository.save(cliente1);
        clienteRepository.save(cliente2);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/kpideclientes"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.promedioEdad").value(30.0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.desviacionEstandar").exists());
    }

    @Test
    void testValidacionDatos() throws Exception {
        ClienteRequestDTO clienteInvalido = new ClienteRequestDTO();
        clienteInvalido.setNombre(""); // Nombre vacío
        clienteInvalido.setApellido("Test");
        clienteInvalido.setEdad(25);
        clienteInvalido.setFechaNacimiento(LocalDate.of(1990, 1, 1));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/creacliente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteInvalido)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        assertEquals(0, clienteRepository.count());
    }

    @Test
    void testFlujCompletoConMultiplesClientes() throws Exception {
        ClienteRequestDTO cliente1 = new ClienteRequestDTO();
        cliente1.setNombre("Lionel");
        cliente1.setApellido("Messi");
        cliente1.setEdad(38);
        cliente1.setFechaNacimiento(LocalDate.of(1987, 6, 24));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/creacliente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cliente1)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        ClienteRequestDTO cliente2 = new ClienteRequestDTO();
        cliente2.setNombre("Angel");
        cliente2.setApellido("Di Maria");
        cliente2.setEdad(37);
        cliente2.setFechaNacimiento(LocalDate.of(1988, 2, 14));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/creacliente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cliente2)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        assertEquals(2, clienteRepository.count());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/kpideclientes"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.promedioEdad").value(37.5));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/listclientes"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2));
    }
}