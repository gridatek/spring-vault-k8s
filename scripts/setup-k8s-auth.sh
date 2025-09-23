#!/bin/bash

export VAULT_ADDR=http://localhost:8200
export VAULT_TOKEN=myroot

# Enable Kubernetes auth method
vault auth enable kubernetes

# Configure Kubernetes auth method
vault write auth/kubernetes/config \
    token_reviewer_jwt="$(cat /var/run/secrets/kubernetes.io/serviceaccount/token)" \
    kubernetes_host="https://$KUBERNETES_PORT_443_TCP_ADDR:443" \
    kubernetes_ca_cert=@/var/run/secrets/kubernetes.io/serviceaccount/ca.crt

# Create a role for the application
vault write auth/kubernetes/role/vault-app-role \
    bound_service_account_names=vault-app \
    bound_service_account_namespaces=vault-app \
    policies=vault-app-policy \
    ttl=24h

# Create policy for the application
vault policy write vault-app-policy - <<EOF
path "secret/data/vault-app/*" {
  capabilities = ["read"]
}
EOF

echo "Kubernetes authentication setup completed successfully!"