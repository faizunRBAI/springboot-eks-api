output "vpc_id" {
  description = "VPC ID — used by the destroy workflow to identify the Kubernetes-created NLB."
  value       = aws_vpc.main.id
}

output "cluster_name" {
  description = "EKS cluster name."
  value       = aws_eks_cluster.main.name
}

output "cluster_endpoint" {
  description = "EKS API server endpoint."
  value       = aws_eks_cluster.main.endpoint
}

output "ecr_repository_url" {
  description = "ECR repository URL for image push/pull."
  value       = aws_ecr_repository.app.repository_url
}

output "database_endpoint" {
  description = "RDS instance hostname."
  value       = aws_db_instance.postgres.address
  sensitive   = true
}

output "database_jdbc_url" {
  description = "JDBC connection URL for the Spring Boot application."
  value       = "jdbc:postgresql://${aws_db_instance.postgres.address}:${aws_db_instance.postgres.port}/${aws_db_instance.postgres.db_name}"
  sensitive   = true
}

output "database_username" {
  description = "RDS master username."
  value       = aws_db_instance.postgres.username
  sensitive   = true
}

output "database_password" {
  description = "RDS master password."
  value       = aws_db_instance.postgres.password
  sensitive   = true
}

output "aws_region" {
  description = "Deployment region."
  value       = var.aws_region
}
