# Spring Boot API on Amazon EKS

Production-ready REST API built with Spring Boot 3 / Java 21, deployed on Amazon EKS with a managed PostgreSQL database, a Network Load Balancer and a seven-gate security pipeline.

---

## Architecture

```
API Clients
    │ HTTPS
    ▼
Network Load Balancer (AWS NLB)
    │ port 80
    ▼
Spring Boot Pods (EKS — 2 replicas, HPA up to 8)
    │ SQL 5432
    ▼
RDS PostgreSQL 17 (encrypted, 7-day backups)
```

Full diagram: [`.udap/architecture.d2`](.udap/architecture.d2)

Infrastructure: EKS 1.33 (2 × t3.medium), VPC with 2 public + 2 private subnets across 2 AZs, ECR (scan-on-push), KMS envelope encryption.

---

## Local development

**Prerequisites:** Java 21, Maven 3.9+, Docker, a local PostgreSQL instance (or `docker compose up db`).

```bash
# Set environment variables
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/appdb
export SPRING_DATASOURCE_USERNAME=appuser
export SPRING_DATASOURCE_PASSWORD=<your-local-password>

# Run the application
./mvnw spring-boot:run

# Run tests (uses H2 in-memory — no Postgres needed)
./mvnw test

# Check coding standards
./mvnw checkstyle:check
```

**Endpoints available locally:**

| Endpoint | Description |
|---|---|
| `GET /` | Landing page (static HTML) |
| `GET /api/info` | Service name, status, timestamp |
| `GET /api/items` | List all items |
| `GET /actuator/health` | Spring Boot health aggregate |
| `GET /actuator/health/liveness` | Kubernetes liveness probe target |
| `GET /actuator/health/readiness` | Kubernetes readiness probe target |

---

## Deployment pipeline

Push to `main` triggers the full pipeline:

| Stage | What it does |
|---|---|
| `lint` | Checkstyle against `config/checkstyle/checkstyle.xml` |
| `test` | Maven unit tests (JUnit 5, MockMvc, H2) |
| `sast` | Semgrep — `p/default`, `p/owasp-top-ten`, `p/java` |
| `secrets_scan` | Gitleaks 8 secret scan |
| `sbom` | CycloneDX bill of materials (JSON) |
| `license_scan` | Dependency licence compliance against the reviewed allowlist |
| `iac_scan` | Trivy IaC scan (CRITICAL blocking) + Checkov advisory |
| `provision` | Terraform: VPC, EKS, ECR, RDS PostgreSQL, KMS |
| `build_push` | Docker multi-stage build → ECR push |
| `image_scan` | Trivy container scan (HIGH/CRITICAL, unfixed only) + image SBOM |
| `configure` | `kubectl`: namespace, secret, Flyway migration Job, manifests, rollout |
| `verify` | NLB address wait + `/actuator/health` health check (10-minute retry) |
| `notify` | GitHub Step Summary with cluster details and live URL |

Security artefacts (Semgrep SARIF, Gitleaks SARIF, dependency SBOM, image SBOM, licence CSV) are attached to every workflow run and retained for 30–90 days.

---

## Configuration

All runtime configuration is injected via environment variables. The application never reads secrets from files.

| Variable | Source | Description |
|---|---|---|
| `SPRING_DATASOURCE_URL` | Kubernetes Secret `app-database` | JDBC URL for RDS PostgreSQL |
| `SPRING_DATASOURCE_USERNAME` | Kubernetes Secret `app-database` | Database username |
| `SPRING_DATASOURCE_PASSWORD` | Kubernetes Secret `app-database` | Database password (secret) |

The Kubernetes Secret is written by the `configure` stage from Terraform outputs — you never handle the password directly.

**CI secrets** (set before deploying):

| Secret | Description |
|---|---|
| `DB_PASSWORD` | RDS master password — set via `set_pipeline_secret` |
| `AWS_ACCESS_KEY_ID` | Platform-managed |
| `AWS_SECRET_ACCESS_KEY` | Platform-managed |
| `TF_STATE_BUCKET` | Platform-managed |
| `PROJECT_NAME` | Platform-managed |

---

## Operations

**Check the live URL:**
```bash
aws eks update-kubeconfig --name <project>-eks --region us-east-1
kubectl get svc api -n <project>
```

**View application logs:**
```bash
kubectl logs -n <project> -l app.kubernetes.io/name=api --tail=100 -f
```

**Check pod status:**
```bash
kubectl get pods -n <project> -o wide
kubectl describe pods -n <project> -l app.kubernetes.io/name=api
```

**Run a database migration locally:**
```bash
./mvnw package -DskipTests
bin/migrate
```

**Scale replicas manually:**
```bash
kubectl scale deployment/api -n <project> --replicas=4
```

**Destroy the stack:** Use the platform's Destroy action in the UI. The pipeline deletes the Kubernetes LoadBalancer service first (so AWS releases the NLB), waits for it to drain, then runs `terraform destroy` — with an automatic retry for lingering network interfaces.

---

## Adding features

1. Add new domain entities under `src/main/java/com/example/springbooteksapi/domain/`
2. Add REST controllers under `src/main/java/com/example/springbooteksapi/api/`
3. Add Flyway migrations as `src/main/resources/db/migration/V<n>__description.sql` — **never edit an applied migration**
4. Add tests under `src/test/java/com/example/springbooteksapi/`
5. Push to `main` — the pipeline handles the rest

---

## Estimated cost (us-east-1)

| Resource | Monthly |
|---|---|
| EKS control plane | ~$73 |
| 2 × t3.medium nodes | ~$60 |
| RDS db.t4g.micro (PostgreSQL 17) | ~$12 |
| ECR, KMS, NLB, data transfer | ~$10 |
| **Total** | **~$155/mo** |

Optional enhancements: NAT Gateway (~+$32), Multi-AZ RDS (~+$24), Prometheus/Grafana monitoring stack.
