#SSL
REM === 1.1 Gateway (client + server) ===
keytool -genkeypair ^
-alias gateway-https ^
-keyalg RSA -keysize 2048 ^
-keystore gateway.jks ^
-validity 3650 ^
-storepass dinhanst2832004 ^
-keypass   dinhanst2832004 ^
-dname "CN=localhost, OU=Gateway, O=CMC University, L=City, ST=Hanoi, C=VN"

REM === 1.2 Identity service ===
keytool -genkeypair ^
-alias identity-https ^
-keyalg RSA -keysize 2048 ^
-keystore identity.jks ^
-validity 3650 ^
-storepass dinhanst2832004 ^
-keypass   dinhanst2832004 ^
-dname "CN=localhost, OU=Identity, O=CMC University, L=City, ST=Hanoi, C=VN"

REM === 1.3 Profile service ===
keytool -genkeypair ^
-alias profile-https ^
-keyalg RSA -keysize 2048 ^
-keystore profile.jks ^
-validity 3650 ^
-storepass dinhanst2832004 ^
-keypass   dinhanst2832004 ^
-dname "CN=localhost, OU=Profile, O=CMC University, L=City, ST=Hanoi, C=VN"

keytool -exportcert -alias identity-https -keystore identity.jks -storepass dinhanst2832004 -file identity.crt
keytool -exportcert -alias profile-https -keystore profile.jks  -storepass dinhanst2832004 -file profile.crt

keytool -importcert -alias identity-service -file identity.crt -keystore gateway-truststore.jks -storepass dinhanst2832004 -noprompt
keytool -importcert -alias profile-service  -file profile.crt  -keystore gateway-truststore.jks -storepass dinhanst2832004 -noprompt

keytool -list -v -keystore gateway-truststore.jks -storepass dinhanst2832004

src\main\resources\ssl\
├─ gateway.jks
├─ gateway-truststore.jks
├─ identity.crt
└─ profile.crt

server:
    port: 8888
    ssl:
        enabled: true
        key-alias: gateway-https
        key-store: classpath:ssl/gateway.jks
        key-store-type: JKS
        key-store-password: dinhanst2832004
        key-password: dinhanst2832004

keytool -exportcert -alias identity-https -keystore identity.jks -storepass dinhanst2832004 -rfc -file ssl\identity.pem
keytool -exportcert -alias profile-https  -keystore profile.jks  -storepass dinhanst2832004 -rfc -file ssl\profile.pem

server:
    port: 8888
    ssl:
        enabled: true
        key-alias: gateway-https
        key-store: classpath:ssl/gateway.jks
        key-store-password: dinhanst2832004
        key-store-type: JKS

spring:
    cloud:
        gateway:
            httpclient:
                ssl:
                    # DÙNG PEM → Không cần trust-store JKS nữa
                    trusted-x509-certificates:
                    - classpath:ssl/identity.pem
                    - classpath:ssl/profile.pem

