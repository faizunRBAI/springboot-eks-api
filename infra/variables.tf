variable "project_name" {
  description = "Branch-scoped project name — used as a prefix for all cloud resources."
  type        = string
}

variable "aws_region" {
  description = "AWS region to deploy into."
  type        = string
  default     = "us-east-1"
}

variable "kubernetes_version" {
  description = "EKS Kubernetes version. Must be in STANDARD support (1.33–1.36 as of 2026-07)."
  type        = string
  default     = "1.33"
}

variable "node_instance_type" {
  description = "EC2 instance type for the EKS managed node group."
  type        = string
  default     = "t3.medium"
}

variable "node_desired_size" {
  description = "Desired number of nodes in the EKS managed node group."
  type        = number
  default     = 2
}

variable "node_min_size" {
  description = "Minimum number of nodes."
  type        = number
  default     = 1
}

variable "node_max_size" {
  description = "Maximum number of nodes (HPA headroom)."
  type        = number
  default     = 4
}

variable "db_instance_class" {
  description = "RDS instance class."
  type        = string
  default     = "db.t4g.micro"
}

variable "db_name" {
  description = "PostgreSQL database name."
  type        = string
  default     = "appdb"
}

variable "db_username" {
  description = "PostgreSQL master username."
  type        = string
  default     = "appuser"
}

variable "db_password" {
  description = "PostgreSQL master password. Provided via TF_VAR_db_password secret."
  type        = string
  sensitive   = true
}

variable "app_replicas" {
  description = "Number of application pod replicas (informational — Kubernetes owns this)."
  type        = number
  default     = 2
}
