# ===== Build stage =====
FROM maven:3.9.9-eclipse-temurin-21 AS build

ARG MODULE
WORKDIR /workspace

# Copy pom gốc và các module để cache dependency
COPY pom.xml .
COPY common-protos/pom.xml common-protos/pom.xml
COPY common-events/pom.xml common-events/pom.xml
COPY common-dtos/pom.xml common-dtos/pom.xml
COPY gateway-service/pom.xml gateway-service/pom.xml
COPY identity-service/pom.xml identity-service/pom.xml
COPY profile-service/pom.xml profile-service/pom.xml
COPY submission-service/pom.xml submission-service/pom.xml
COPY quiz-service/pom.xml quiz-service/pom.xml
COPY coding-service/pom.xml coding-service/pom.xml
COPY ai-service/pom.xml ai-service/pom.xml
COPY search-service/pom.xml search-service/pom.xml
COPY notification-service/pom.xml notification-service/pom.xml
COPY chat-service/pom.xml chat-service/pom.xml
COPY post-service/pom.xml post-service/pom.xml
COPY payment-service/pom.xml payment-service/pom.xml
COPY org-service/pom.xml org-service/pom.xml


# Tải dependency trước để cache (Không compile)
RUN mvn -q -DskipTests dependency:go-offline

## Copy toàn bộ source
COPY . .

# Build & install proto + events (tạo jar trước)
RUN mvn -q -DskipTests install -pl common-protos

# Build đúng module
RUN mvn -q -DskipTests -pl ${MODULE} -am package

# ===== Runtime stage =====
FROM eclipse-temurin:21-jre
ARG MODULE
ENV JAVA_OPTS=""
WORKDIR /app
COPY --from=build /workspace/${MODULE}/target/*.jar app.jar
EXPOSE 7777
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]