variable "environment" {
  description = "Environment name (dev, staging, prod)"
  type        = string
}

variable "function_name" {
  description = "Name of the Lambda function"
  type        = string
}

variable "lambda_zip_path" {
  description = "Path to the Lambda deployment package (ZIP file)"
  type        = string
}

variable "source_queue_arn" {
  description = "ARN of the source SQS queue (ingestion queue)"
  type        = string
}

variable "destination_queue_arns" {
  description = "ARNs of destination SQS queues (high/low priority)"
  type        = list(string)
}

variable "memory_size" {
  description = "Memory allocated to Lambda function (MB)"
  type        = number
  default     = 512
}

variable "timeout" {
  description = "Lambda function timeout (seconds)"
  type        = number
  default     = 60
}

variable "batch_size" {
  description = "Maximum number of messages to process per invocation"
  type        = number
  default     = 10
}

variable "batch_window" {
  description = "Maximum time to wait for batch accumulation (seconds)"
  type        = number
  default     = 5
}

variable "max_concurrency" {
  description = "Maximum concurrent Lambda invocations"
  type        = number
  default     = 10
}

variable "log_retention_days" {
  description = "CloudWatch log retention period (days)"
  type        = number
  default     = 7
}

variable "environment_variables" {
  description = "Environment variables for Lambda"
  type        = map(string)
}
