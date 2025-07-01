package com.pinapp.cliente_microservice.repository;

import com.pinapp.cliente_microservice.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    @Query("SELECT c.edad FROM Cliente c")
    List<Integer> findAllEdades();

    @Query("SELECT AVG(c.edad) FROM Cliente c")
    Double findPromedioEdad();

    List<Cliente> findByNombreContainingIgnoreCase(String nombre);

    List<Cliente> findByApellidoContainingIgnoreCase(String apellido);
}