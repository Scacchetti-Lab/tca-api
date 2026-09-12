FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY . .
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

ENV JAVA_OPTS="-Xmx350m -Xss256k"

EXPOSE 8080
CMD ["java", "$JAVA_OPTS", "-Dserver.port=${PORT:-8080}", "-jar", "app.jar"]