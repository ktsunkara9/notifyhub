output "api_id" {
  description = "ID of the API Gateway"
  value       = aws_api_gateway_rest_api.api.id
}

output "api_endpoint" {
  description = "Full URL of the API endpoint"
  value       = "${aws_api_gateway_stage.stage.invoke_url}/api/v1/notifications"
}

output "stage_name" {
  description = "Name of the API Gateway stage"
  value       = aws_api_gateway_stage.stage.stage_name
}
