package com.pinapp.cliente_microservice.dto;


import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para estadísticas KPI de clientes")
public class KpiClientesDTO {

    @Schema(description = "Promedio de edad de todos los clientes")
    private Double promedioEdad;

    @Schema(description = "Desviación estándar de las edades")
    private Double desviacionEstandar;

    public KpiClientesDTO() {}

    public KpiClientesDTO(Double promedioEdad, Double desviacionEstandar) {
        this.promedioEdad = promedioEdad;
        this.desviacionEstandar = desviacionEstandar;
    }

    public Double getPromedioEdad() {
        return promedioEdad;
    }

    public void setPromedioEdad(Double promedioEdad) {
        this.promedioEdad = promedioEdad;
    }

    public Double getDesviacionEstandar() {
        return desviacionEstandar;
    }

    public void setDesviacionEstandar(Double desviacionEstandar) {
        this.desviacionEstandar = desviacionEstandar;
    }
}