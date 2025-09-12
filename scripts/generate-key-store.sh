#!/usr/bin/env bash
set -euo pipefail

# MSYS đang “đổi” đối số -subj "/CN=..." 
# thành đường dẫn C:/Program Files/Git/CN=..., khiến OpenSSL báo lỗi.
# Nên thêm 2 dòng dưới đây để fix
if [[ -n "${MSYSTEM-}" ]]; then
  export MSYS2_ARG_CONV_EXCL='*'
fi

# 1. Root CA (dùng lại cho tất cả)
mkdir -p ssl && cd ssl
openssl genrsa -out ca.key 4096
openssl req -x509 -new -nodes -key ca.key -sha256 -days 3650 \
  -out ca.crt -subj "/CN=codecampus-local-CA"

# 2. Hàm tiện ích (bash) để tạo cert cho từng service
#   SAN gồm: localhost, <service>-service (khi chạy Docker), và 127.0.0.1
gen_cert () {
  SVC="$1" ; CN="${2:-localhost}" ; PASS="${3:-changeit}"
  openssl genrsa -out "${SVC}.key" 2048
  cat > "${SVC}.cnf" <<EOF
[req]
default_bits = 2048
distinguished_name = dn
req_extensions = req_ext
prompt = no
[dn]
CN = ${CN}
[req_ext]
subjectAltName = @alt_names
[alt_names]
DNS.1 = localhost
DNS.2 = ${SVC}-service
IP.1  = 127.0.0.1
EOF
  openssl req -new -key "${SVC}.key" -out "${SVC}.csr" -config "${SVC}.cnf"
  openssl x509 -req -in "${SVC}.csr" -CA ca.crt -CAkey ca.key -CAcreateserial \
    -out "${SVC}.crt" -days 825 -sha256 -extfile "${SVC}.cnf" -extensions req_ext
  cat "${SVC}.crt" ca.crt > "${SVC}-chain.crt"

  # PKCS12 keystore (server private key + chain)
  openssl pkcs12 -export \
    -inkey "${SVC}.key" \
    -in "${SVC}-chain.crt" \
    -name "${SVC}-https" \
    -out "${SVC}.p12" \
    -passout pass:${PASS}
}

# 3. Tạo cho tất cả services (ít nhất: gateway, identity, profile, ...)
gen_cert gateway localhost changeit
gen_cert identity localhost changeit
gen_cert profile localhost changeit
gen_cert submission localhost changeit
gen_cert coding localhost changeit
gen_cert quiz localhost changeit
gen_cert ai localhost changeit
gen_cert search localhost changeit
gen_cert notification localhost changeit
gen_cert chat localhost changeit
gen_cert post localhost changeit
gen_cert payment localhost changeit
gen_cert organization localhost changeit

# 4. Truststore (chứa CA dùng để verify tất cả cert)
keytool -importcert -file ca.crt -alias codecampus-local-ca \
  -keystore truststore.p12 -storetype PKCS12 -storepass changeit -noprompt
