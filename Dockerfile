# Build stage (Etapa de Construcción) generar el archivo jar
# ----------------------------------------------------------------------
# 1. FROM: Inicia la primera etapa usando una imagen que tiene Maven y JDK 17. 
#    Se le asigna el nombre 'build' para referenciarla más tarde.
FROM maven:3.9.9-eclipse-temurin-17 AS build

# 2. WORKDIR: Define el directorio de trabajo dentro del contenedor.
WORKDIR /app

# 3. COPY: Copia el archivo de configuración de Maven (pom.xml) al contenedor.
COPY pom.xml .

# 4. RUN: Ejecuta Maven para precargar las dependencias. Esto es opcional pero
#    puede acelerar la construcción si las dependencias no cambian con frecuencia.
RUN mvn -q -B -DskipTests dependency:go-offline

# 5. COPY: Copia el código fuente (carpeta src) al contenedor.
COPY src ./src

# 6. RUN: Ejecuta Maven para compilar, resolver dependencias y empaquetar 
#    la aplicación en un .jar. Se omiten las pruebas para acelerar el proceso.
RUN mvn -q -B clean package -DskipTests

# Run stage (Etapa de Ejecución) ejecuta el archivo jar generado
# ----------------------------------------------------------------------
# 7. FROM: Inicia la etapa final con una imagen mínima que solo tiene el JRE 17.
#    Esto reduce drásticamente el tamaño de la imagen final.
FROM eclipse-temurin:17-jre-jammy

# 8. WORKDIR: Define el directorio de trabajo por defecto para la ejecución.
WORKDIR /app

# 9. COPY --from=build: Copia SOLAMENTE el archivo JAR final (generado en la etapa 'build')
#    y lo nombra como app.jar en esta nueva imagen ligera.
COPY --from=build /app/target/*-SNAPSHOT.jar app.jar

# 10. EXPOSE: Documenta el puerto en el que la aplicación Spring Boot estará escuchando.
EXPOSE 8080

# 11. ENTRYPOINT: Define el comando que se ejecutará al iniciar el contenedor,
#     lanzando la aplicación Java.
ENTRYPOINT ["java", "-jar", "app.jar"]