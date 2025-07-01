package com.pinapp.cliente_microservice.service;

import com.pinapp.cliente_microservice.dto.ClienteRequestDTO;
import com.pinapp.cliente_microservice.dto.KpiClientesDTO;
import com.pinapp.cliente_microservice.entity.Cliente;
import com.pinapp.cliente_microservice.repository.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class ClienteServiceTest {

    @Autowired
    private ClienteService clienteService;

    @MockBean
    private ClienteRepository clienteRepository;

    private ClienteRequestDTO clienteRequestDTO;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        clienteRequestDTO = new ClienteRequestDTO();
        clienteRequestDTO.setNombre("Lionel");
        clienteRequestDTO.setApellido("Messi");
        clienteRequestDTO.setEdad(38);
        clienteRequestDTO.setFechaNacimiento(LocalDate.of(1987, 6, 24));

        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombre("Lionel");
        cliente.setApellido("Messi");
        cliente.setEdad(38);
        cliente.setFechaNacimiento(LocalDate.of(1987, 6, 24));
    }

    @Test
    void testCrearCliente() {
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        Cliente clienteCreado = clienteService.crearCliente(clienteRequestDTO);

        assertNotNull(clienteCreado);
        assertEquals("Lionel", clienteCreado.getNombre());
        assertEquals("Messi", clienteCreado.getApellido());
        assertEquals(38, clienteCreado.getEdad());

        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    void testObtenerKpiClientes() {
        List<Integer> edades = Arrays.asList(38,37);
        when(clienteRepository.findAllEdades()).thenReturn(edades);

        KpiClientesDTO kpi = clienteService.obtenerKpiClientes();

        assertNotNull(kpi);
        assertEquals(37.5, kpi.getPromedioEdad());
        assertTrue(kpi.getDesviacionEstandar() > 0);

        verify(clienteRepository, times(1)).findAllEdades();
    }

    @Test
    void testObtenerKpiClientesSinDatos() {
        when(clienteRepository.findAllEdades()).thenReturn(Arrays.asList());

        KpiClientesDTO kpi = clienteService.obtenerKpiClientes();

        assertNotNull(kpi);
        assertEquals(0.0, kpi.getPromedioEdad());
        assertEquals(0.0, kpi.getDesviacionEstandar());
    }

    @Test
    void testObtenerClientePorIdExiste() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        Cliente clienteEncontrado = clienteService.obtenerClientePorId(1L);

        assertNotNull(clienteEncontrado);
        assertEquals(1L, clienteEncontrado.getId());
        assertEquals("Lionel", clienteEncontrado.getNombre());
    }

    @Test
    void testObtenerClientePorIdNoExiste() {
        when(clienteRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            clienteService.obtenerClientePorId(999L);
        });

        assertTrue(exception.getMessage().contains("Cliente no encontrado"));
    }

    @Test
    void testObtenerListaClientes() {
        List<Cliente> clientes = Arrays.asList(cliente);
        when(clienteRepository.findAll()).thenReturn(clientes);

        var resultado = clienteService.obtenerListaClientes();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());

        var clienteResponse = resultado.get(0);
        assertEquals(1L, clienteResponse.getId());
        assertEquals("Lionel", clienteResponse.getNombre());
        assertEquals("Messi", clienteResponse.getApellido());
        assertEquals(38, clienteResponse.getEdad());
        assertNotNull(clienteResponse.getFechaProbableMuerte());
        assertTrue(clienteResponse.getFechaProbableMuerte().isAfter(LocalDate.now()));
    }

    @Test
    void testObtenerTodosLosClientes() {
        List<Cliente> clientes = Arrays.asList(cliente);
        when(clienteRepository.findAll()).thenReturn(clientes);

        List<Cliente> resultado = clienteService.obtenerTodosLosClientes();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(cliente, resultado.get(0));

        verify(clienteRepository, times(1)).findAll();
    }
}