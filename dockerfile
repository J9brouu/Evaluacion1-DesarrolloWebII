# =====================================================
# DOCKERFILE PARA DESARROLLO CON SPRING BOOT + MAVEN
# Sistema de Gestión de Prácticas
# =====================================================
#
# Este Dockerfile ejecuta directamente con Maven (mvn spring-boot:run)
# IDEAL PARA DESARROLLO: Hot reload, debugging, cambios rápidos
#
# Ventajas:
# - Los cambios en código se reflejan automáticamente
# - No necesitas reconstruir la imagen constantemente
# - Puedes usar volúmenes para sincronizar src/
# - Maven descarga dependencias automáticamente
#
# Desventajas:
# - Imagen más pesada (~800MB con Maven + JDK)
# - NO recomendado para producción
#
# @author Sistema de Prácticas
# @version 1.0-dev
# =====================================================

# Usar imagen con Maven + JDK
FROM maven:3.9-eclipse-temurin-17-alpine

# Metadatos de la imagen
LABEL maintainer="Sistema de Prácticas <practicas@universidad.edu>"
LABEL description="Sistema de Gestión de Prácticas - Modo Desarrollo"
LABEL version="1.0-dev"

# Establecer directorio de trabajo
WORKDIR /app

# Copiar archivos de configuración primero (para aprovechar caché)
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Descargar dependencias (se cachea si pom.xml no cambia)
RUN mvn dependency:go-offline -B

# Copiar el código fuente
COPY src ./src

# ==================================================
# COMPILAR Y EMPAQUETAR LA APLICACIÓN
# ==================================================
# clean: limpia compilaciones previas
# package: compila y empaqueta en target/
# -DskipTests: omite tests para acelerar
# -B: modo batch (sin interacción)
# -e: mostrar errores completos
RUN mvn clean package -DskipTests -B -e

# Verificar que el JAR se creó correctamente
RUN ls -l target/*.jar

# Exponer puerto
EXPOSE 8080

# Variables de entorno
ENV MAVEN_OPTS="-Xmx512m"
ENV SPRING_PROFILES_ACTIVE=development

# =====================================================
# COMANDO DE INICIO - EJECUTAR CON MAVEN
# =====================================================
# spring-boot:run ejecuta la aplicación sin crear JAR
# Permite hot reload de recursos estáticos (templates, CSS, JS)
CMD ["mvn", "spring-boot:run"]

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
