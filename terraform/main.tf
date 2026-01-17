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

# SQS Queue
module "sqs" {
  source      = "./modules/sqs"
  environment = var.environment
  queue_name  = "notification-queue"
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
