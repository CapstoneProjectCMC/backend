#!/bin/bash
set -euo pipefail

# --- Lấy mật khẩu superuser (ưu tiên biến của Bitnami, fallback sang PG_SUPER_PASSWORD) ---
export PGPASSWORD="${POSTGRESQL_POSTGRES_PASSWORD:-${POSTGRESQL_PASSWORD:-${PG_SUPER_PASSWORD:-}}}"

# Set =0 nếu KHÔNG muốn cấp CREATE trên schema public
GRANT_CREATE_ON_PUBLIC="${GRANT_CREATE_ON_PUBLIC:-1}"

psql_super() {
  psql -U postgres -d postgres -v ON_ERROR_STOP=1 "$@"
}

psql_db() {
  local db="$1"; shift
  psql -U postgres -d "${db}" -v ON_ERROR_STOP=1 "$@"
}

create_db_user () {
  local db="$1" user="$2" pass="$3"

  if [[ -z "${db}" || -z "${user}" || -z "${pass}" ]]; then
    echo "❌ Thiếu tham số: db='${db}', user='${user}'"; exit 1
  fi

  # Tạo database nếu chưa có
  if ! psql -U postgres -tAc "SELECT 1 FROM pg_database WHERE datname='${db}'" | grep -q 1; then
    createdb -U postgres "${db}"
    echo "✅ Created database ${db}"
  else
    echo "ℹ️  Database ${db} đã tồn tại"
  fi

  # Tạo role/app user nếu chưa có
  psql_super <<-SQL
    DO \$\$
    BEGIN
      IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname='${user}') THEN
        CREATE ROLE "${user}" LOGIN PASSWORD '${pass}';
      END IF;
    END
    \$\$;
SQL

  # Cấp quyền ở mức database (CONNECT/CREATE/TEMP)
  psql_super -c "GRANT ALL PRIVILEGES ON DATABASE \"${db}\" TO \"${user}\";"

  # Tạo schema riêng cho user (nếu chưa có) và set search_path
  psql_db "${db}" <<-SQL
    DO \$\$
    BEGIN
      IF NOT EXISTS (SELECT 1 FROM pg_namespace WHERE nspname='${user}') THEN
        EXECUTE format('CREATE SCHEMA "%I" AUTHORIZATION "%I";', '${user}', '${user}');
      END IF;
    END
    \$\$;

    -- Đặt search_path để app mặc định dùng schema riêng
    ALTER ROLE "${user}" IN DATABASE "${db}" SET search_path = '"${user}"', public;
SQL

  # Cấp quyền trên schema public để tránh lỗi nếu app vẫn tạo ở public
  if [[ "${GRANT_CREATE_ON_PUBLIC}" == "1" ]]; then
    psql_db "${db}" <<-SQL
      GRANT USAGE, CREATE ON SCHEMA public TO "${user}";
SQL
  fi

  echo "✅ Ready user=${user}, db=${db}, schema=${user}"
}

create_db_user "${IDENTITY_DATABASE}"     "${IDENTITY_USERNAME}"     "${IDENTITY_DB_PASSWORD}"
create_db_user "${QUIZ_DATABASE}"         "${QUIZ_USERNAME}"         "${QUIZ_DB_PASSWORD}"
create_db_user "${SUBMISSION_DATABASE}"   "${SUBMISSION_USERNAME}"   "${SUBMISSION_DB_PASSWORD}"
create_db_user "${CODING_DATABASE}"       "${CODING_USERNAME}"       "${CODING_DB_PASSWORD}"
create_db_user "${AI_DATABASE}"           "${AI_USERNAME}"           "${AI_DB_PASSWORD}"
create_db_user "${POST_DATABASE}"         "${POST_USERNAME}"         "${POST_DB_PASSWORD}"
create_db_user "${PAYMENT_DATABASE}"      "${PAYMENT_USERNAME}"      "${PAYMENT_DB_PASSWORD}"
create_db_user "${ORGANIZATION_DATABASE}" "${ORGANIZATION_USERNAME}" "${ORGANIZATION_DB_PASSWORD}"
