👥 Cliente Microservice - Backend
📋 Descripción
API REST desarrollada con Spring Boot que gestiona información de clientes, proporcionando endpoints para crear clientes, obtener estadísticas demográficas y listar clientes con predicciones actuariales.
🛠️ Tecnologías

Java 21
Spring Boot 3.0+
Spring Data JPA
H2 Database (en memoria)
Jakarta EE (validaciones)
OpenAPI 3.0 (Springdoc)
Maven
JUnit 5 & Mockito

📁 Estructura del Proyecto
```
src/
├── main/
│   ├── java/com/pinapp/cliente_microservice/
│   │   ├── controller/
│   │   │   └── ClienteController.java       # Controlador REST con endpoints
│   │   ├── service/
│   │   │   └── ClienteService.java         # Lógica de negocio
│   │   ├── repository/
│   │   │   └── ClienteRepository.java      # Queries personalizadas JPA
│   │   ├── entity/
│   │   │   └── Cliente.java                # Entidad JPA con validaciones
│   │   ├── dto/
│   │   │   ├── ClienteRequestDTO.java      # DTO para crear cliente
│   │   │   ├── ClienteResponseDTO.java     # DTO con fecha probable muerte
│   │   │   └── KpiClientesDTO.java         # DTO de estadísticas
│   │   ├── config/
│   │   │   ├── SwaggerConfig.java          # Configuración OpenAPI
│   │   │   └── CorsConfig.java             # Configuración CORS
│   │   └── ClienteMicroserviceApplication.java
│   └── resources/
│       └── application.yml                  # Configuración YAML
└── test/
    └── java/com/pinapp/cliente_microservice/
        ├── controller/
        │   └── ClienteControllerTest.java   # Tests unitarios controller
        ├── service/
        │   └── ClienteServiceTest.java      # Tests unitarios service
        └── integration/
            └── ClienteIntegrationTest.java  # Tests de integración
```
🔌 API Endpoints
1. Crear Cliente
POST /creacliente
Content-Type: application/json
```
{
  "nombre": "Juan",
  "apellido": "Pérez",
  "edad": 30,
  "fechaNacimiento": "1994-01-15"
}
```
Response: 201 Created
```
{
  "id": 1,
  "nombre": "Juan",
  "apellido": "Pérez",
  "edad": 30,
  "fechaNacimiento": "1994-01-15"
}
```
3. Obtener KPIs de Clientes
GET /kpideclientes
Response: 200 OK
```
{
  "promedioEdad": 35.5,
  "desviacionEstandar": 12.3
}
```
5. Listar Clientes
GET /listclientes
Response: 200 OK
```
  {
    "id": 1,
    "nombre": "Juan",
    "apellido": "Pérez",
    "edad": 30,
    "fechaNacimiento": "1994-01-15",
    "fechaProbableMuerte": "2064-01-15"
}
```
⚙️ Configuración
Desarrollo Local

Clonar el repositorio:

bashgit clone https://github.com/bguzmanech/cliente-microservice-backend.git
cd cliente-microservice-backend

Configuración de la base de datos (application.yml):
```
yamlspring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password: password
  h2:
    console:
      enabled: true
      path: /h2-console
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
```
Ejecutar la aplicación:

bashmvn clean install
mvn spring-boot:run
La API estará disponible en: http://localhost:8080
Consola H2: http://localhost:8080/h2-console

📚 Documentación API
Swagger UI

Local: http://localhost:8080/swagger-ui/index.html
AWS: http://microservice-backend.us-east-2.elasticbeanstalk.com/swagger-ui/index.html

🧪 Testing
El proyecto incluye tests unitarios y de integración completos.
Ejecutar todos los tests:
bashmvn test
Tests con coverage:
bashmvn test jacoco:report
Tests de integración específicos:
bashmvn test -Dtest=ClienteIntegrationTest
Cobertura de Tests:

Controller: Validación de endpoints y respuestas HTTP
Service: Lógica de negocio y cálculos
Integration: Flujo completo de la aplicación
Repository: Queries personalizadas

☁️ Despliegue en AWS
✅ Implementación Actual
El backend está desplegado en AWS Elastic Beanstalk:

URL: http://microservice-backend.us-east-2.elasticbeanstalk.com
Swagger: http://microservice-backend.us-east-2.elasticbeanstalk.com/swagger-ui/index.html

⚠️ Problema Conocido: Mixed Content
Durante las pruebas de integración con el frontend desplegado, se identificó un problema de Mixed Content:

Frontend (AWS Amplify): Servido por HTTPS ✅
Backend (Elastic Beanstalk): Servido por HTTP ❌
Resultado: Los navegadores bloquean las peticiones HTTP desde un sitio HTTPS

🔧 Solución en Proceso

Load Balancer: Parcialmente configurado
Certificado SSL: Intentando obtener certificado ACM

AWS no emite certificados para dominios *.elasticbeanstalk.com
Dominio personalizado requerido (Route 53)

🔧 Configuración CORS
CORS está configurado para permitir requests del frontend:
```
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);
    }
}
```
📊 Modelo de Datos
Entidad Cliente
java@Entity
@Table(name = "clientes")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 50)
    @Column(nullable = false)
    private String nombre;
    
    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, max = 50)
    @Column(nullable = false)
    private String apellido;
    
    @NotNull(message = "La edad es obligatoria")
    @Min(value = 0)
    @Max(value = 150)
    @Column(nullable = false)
    private Integer edad;
    
    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser anterior a hoy")
    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;
}
Cálculo de Fecha Probable de Muerte
El servicio implementa un algoritmo que considera:

Expectativa de vida en Argentina: 76 años
Variación aleatoria: ±5 años
Mínimo de años restantes: 1 año

private LocalDate calcularFechaProbableMuerte(Integer edadActual) {
    final int EXPECTATIVA_VIDA_ARGENTINA = 76;
    int anosRestantes = Math.max(EXPECTATIVA_VIDA_ARGENTINA - edadActual, 5);
    int variacion = (int) (Math.random() * 11) - 5;
    anosRestantes = Math.max(anosRestantes + variacion, 1);
    return LocalDate.now().plusYears(anosRestantes);
}

🐛 Manejo de Errores
Respuestas estandarizadas:
```
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "La edad debe ser mayor a 0",
  "path": "/creacliente"
}
```
🤝 Frontend
Este backend está diseñado para trabajar con el frontend Angular disponible en:
cliente-microservice-frontend
🙏 Agradecimientos
Gracias PinApp por la oportunidad de este challenge técnico, fue un desafío apasionante donde aprendí mucho.
