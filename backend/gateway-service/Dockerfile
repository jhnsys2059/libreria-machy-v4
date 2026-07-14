# Build stage
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app
COPY pom.xml .
COPY backend/discovery-service/pom.xml backend/discovery-service/pom.xml
COPY backend/gateway-service/pom.xml backend/gateway-service/pom.xml
COPY backend/auth-service/pom.xml backend/auth-service/pom.xml
COPY backend/product-service/pom.xml backend/product-service/pom.xml
COPY backend/sale-service/pom.xml backend/sale-service/pom.xml

RUN mvn dependency:go-offline -pl backend/gateway-service -am || true
COPY . .
RUN mvn clean package -pl backend/gateway-service -am -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/backend/gateway-service/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
