package com.pinapp.cliente_microservice.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

@Schema(description = "DTO de respuesta para cliente con fecha probable de muerte")
public class ClienteResponseDTO {

    @Schema(description = "ID único del cliente")
    private Long id;

    @Schema(description = "Nombre del cliente")
    private String nombre;

    @Schema(description = "Apellido del cliente")
    private String apellido;

    @Schema(description = "Edad del cliente")
    private Integer edad;

    @Schema(description = "Fecha de nacimiento")
    private LocalDate fechaNacimiento;

    @Schema(description = "Fecha probable de muerte calculada")
    private LocalDate fechaProbableMuerte;

    public ClienteResponseDTO() {}

    public ClienteResponseDTO(Long id, String nombre, String apellido, Integer edad,
                              LocalDate fechaNacimiento, LocalDate fechaProbableMuerte) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.edad = edad;
        this.fechaNacimiento = fechaNacimiento;
        this.fechaProbableMuerte = fechaProbableMuerte;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public Integer getEdad() { return edad; }
    public void setEdad(Integer edad) { this.edad = edad; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public LocalDate getFechaProbableMuerte() { return fechaProbableMuerte; }
    public void setFechaProbableMuerte(LocalDate fechaProbableMuerte) { this.fechaProbableMuerte = fechaProbableMuerte; }
}

