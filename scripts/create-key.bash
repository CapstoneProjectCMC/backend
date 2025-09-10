# Tạo thư mục ~/.ssh (nếu chưa có) và set quyền đúng
mkdir -p ~/.ssh && chmod 700 ~/.ssh

# Tạo file deploy-ssh-yunomix2834.pub với nội dung của public key
cat > ~/.ssh/deploy-ssh-yunomix2834.pub <<'EOF'
ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIEW/9NznXyBGgs7e3gvDwqCbjqZKF2E3XCknvacgQubm deploy-ssh-yunomix2834@gmail.com
EOF

# Set permission cho file public key
chmod 644 ~/.ssh/deploy-ssh-yunomix2834.pub

# Tạo authorized_keys nếu chưa có
touch ~/.ssh/authorized_keys && chmod 600 ~/.ssh/authorized_keys

# Chỉ thêm nếu chưa tồn tại để tránh trùng lặp
grep -qxF -f ~/.ssh/deploy-ssh-yunomix2834.pub ~/.ssh/authorized_keys \
  || cat ~/.ssh/deploy-ssh-yunomix2834.pub >> ~/.ssh/authorized_keys

# Kiểm tra nhanh
ssh-keygen -lf ~/.ssh/deploy-ssh-yunomix2834.pub
# sẽ in fingerprint của key để xác nhận
