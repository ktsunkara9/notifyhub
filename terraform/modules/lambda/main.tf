# IAM role for Lambda execution
resource "aws_iam_role" "lambda_role" {
  name = "${var.function_name}-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Action = "sts:AssumeRole"
      Effect = "Allow"
      Principal = {
        Service = "lambda.amazonaws.com"
      }
    }]
  })

  tags = {
    Name        = "${var.function_name}-role"
    Environment = var.environment
    Project     = "NotifyHub"
  }
}

# Attach basic Lambda execution policy (CloudWatch Logs)
resource "aws_iam_role_policy_attachment" "lambda_basic" {
  role       = aws_iam_role.lambda_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
}

# Policy for SQS access (send messages only)
resource "aws_iam_role_policy" "lambda_sqs" {
  name = "${var.function_name}-sqs-policy"
  role = aws_iam_role.lambda_role.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect = "Allow"
      Action = [
        "sqs:SendMessage",
        "sqs:GetQueueUrl"
      ]
      Resource = var.queue_arn
    }]
  })
}

# Lambda function
resource "aws_lambda_function" "function" {
  filename         = var.lambda_zip_path
  function_name    = var.function_name
  role            = aws_iam_role.lambda_role.arn
  handler         = "not.used.in.provided.runtime"
  source_code_hash = filebase64sha256(var.lambda_zip_path)
  runtime         = "provided.al2023"
  memory_size     = var.memory_size
  timeout         = var.timeout

  environment {
    variables = var.environment_variables
  }

  tags = {
    Name        = var.function_name
    Environment = var.environment
    Project     = "NotifyHub"
  }
}

# CloudWatch Log Group
resource "aws_cloudwatch_log_group" "lambda_logs" {
  name              = "/aws/lambda/${aws_lambda_function.function.function_name}"
  retention_in_days = var.log_retention_days

  tags = {
    Name        = "${var.function_name}-logs"
    Environment = var.environment
    Project     = "NotifyHub"
  }
}
