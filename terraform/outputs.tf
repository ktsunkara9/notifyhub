output "api_endpoint" {
  description = "API Gateway endpoint URL"
  value       = module.api_gateway.api_endpoint
}

output "health_endpoint" {
  description = "Health check endpoint"
  value       = "${module.api_gateway.api_endpoint}/health"
}

output "notifications_endpoint" {
  description = "Notifications endpoint"
  value       = "${module.api_gateway.api_endpoint}/api/v1/notifications"
}

output "queue_url" {
  description = "SQS Queue URL"
  value       = module.sqs.queue_url
}

output "lambda_function_name" {
  description = "Lambda function name"
  value       = module.lambda.function_name
}
