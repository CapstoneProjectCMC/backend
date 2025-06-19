# khởi tạo & chạy nền

docker compose up -d

# Kết thúc chạy

docker compose down

# Chạy + build lại

docker compose up -d --build

# xem log từng dịch vụ

docker compose logs -f postgresql
docker compose logs -f neo4j

#1 POSTGRESQL
docker pull bitnami/postgresql
docker run -d --name postgresql ^
-e POSTGRESQL_USERNAME=postgres ^
-e POSTGRESQL_PASSWORD=dinhanst2832004 ^
-e POSTGRESQL_DATABASE=identity_db ^
-e POSTGRESQL_POSTGRES_PASSWORD=dinhanst2832004 ^
-p 5431:5432 ^
bitnami/postgresql:latest

#2 NEO4J
docker pull bitnami/neo4j
docker run -d --name neo4j ^
-e NEO4J_DAEMON_USER=neo4j ^
-e NEO4J_BOLT_PORT_NUMBER=7687 ^
-e NEO4J_PASSWORD=dinhanst2832004 ^
-p 7689:7687 ^
bitnami/neo4j:latest
