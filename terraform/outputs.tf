output "api_endpoint" {
  description = "API Gateway endpoint URL"
  value       = module.api_gateway.api_endpoint
}

output "queue_url" {
  description = "SQS Queue URL"
  value       = module.sqs.queue_url
}

output "lambda_function_name" {
  description = "Lambda function name"
  value       = module.lambda.function_name
}
