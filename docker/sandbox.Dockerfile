FROM python:3.14-slim-bookworm

# docker build -t capstoneprojectpythondocker:latest .
# ===== Công cụ biên dịch =====
#  - build-essential (gcc, g++, make…)
#  - OpenJDK 17 headless  (javac, java, jar)
RUN apt-get update && \
    DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends \
        build-essential \
        openjdk-17-jdk-headless \
        && rm -rf /var/lib/apt/lists/*

# ===== Thư viện Python hay dùng trong competitive-prog / data =====
RUN pip install --no-cache-dir \
      numpy pandas requests matplotlib scipy pillow sympy networkx scikit-learn

# ===== Tạo user hạn quyền & set workspace =====
RUN useradd -m sandbox
WORKDIR /app
RUN chown -R sandbox:sandbox /app
USER sandbox

# ===== Lệnh mặc định – container “nhàn rỗi” =====
CMD ["bash"]
