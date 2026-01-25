terraform {
  required_version = ">= 1.0"
  
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = var.aws_region
}

# SQS Queue (Ingestion)
module "sqs" {
  source      = "./modules/sqs"
  environment = var.environment
  queue_name  = "notification-queue"
}

# SQS Queue (High Priority)
module "sqs_high_priority" {
  source      = "./modules/sqs"
  environment = var.environment
  queue_name  = "high-priority-queue"
}

# SQS Queue (Low Priority)
module "sqs_low_priority" {
  source      = "./modules/sqs"
  environment = var.environment
  queue_name  = "low-priority-queue"
}

# Lambda Function (API Handler)
module "lambda" {
  source         = "./modules/lambda"
  environment    = var.environment
  function_name  = "notification-handler"
  lambda_zip_path = var.lambda_zip_path
  queue_arn      = module.sqs.queue_arn

  environment_variables = {
    NOTIFYHUB_SQS_QUEUE_URL = module.sqs.queue_url
  }
}

# API Gateway
module "api_gateway" {
  source               = "./modules/api-gateway"
  environment          = var.environment
  api_name             = "notifyhub-api"
  lambda_invoke_arn    = module.lambda.function_invoke_arn
  lambda_function_name = module.lambda.function_name
}

# Lambda Function (Validator Prioritizer)
module "lambda_validator" {
  source         = "./modules/lambda-validator"
  environment    = var.environment
  function_name  = "validator-prioritizer"
  lambda_zip_path = var.lambda_zip_path
  source_queue_arn = module.sqs.queue_arn
  destination_queue_arns = [
    module.sqs_high_priority.queue_arn,
    module.sqs_low_priority.queue_arn
  ]

  environment_variables = {
    QUEUE_HIGH_PRIORITY_URL = module.sqs_high_priority.queue_url
    QUEUE_LOW_PRIORITY_URL  = module.sqs_low_priority.queue_url
  }
}
