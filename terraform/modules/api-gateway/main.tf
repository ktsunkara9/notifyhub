# REST API
resource "aws_api_gateway_rest_api" "api" {
  name        = "${var.environment}-${var.api_name}"
  description = "NotifyHub Notification API"

  endpoint_configuration {
    types = ["REGIONAL"]
  }

  tags = {
    Name        = "${var.environment}-${var.api_name}"
    Environment = var.environment
    Project     = "NotifyHub"
  }
}

# /api/v1 resource
resource "aws_api_gateway_resource" "api_v1" {
  rest_api_id = aws_api_gateway_rest_api.api.id
  parent_id   = aws_api_gateway_rest_api.api.root_resource_id
  path_part   = "api"
}

resource "aws_api_gateway_resource" "v1" {
  rest_api_id = aws_api_gateway_rest_api.api.id
  parent_id   = aws_api_gateway_resource.api_v1.id
  path_part   = "v1"
}

# /api/v1/notifications resource
resource "aws_api_gateway_resource" "notifications" {
  rest_api_id = aws_api_gateway_rest_api.api.id
  parent_id   = aws_api_gateway_resource.v1.id
  path_part   = "notifications"
}

# POST method
resource "aws_api_gateway_method" "post_notification" {
  rest_api_id   = aws_api_gateway_rest_api.api.id
  resource_id   = aws_api_gateway_resource.notifications.id
  http_method   = "POST"
  authorization = "NONE"
}

# Lambda integration for notifications
resource "aws_api_gateway_integration" "lambda" {
  rest_api_id             = aws_api_gateway_rest_api.api.id
  resource_id             = aws_api_gateway_resource.notifications.id
  http_method             = aws_api_gateway_method.post_notification.http_method
  integration_http_method = "POST"
  type                    = "AWS"
  uri                     = var.lambda_invoke_arn

  request_templates = {
    "application/json" = <<EOF
{
  "path": "$context.resourcePath",
  "httpMethod": "$context.httpMethod",
  "body": $input.json('$')
}
EOF
  }
}

# Method response for POST
resource "aws_api_gateway_method_response" "post_202" {
  rest_api_id = aws_api_gateway_rest_api.api.id
  resource_id = aws_api_gateway_resource.notifications.id
  http_method = aws_api_gateway_method.post_notification.http_method
  status_code = "202"
}

# Integration response for POST
resource "aws_api_gateway_integration_response" "post_response" {
  rest_api_id = aws_api_gateway_rest_api.api.id
  resource_id = aws_api_gateway_resource.notifications.id
  http_method = aws_api_gateway_method.post_notification.http_method
  status_code = "202"

  depends_on = [aws_api_gateway_integration.lambda]
}

# /health resource
resource "aws_api_gateway_resource" "health" {
  rest_api_id = aws_api_gateway_rest_api.api.id
  parent_id   = aws_api_gateway_rest_api.api.root_resource_id
  path_part   = "health"
}

# GET /health
resource "aws_api_gateway_method" "get_health" {
  rest_api_id   = aws_api_gateway_rest_api.api.id
  resource_id   = aws_api_gateway_resource.health.id
  http_method   = "GET"
  authorization = "NONE"
}

# Lambda integration for health
resource "aws_api_gateway_integration" "health" {
  rest_api_id             = aws_api_gateway_rest_api.api.id
  resource_id             = aws_api_gateway_resource.health.id
  http_method             = aws_api_gateway_method.get_health.http_method
  integration_http_method = "POST"
  type                    = "AWS"
  uri                     = var.lambda_invoke_arn

  request_templates = {
    "application/json" = <<EOF
{
  "path": "$context.resourcePath",
  "httpMethod": "$context.httpMethod"
}
EOF
  }
}

# Method response for GET
resource "aws_api_gateway_method_response" "health_200" {
  rest_api_id = aws_api_gateway_rest_api.api.id
  resource_id = aws_api_gateway_resource.health.id
  http_method = aws_api_gateway_method.get_health.http_method
  status_code = "200"
}

# Integration response for GET
resource "aws_api_gateway_integration_response" "health_response" {
  rest_api_id = aws_api_gateway_rest_api.api.id
  resource_id = aws_api_gateway_resource.health.id
  http_method = aws_api_gateway_method.get_health.http_method
  status_code = "200"

  depends_on = [aws_api_gateway_integration.health]
}

# Lambda permission for API Gateway
resource "aws_lambda_permission" "apigw" {
  statement_id  = "AllowAPIGatewayInvoke"
  action        = "lambda:InvokeFunction"
  function_name = var.lambda_function_name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "${aws_api_gateway_rest_api.api.execution_arn}/*/*"
}

# Deployment
resource "aws_api_gateway_deployment" "deployment" {
  rest_api_id = aws_api_gateway_rest_api.api.id

  depends_on = [
    aws_api_gateway_integration.lambda,
    aws_api_gateway_integration.health
  ]

  lifecycle {
    create_before_destroy = true
  }
}

# Stage
resource "aws_api_gateway_stage" "stage" {
  deployment_id = aws_api_gateway_deployment.deployment.id
  rest_api_id   = aws_api_gateway_rest_api.api.id
  stage_name    = var.environment

  tags = {
    Name        = "${var.environment}-stage"
    Environment = var.environment
    Project     = "NotifyHub"
  }
}

# Throttling settings
resource "aws_api_gateway_method_settings" "settings" {
  rest_api_id = aws_api_gateway_rest_api.api.id
  stage_name  = aws_api_gateway_stage.stage.stage_name
  method_path = "*/*"

  settings {
    throttling_burst_limit = var.throttle_burst_limit
    throttling_rate_limit  = var.throttle_rate_limit
    logging_level          = "INFO"
    metrics_enabled        = true
  }
}
