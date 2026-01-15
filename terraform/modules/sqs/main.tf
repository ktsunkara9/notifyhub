# SQS Queue for notification ingestion
resource "aws_sqs_queue" "notification_queue" {
  name                       = "${var.environment}-${var.queue_name}"
  visibility_timeout_seconds = 300
  message_retention_seconds  = 345600 # 4 days
  max_message_size           = 262144 # 256 KB
  delay_seconds              = 0
  receive_wait_time_seconds  = 20 # Long polling for cost optimization

  tags = {
    Name        = "${var.environment}-${var.queue_name}"
    Environment = var.environment
    Project     = "NotifyHub"
  }
}

# Dead Letter Queue for failed messages
resource "aws_sqs_queue" "notification_dlq" {
  name                       = "${var.environment}-${var.queue_name}-dlq"
  message_retention_seconds  = 1209600 # 14 days
  receive_wait_time_seconds  = 20

  tags = {
    Name        = "${var.environment}-${var.queue_name}-dlq"
    Environment = var.environment
    Project     = "NotifyHub"
  }
}

# Redrive policy - send failed messages to DLQ after 3 attempts
resource "aws_sqs_queue_redrive_policy" "notification_queue_redrive" {
  queue_url = aws_sqs_queue.notification_queue.id
  redrive_policy = jsonencode({
    deadLetterTargetArn = aws_sqs_queue.notification_dlq.arn
    maxReceiveCount     = 3
  })
}
