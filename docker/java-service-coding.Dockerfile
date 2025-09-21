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
RUN apt-get update && \
    apt-get install -y \
        apt-transport-https \
        ca-certificates \
        curl \
        gnupg && \
    curl -fsSL https://download.docker.com/linux/debian/gpg | gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg && \
    echo "deb [arch=amd64 signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/debian buster stable" > /etc/apt/sources.list.d/docker.list && \
    apt-get update && \
    apt-get install -y docker-ce-cli

## Copy toàn bộ source
COPY . .

# Build & install proto + events (tạo jar trước)
RUN mvn -q -DskipTests install -pl common-protos

# Build đúng module
RUN mvn -q -DskipTests -pl ${MODULE} -am package

# ===== Runtime stage =====
FROM eclipse-temurin:21-jre

# Build-args
ARG MODULE
ARG DOCKER_HOST_GID=999

# Cài đặt Docker CLI trong runtime image
RUN apt-get update && \
    apt-get install -y \
        apt-transport-https \
        ca-certificates \
        curl \
        gnupg \
        sudo && \
    curl -fsSL https://download.docker.com/linux/debian/gpg | gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg && \
    echo "deb [arch=amd64 signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/debian buster stable" > /etc/apt/sources.list.d/docker.list && \
    apt-get update && \
    apt-get install -y docker-ce-cli

# Tạo group và user cho ứng dụng (chỉ cho coding-service)
RUN if [ "$MODULE" = "coding-service" ]; then \
      groupadd -r -g ${DOCKER_HOST_GID} docker_host && \
      groupadd -r -g 1001 appuser && \
      useradd  -r -u 1001 -g appuser -G docker_host appuser && \
      echo "appuser ALL=(root) NOPASSWD: /usr/bin/docker" >> /etc/sudoers; \
    fi

# TẠO THƯ MỤC /WORK VÀ CẤP QUYỀN
RUN if [ "$MODULE" = "coding-service" ]; then \
      mkdir -p /work && chown -R 1001:1001 /work; \
    fi

# Thiết lập thư mục làm việc
WORKDIR /app

# Copy JAR với quyền sở hữu phù hợp
COPY --from=build /workspace/${MODULE}/target/*.jar app.jar

# Đặt quyền sở hữu cho coding-service
RUN if [ "$MODULE" = "coding-service" ]; then \
      chown appuser:appuser app.jar; \
    fi

# Đảm bảo quyền đọc
RUN chmod +r app.jar

# Chuyển sang sử dụng user appuser (chỉ cho coding-service)
USER appuser

ENV JAVA_OPTS=""
EXPOSE 7777
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]