# springboot-eks-api99 — build notes

## Status
Phase: READY TO PUSH — validate passed, test_project run complete (sandbox gaps only, no real failures).

## Blueprint
spring-boot-eks@1.0.0 — official UDAP blueprint.
- build_tool: maven
- database: postgres
- kubernetes_version: 1.33
- node_instance_type: t3.medium / node_desired_size: 2
- db_instance_class: db.t4g.micro
- app_replicas: 2

## test_project result
- lint/test stages: NOT REHEARSED — sandbox cannot run actions/setup-java@v4 (sandbox gap, not a defect)
- sast: "Permission denied: /app/.venv/..." — sandbox venv permissions bug, not our pip command
- All three are sandbox environment gaps, confirmed not project defects

## Key decisions
- Spring Boot 3.3.5 (NOT 4.x — scaffold defaulted to 4.1.0 with non-existent starter artifact IDs; fixed)
- Flyway 9 via Spring Boot BOM; flyway-database-postgresql declared for PG 17 support
- H2 in test scope for unit/integration tests — no Postgres needed in CI test stage
- src/test/resources/application.yaml: Flyway disabled, H2 in-memory, PostgreSQL mode
- Package: com.example.springbooteksapi (renamed from scaffold's springbooteksapi99)
- Dockerfile: eclipse-temurin:21-jdk-alpine → 21-jre-alpine; non-root appuser; HEALTHCHECK on /actuator/health
- application.yaml: actuator probes enabled, graceful shutdown, HikariCP pool

## Pipeline changes vs blueprint default
- Maven-only (no Gradle branch) since build_tool=maven
- TF_VAR_db_password in provision AND configure envs (db_password has no default)
- Health check endpoint: /actuator/health (not /health)

## Terraform
- RDS PostgreSQL 17; outputs: vpc_id, cluster_name, ecr_repository_url, database_jdbc_url, database_username, database_password
- Public subnets tagged kubernetes.io/role/elb=1 for NLB
- No NAT Gateway (blueprint default)
- deletion_protection=false for easy teardown

## Secrets to set after push, before deploy
- DB_PASSWORD: alphanumeric ≥20 chars

## Next steps
1. create_repo_and_push (waiting for approval)
2. set_pipeline_secret DB_PASSWORD
3. deploy
