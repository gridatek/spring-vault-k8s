#!/bin/bash

export VAULT_ADDR=http://localhost:8200
export VAULT_TOKEN=myroot

# Enable KV secrets engine
vault secrets enable -path=secret kv-v2

# Create database password secret
vault kv put secret/vault-app database.password=supersecretpassword

echo "Vault setup completed successfully!"