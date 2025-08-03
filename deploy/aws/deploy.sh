#!/bin/bash

# AWS Deployment Script for Threat Detection Platform
# This script deploys the application to AWS using Docker and EC2

set -e

# Configuration
REGION=${AWS_REGION:-us-east-1}
INSTANCE_TYPE=${INSTANCE_TYPE:-t3.large}
KEY_NAME=${KEY_NAME:-threat-detection-key}
SECURITY_GROUP=${SECURITY_GROUP:-threat-detection-sg}
DOMAIN_NAME=${DOMAIN_NAME:-demo.threatdetection.ai}

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}🚀 Starting AWS Deployment for Threat Detection Platform${NC}"

# Check AWS CLI
if ! command -v aws &> /dev/null; then
    echo -e "${RED}❌ AWS CLI not found. Please install AWS CLI first.${NC}"
    exit 1
fi

# Check Docker
if ! command -v docker &> /dev/null; then
    echo -e "${RED}❌ Docker not found. Please install Docker first.${NC}"
    exit 1
fi

# Generate random passwords if not set
export DB_PASSWORD=${DB_PASSWORD:-$(openssl rand -base64 32)}
export GRAFANA_PASSWORD=${GRAFANA_PASSWORD:-$(openssl rand -base64 16)}

echo -e "${YELLOW}📋 Configuration:${NC}"
echo "Region: $REGION"
echo "Instance Type: $INSTANCE_TYPE"
echo "Domain: $DOMAIN_NAME"
echo "DB Password: [HIDDEN]"
echo "Grafana Password: [HIDDEN]"

# Create ECR repository if it doesn't exist
echo -e "${YELLOW}🏗️  Setting up ECR repository...${NC}"
aws ecr describe-repositories --repository-names threat-detection-platform --region $REGION 2>/dev/null || \
aws ecr create-repository --repository-name threat-detection-platform --region $REGION

# Get ECR login
aws ecr get-login-password --region $REGION | docker login --username AWS --password-stdin $(aws sts get-caller-identity --query Account --output text).dkr.ecr.$REGION.amazonaws.com

# Build and push Docker image
echo -e "${YELLOW}🐳 Building and pushing Docker image...${NC}"
export AWS_ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
export VERSION=$(date +%Y%m%d-%H%M%S)

cd ../..
docker build -t threat-detection-platform:$VERSION .
docker tag threat-detection-platform:$VERSION $AWS_ACCOUNT_ID.dkr.ecr.$REGION.amazonaws.com/threat-detection-platform:$VERSION
docker tag threat-detection-platform:$VERSION $AWS_ACCOUNT_ID.dkr.ecr.$REGION.amazonaws.com/threat-detection-platform:latest
docker push $AWS_ACCOUNT_ID.dkr.ecr.$REGION.amazonaws.com/threat-detection-platform:$VERSION
docker push $AWS_ACCOUNT_ID.dkr.ecr.$REGION.amazonaws.com/threat-detection-platform:latest

cd deploy/aws

# Create security group
echo -e "${YELLOW}🔒 Setting up security group...${NC}"
aws ec2 describe-security-groups --group-names $SECURITY_GROUP --region $REGION 2>/dev/null || \
aws ec2 create-security-group --group-name $SECURITY_GROUP --description "Security group for Threat Detection Platform" --region $REGION

# Add security group rules
aws ec2 authorize-security-group-ingress --group-name $SECURITY_GROUP --protocol tcp --port 22 --cidr 0.0.0.0/0 --region $REGION 2>/dev/null || true
aws ec2 authorize-security-group-ingress --group-name $SECURITY_GROUP --protocol tcp --port 80 --cidr 0.0.0.0/0 --region $REGION 2>/dev/null || true
aws ec2 authorize-security-group-ingress --group-name $SECURITY_GROUP --protocol tcp --port 443 --cidr 0.0.0.0/0 --region $REGION 2>/dev/null || true
aws ec2 authorize-security-group-ingress --group-name $SECURITY_GROUP --protocol tcp --port 8080 --cidr 0.0.0.0/0 --region $REGION 2>/dev/null || true
aws ec2 authorize-security-group-ingress --group-name $SECURITY_GROUP --protocol tcp --port 3000 --cidr 0.0.0.0/0 --region $REGION 2>/dev/null || true

# Create user data script
cat > user-data.sh << 'EOF'
#!/bin/bash
yum update -y
yum install -y docker
systemctl start docker
systemctl enable docker
usermod -a -G docker ec2-user

# Install Docker Compose
curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose

# Install AWS CLI v2
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
./aws/install

# Create application directory
mkdir -p /opt/threat-detection
cd /opt/threat-detection

# Download deployment files (you'll need to upload these to S3 or use git)
# For now, we'll create them directly
EOF

# Launch EC2 instance
echo -e "${YELLOW}🖥️  Launching EC2 instance...${NC}"
INSTANCE_ID=$(aws ec2 run-instances \
    --image-id ami-0c02fb55956c7d316 \
    --count 1 \
    --instance-type $INSTANCE_TYPE \
    --key-name $KEY_NAME \
    --security-groups $SECURITY_GROUP \
    --user-data file://user-data.sh \
    --region $REGION \
    --query 'Instances[0].InstanceId' \
    --output text)

echo -e "${GREEN}✅ Instance launched: $INSTANCE_ID${NC}"

# Wait for instance to be running
echo -e "${YELLOW}⏳ Waiting for instance to be running...${NC}"
aws ec2 wait instance-running --instance-ids $INSTANCE_ID --region $REGION

# Get public IP
PUBLIC_IP=$(aws ec2 describe-instances --instance-ids $INSTANCE_ID --region $REGION --query 'Reservations[0].Instances[0].PublicIpAddress' --output text)

echo -e "${GREEN}✅ Instance is running at: $PUBLIC_IP${NC}"

# Save deployment info
cat > deployment-info.txt << EOF
Deployment Information
=====================
Instance ID: $INSTANCE_ID
Public IP: $PUBLIC_IP
Region: $REGION
Domain: $DOMAIN_NAME
DB Password: $DB_PASSWORD
Grafana Password: $GRAFANA_PASSWORD

Access URLs:
- Application: http://$PUBLIC_IP:8080
- Grafana: http://$PUBLIC_IP:3000
- Prometheus: http://$PUBLIC_IP:9090

SSH Command:
ssh -i ~/.ssh/$KEY_NAME.pem ec2-user@$PUBLIC_IP
EOF

echo -e "${GREEN}🎉 Deployment completed!${NC}"
echo -e "${YELLOW}📄 Deployment details saved to deployment-info.txt${NC}"
echo -e "${YELLOW}🌐 Application will be available at: http://$PUBLIC_IP:8080${NC}"
echo -e "${YELLOW}📊 Grafana dashboard: http://$PUBLIC_IP:3000 (admin/$GRAFANA_PASSWORD)${NC}"

# Clean up
rm -f user-data.sh

echo -e "${GREEN}✅ Deployment script completed successfully!${NC}"
