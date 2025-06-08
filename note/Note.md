### **1. Tạo keystore `gateway.jks` với key pair (RSA)**
```cmd
keytool -genkeypair -alias gateway-https -keyalg RSA -keysize 2048 -keystore gateway.jks -validity 3650 -storepass dinhanst2832004 -keypass dinhanst2832004 -dname "CN=localhost, OU=Gateway, O=CMC University, L=City, ST=State, C=VN"
```
- **Mục đích**: Tạo một **keystore** (`gateway.jks`) chứa cặp khóa RSA dùng cho HTTPS (ví dụ: Spring Boot Gateway).
- **Chi tiết**:
    - `-alias gateway-https`: Đặt tên alias (định danh) cho key pair.
    - `-keyalg RSA`: Thuật toán tạo khóa là RSA.
    - `-keysize 2048`: Độ dài khóa 2048-bit (đủ mạnh cho HTTPS).
    - `-keystore gateway.jks`: Tên file keystore sẽ được tạo.
    - `-validity 3650`: Khóa có hiệu lực trong 10 năm (~3650 ngày).
    - `-storepass dinhanst2832004`: Mật khẩu truy cập keystore.
    - `-keypass dinhanst2832004`: Mật khẩu riêng cho private key.
    - `-dname "CN=..."`: Thông tin chủ sở hữu (Distinguished Name):
        - `CN=localhost`: Tên tổ chức (Common Name).
        - `OU=Gateway`: Đơn vị (Organizational Unit).
        - `O=CMC University`: Tên công ty (Organization).
        - `L=City`, `ST=State`: Thành phố và bang (không bắt buộc).
        - `C=VN`: Mã quốc gia (Việt Nam).

---

### **2. Tạo keystore `identity.jks` với key pair (RSA)**
```cmd
keytool -genkeypair -alias identity-https -keyalg RSA -keysize 2048 -keystore identity.jks -validity 3650 -storepass dinhanst2832004 -keypass dinhanst2832004 -dname "CN=localhost, OU=Identity, O=CMC University, L=City, ST=State, C=VN"
```
- **Mục đích**: Tạo keystore (`identity.jks`) chứa cặp khóa RSA cho dịch vụ xác thực (ví dụ: Spring Security OAuth2).
- **Khác biệt so với lệnh 1**:
    - `-alias identity-https`: Alias khác để phân biệt với Gateway.
    - `OU=Identity`: Đơn vị là "Identity" (thường dùng cho service xác thực).

---

### **3. Xuất certificate từ `identity.jks` ra file `identity.crt`**
```cmd
keytool -exportcert -alias identity-https -keystore identity.jks -file identity.crt -storepass dinhanst2832004
```
- **Mục đích**: Xuất **public key** (dưới dạng certificate) từ keystore `identity.jks` để chia sẻ với các service khác.
- **Chi tiết**:
    - `-alias identity-https`: Chọn alias cần xuất.
    - `-keystore identity.jks`: Keystore chứa alias.
    - `-file identity.crt`: File certificate đầu ra (định dạng X.509).
    - `-storepass dinhanst2832004`: Mật khẩu keystore.

---

### **4. Import certificate `identity.crt` vào `gateway-truststore.jks`**
```cmd
keytool -importcert -alias identity-service -file identity.crt -keystore gateway-truststore.jks -storepass truststorePassword -noprompt
```
- **Mục đích**: Thêm certificate của `identity.jks` vào **truststore** của Gateway để Gateway tin tưởng dịch vụ Identity.
- **Chi tiết**:
    - `-alias identity-service`: Đặt tên alias cho certificate trong truststore.
    - `-file identity.crt`: File certificate cần import.
    - `-keystore gateway-truststore.jks`: Tên truststore (nếu chưa có, sẽ tự động tạo mới).
    - `-storepass truststorePassword`: Mật khẩu truststore (khác với keystore).
    - `-noprompt`: Không hỏi xác nhận khi import.

---

### **Tóm tắt luồng hoạt động**:
1. **Gateway** và **Identity Service** mỗi bên có keystore riêng (`gateway.jks` và `identity.jks`).
2. Identity Service xuất certificate (`identity.crt`) để chia sẻ public key.
3. Gateway nhận certificate này và lưu vào truststore (`gateway-truststore.jks`) để **xác thực kết nối HTTPS** từ Identity Service.

👉 **Ứng dụng thực tế**:
- Khi Gateway gọi API của Identity Service, nó sẽ kiểm tra certificate trong `identity.crt` để đảm bảo kết nối an toàn (SSL/TLS).








keytool -genkeypair -alias gateway-https -keyalg RSA -keysize 2048 -keystore gateway.jks -validity 3650 -storepass dinhanst2832004 -keypass dinhanst2832004 -dname "CN=localhost, OU=Gateway, O=CMC University, L=City, ST=Hanoi, C=VN"

keytool -genkeypair -alias identity-https -keyalg RSA -keysize 2048 -keystore identity.jks -validity 3650 -storepass dinhanst2832004 -keypass dinhanst2832004 -dname "CN=localhost, OU=Identity, O=CMC University, L=City, ST=Hanoi, C=VN"

keytool -genkeypair -alias profile-https -keyalg RSA -keysize 2048 -keystore profile.jks -validity 3650 -storepass dinhanst2832004 -keypass dinhanst2832004 -dname "CN=localhost, OU=Profile, O=CMC University, L=City, ST=Hanoi, C=VN"

keytool -exportcert -alias identity-https -keystore identity.jks -file identity.crt -storepass dinhanst2832004

keytool -exportcert -alias profile-https -keystore profile.jks -file profile.crt -storepass dinhanst2832004

keytool -importcert -alias identity-service -file identity.crt -keystore gateway-truststore.jks -storepass truststorePassword -noprompt

keytool -importcert -alias profile-service -file profile.crt -keystore gateway-truststore.jks -storepass truststorePassword -noprompt