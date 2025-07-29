FROM python:3.9-slim

# Cài các công cụ biên dịch cần thiết (gcc g++)
RUN apt-get update && \
    apt-get install -y gcc g++ && \
    rm -rf /var/lib/apt/lists/*

# Cài sẵn các thư viện Python phổ biến cho competitive programming & data
RUN pip install --no-cache-dir \
    numpy \
    pandas \
    requests \
    matplotlib \
    scipy \
    pillow \
    sympy \
    networkx \
    scikit-learn

# Tạo user không phải root
RUN useradd -m sandbox

# Set working directory
WORKDIR /app

# Chuyển quyền thư mục cho user
RUN chown -R sandbox:sandbox /app

# Chạy container dưới user sandbox
USER sandbox

CMD ["python3", "/app/Main.py"]
