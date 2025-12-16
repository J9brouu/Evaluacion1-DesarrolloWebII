# =====================================================
# MULTI-STAGE DOCKERFILE PARA PRODUCCIÓN (KOYEB/CLOUD)
# Sistema de Gestión de Prácticas
# =====================================================
#
# Dockerfile optimizado para despliegue en Koyeb/Render/Railway
# 
# Características:
# - Multi-stage build (imagen final ligera ~150MB)
# - Puerto dinámico compatible con Koyeb ($PORT)
# - JAR ejecutable standalone
# - Optimizado para cloud con health checks
#
# @version 2.0-production
# =====================================================

# ==================
# ETAPA 1: BUILD
# ==================
FROM maven:3.9-eclipse-temurin-17-alpine AS build

# Metadatos
LABEL maintainer="Sistema de Prácticas"
LABEL stage="builder"

WORKDIR /app

# Copiar dependencias primero (caché Docker)
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Descargar dependencias (se cachea si pom.xml no cambia)
RUN mvn dependency:go-offline -B

# Copiar código fuente y compilar
COPY src ./src
RUN mvn clean package -DskipTests -B -e

# Verificar JAR creado
RUN ls -lh target/*.jar

# ==================
# ETAPA 2: RUNTIME
# ==================
FROM eclipse-temurin:17-jre-alpine

LABEL maintainer="Sistema de Prácticas"
LABEL description="Sistema de Gestión de Prácticas - Producción"
LABEL version="2.0-production"

WORKDIR /app

# Copiar solo el JAR compilado desde etapa de build
COPY --from=build /app/target/*.jar app.jar

# Exponer puerto (Koyeb usa $PORT, default 8000)
EXPOSE 8000

# Variables de entorno para producción
ENV JAVA_OPTS="-Xms256m -Xmx512m -Djava.security.egd=file:/dev/./urandom"
ENV SPRING_PROFILES_ACTIVE=production

# Health check para Koyeb/cloud platforms
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:${PORT:-8000}/actuator/health || exit 1

# Comando de inicio con puerto dinámico
# Koyeb inyecta $PORT automáticamente (default 8000)
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-8000} -jar app.jar"]

# =====================================================
# COMANDOS ÚTILES PARA ESTUDIANTES
# =====================================================
#
# 1. CONSTRUIR LA IMAGEN:
#    docker build -t springboot-dev .
#
# 2. EJECUTAR LOCALMENTE:
#    docker run -p 8080:8080 springboot-app
#
# 3. EJECUTAR CON VOLÚMENES (HOT RELOAD COMPLETO):
#    Los cambios en src/ se reflejan automáticamente
#    
#    En Windows (PowerShell):
#    docker run -d -p 8080:8080 `
#      -v ${PWD}/src:/app/src `
#      -v ${PWD}/target:/app/target `
#      -v maven-repo:/root/.m2 `
#      --name spring-dev `
#      springboot-app
#
#    En Linux/Mac:
#    docker run -d -p 8080:8080 \
#      -v $(pwd)/src:/app/src \
#      -v $(pwd)/target:/app/target \
#      -v maven-repo:/root/.m2 \
#      --name spring-dev \
#      springboot-app
#
#    Nota: maven-repo es un volumen nombrado para cachear dependencias
#
# 4. EJECUTAR CON BASE DE DATOS EXTERNA:
#    docker run -d -p 8080:8080 \
#      -v ${PWD}/src:/app/src \
#      -e SPRING_DATASOURCE_URL="jdbc:mysql://host:3306/db" \
#      -e SPRING_DATASOURCE_USERNAME="user" \
#      -e SPRING_DATASOURCE_PASSWORD="pass" \
#      --name spring-dev \
#      springboot-app
#
# 5. VER LOGS EN TIEMPO REAL:
#    docker logs -f springboot-app
#
# 6. REINICIAR DESPUÉS DE CAMBIOS EN pom.xml:
#    docker restart springboot-app
#
# 7. ENTRAR AL CONTENEDOR:
#    docker exec -it springboot-app sh
#
# 8. DETENER Y ELIMINAR:
#    docker stop springboot-app && docker rm springboot-app
#
# 9. VER CONSUMO DE RECURSOS:
#    docker stats springboot-app
#
# 10. DOCKER COMPOSE (crear docker-compose.yml):
#     version: '3.8'
#     services:
#       app:
#         build: .
#         ports:
#           - "8080:8080"
#         volumes:
#           - ./src:/app/src
#           - ./target:/app/target
#           - maven-repo:/root/.m2
#         environment:
#           - SPRING_PROFILES_ACTIVE=development
#     
#     volumes:
#       maven-repo:
#
#     Ejecutar: docker-compose up -d
#     Ver logs: docker-compose logs -f
#     Detener: docker-compose down
#
# 11. LIMPIAR CACHE DE MAVEN (si hay problemas):
#     docker volume rm maven-repo
#
# =====================================================
