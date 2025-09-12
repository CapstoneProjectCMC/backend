#!/usr/bin/env bash
set -euo pipefail

KEY_NAME="${1:-deploy-ssh-yunomix2834}"
EMAIL_HINT="${2:-deploy-ssh-yunomix2834@gmail.com}"

umask 077
ssh-keygen -t ed25519 -C "$EMAIL_HINT" -f "./${KEY_NAME}" -N ""

echo
echo "Created key pair:"
echo "  Private: ./${KEY_NAME}"
echo "  Public : ./${KEY_NAME}.pub"
echo
echo "Trên server, thêm public key vào ~/.ssh/authorized_keys:"
echo "   cat ${KEY_NAME}.pub >> ~/.ssh/authorized_keys && chmod 600 ~/.ssh/authorized_keys"
echo
echo "Trong GitHub → Secrets, set SSH_PRIVATE_KEY = nội dung file ${KEY_NAME}"
