# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot application that integrates with HashiCorp Vault for secrets management and is designed to run on Kubernetes. The application demonstrates secure secret retrieval using Kubernetes service account authentication with Vault.

## Build and Development Commands

### Maven Commands
- Build: `./mvnw clean package`
- Run unit tests: `./mvnw test`
- Run integration tests: `./mvnw verify`
- Run application locally: `./mvnw spring-boot:run`
- Skip tests during build: `./mvnw clean package -DskipTests`

### Docker Commands
- Build image: `docker build -t vault-app:latest .`
- Run with Docker Compose: `docker-compose up -d`
- Stop Docker Compose: `docker-compose down`

### Kubernetes Commands
- Apply all manifests: `kubectl apply -f k8s/`
- Check deployment status: `kubectl rollout status deployment/vault-app -n vault-app`
- Port forward for testing: `kubectl port-forward service/vault-app 8080:8080 -n vault-app`
- View logs: `kubectl logs -f deployment/vault-app -n vault-app`

### Local Development Setup
1. Make scripts executable: `chmod +x scripts/*.sh`
2. Start services: `docker-compose up -d`
3. Test health endpoint: `curl http://localhost:8080/api/health`

## Architecture Overview

### Key Integration Patterns
- **Vault Authentication**: Uses Kubernetes service account JWT tokens for authentication to Vault
- **Secret Management**: Database passwords and other secrets are retrieved from Vault's KV v2 engine
- **Configuration**: Bootstrap configuration loads Vault properties before main application context
- **SSL/TLS**: Vault communication uses custom CA certificates mounted as Kubernetes secrets

### Core Components
- **VaultConfig**: Configures Vault client with Kubernetes authentication and SSL settings
- **DatabaseConfig**: Creates DataSource with password retrieved from Vault
- **UserService/Repository**: Standard Spring Data JPA pattern for user management
- **HealthController**: Provides health checks and basic CRUD operations

### Configuration Structure
- `bootstrap.yml`: Vault connection and authentication configuration
- `application.yml`: Main application settings including database and management endpoints
- Vault stores sensitive data under `secret/vault-app/` path

### Testing Strategy
- **Unit Tests**: Mock-based tests with `@SpringBootTest` and `@MockBean`
- **Integration Tests**: Use Testcontainers for PostgreSQL database testing
- **E2E Tests**: Full application testing in CI/CD pipeline
- Test configuration separates unit tests from integration tests using Maven profiles

### Kubernetes Deployment
- Uses custom ServiceAccount with `system:auth-delegator` role for Vault authentication
- Mounts Vault CA certificate as a secret volume
- Includes health/readiness probes for proper lifecycle management
- PostgreSQL runs as a separate deployment with persistent storage

### Development vs Production
- **Local**: Uses Docker Compose with dev mode Vault (token-based auth)
- **Kubernetes**: Uses service account authentication with proper SSL certificates
- Configuration is environment-specific via Spring profiles (`local` vs `kubernetes`)

## Important File Locations
- Maven wrapper: `./mvnw` (use this instead of `mvn`)
- Kubernetes manifests: `k8s/` directory
- Setup scripts: `scripts/` directory
- CI/CD pipeline: `.github/workflows/ci-cd.yml`
- Docker configuration: `Dockerfile` and `docker-compose.yml`

## Security Considerations
- Vault secrets are never logged or exposed in configuration files
- Database passwords are dynamically retrieved from Vault at runtime
- Kubernetes authentication uses short-lived JWT tokens
- SSL/TLS is enforced for all Vault communication in production