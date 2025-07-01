package com.pinapp.cliente_microservice.service;

import com.pinapp.cliente_microservice.dto.ClienteRequestDTO;
import com.pinapp.cliente_microservice.dto.ClienteResponseDTO;
import com.pinapp.cliente_microservice.dto.KpiClientesDTO;
import com.pinapp.cliente_microservice.entity.Cliente;
import com.pinapp.cliente_microservice.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;


    public Cliente crearCliente(ClienteRequestDTO clienteRequest) {
        Cliente cliente = new Cliente();
        cliente.setNombre(clienteRequest.getNombre());
        cliente.setApellido(clienteRequest.getApellido());
        cliente.setEdad(clienteRequest.getEdad());
        cliente.setFechaNacimiento(clienteRequest.getFechaNacimiento());

        return clienteRepository.save(cliente);
    }


    public KpiClientesDTO obtenerKpiClientes() {
        List<Integer> edades = clienteRepository.findAllEdades();

        if (edades.isEmpty()) {
            return new KpiClientesDTO(0.0, 0.0);
        }

        Double promedio = edades.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);

        Double varianza = edades.stream()
                .mapToDouble(edad -> Math.pow(edad - promedio, 2))
                .average()
                .orElse(0.0);

        Double desviacionEstandar = Math.sqrt(varianza);

        return new KpiClientesDTO(
                Math.round(promedio * 100.0) / 100.0,
                Math.round(desviacionEstandar * 100.0) / 100.0
        );
    }


    public List<ClienteResponseDTO> obtenerListaClientes() {
        List<Cliente> clientes = clienteRepository.findAll();

        return clientes.stream()
                .map(this::convertirAClienteResponseDTO)
                .collect(Collectors.toList());
    }


    private ClienteResponseDTO convertirAClienteResponseDTO(Cliente cliente) {
        LocalDate fechaProbableMuerte = calcularFechaProbableMuerte(cliente.getEdad());

        return new ClienteResponseDTO(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getApellido(),
                cliente.getEdad(),
                cliente.getFechaNacimiento(),
                fechaProbableMuerte
        );
    }


    private LocalDate calcularFechaProbableMuerte(Integer edadActual) {
        final int EXPECTATIVA_VIDA_ARGENTINA = 76;

        int anosRestantes = Math.max(EXPECTATIVA_VIDA_ARGENTINA - edadActual, 5);

        int variacion = (int) (Math.random() * 11) - 5;
        anosRestantes += variacion;

        anosRestantes = Math.max(anosRestantes, 1);

        return LocalDate.now().plusYears(anosRestantes);
    }


    public List<Cliente> obtenerTodosLosClientes() {
        return clienteRepository.findAll();
    }


    public Cliente obtenerClientePorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + id));
    }
}