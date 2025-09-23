#!/bin/bash

# Generate CA private key
openssl genrsa -out ca-key.pem 4096

# Generate CA certificate
openssl req -new -x509 -key ca-key.pem -out ca-cert.pem -days 365 -subj "/CN=Vault CA"

# Generate Vault private key
openssl genrsa -out vault-key.pem 4096

# Generate Vault certificate signing request
openssl req -new -key vault-key.pem -out vault-csr.pem -subj "/CN=vault"

# Generate Vault certificate
openssl x509 -req -in vault-csr.pem -CA ca-cert.pem -CAkey ca-key.pem -CAcreateserial -out vault-cert.pem -days 365

echo "Certificates generated successfully!"