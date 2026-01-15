variable "environment" {
  description = "Environment name (dev, staging, prod)"
  type        = string
}

variable "queue_name" {
  description = "Name of the SQS queue"
  type        = string
}
