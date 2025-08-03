# 🚀 Deployment Guide - Threat Detection Platform

This guide provides comprehensive instructions for deploying the AI-powered threat detection platform in various environments.

## 🎯 Quick Demo Deployment

### Option 1: One-Click AWS Deployment (Recommended)

```bash
# Clone the repository
git clone https://github.com/mriridescent/threat-detection-platform.git
cd threat-detection-platform

# Set up AWS credentials
export AWS_ACCESS_KEY_ID="your-access-key"
export AWS_SECRET_ACCESS_KEY="your-secret-key"
export AWS_REGION="us-east-1"

# Deploy to AWS (creates EC2 instance with full stack)
chmod +x deploy/aws/deploy.sh
./deploy/aws/deploy.sh
```

**🌐 Your demo will be available at the provided IP address within 10 minutes!**

### Option 2: Local Docker Deployment

```bash
# Clone and start locally
git clone https://github.com/mriridescent/threat-detection-platform.git
cd threat-detection-platform

# Start all services with demo data
docker-compose up -d

# Wait for services to start (2-3 minutes)
docker-compose logs -f app

# Access the application
open http://localhost:8080
```

## 🏗️ Production Deployment Options

### 🐳 Docker Swarm Deployment

```bash
# Initialize Docker Swarm
docker swarm init

# Deploy the stack
docker stack deploy -c docker-compose.prod.yml threatdetection

# Scale services
docker service scale threatdetection_app=3
docker service scale threatdetection_postgres=2
```

### ☸️ Kubernetes Deployment

```bash
# Apply Kubernetes manifests
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/secrets.yaml
kubectl apply -f k8s/postgres.yaml
kubectl apply -f k8s/redis.yaml
kubectl apply -f k8s/app.yaml
kubectl apply -f k8s/ingress.yaml

# Check deployment status
kubectl get pods -n threatdetection
kubectl get services -n threatdetection
```

### 🌩️ Cloud Provider Specific

#### AWS ECS Deployment
```bash
# Create ECS cluster
aws ecs create-cluster --cluster-name threatdetection-cluster

# Register task definition
aws ecs register-task-definition --cli-input-json file://aws/ecs-task-definition.json

# Create service
aws ecs create-service --cluster threatdetection-cluster \
  --service-name threatdetection-service \
  --task-definition threatdetection:1 \
  --desired-count 2
```

#### Azure Container Instances
```bash
# Create resource group
az group create --name threatdetection-rg --location eastus

# Deploy container group
az container create --resource-group threatdetection-rg \
  --file azure/container-group.yaml
```

#### Google Cloud Run
```bash
# Build and push to Container Registry
gcloud builds submit --tag gcr.io/PROJECT-ID/threatdetection

# Deploy to Cloud Run
gcloud run deploy threatdetection \
  --image gcr.io/PROJECT-ID/threatdetection \
  --platform managed \
  --region us-central1 \
  --allow-unauthenticated
```

## 🔧 Configuration

### Environment Variables

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `SPRING_PROFILES_ACTIVE` | Active Spring profiles | `prod` | Yes |
| `SPRING_DATASOURCE_URL` | PostgreSQL connection URL | - | Yes |
| `SPRING_DATASOURCE_USERNAME` | Database username | `postgres` | Yes |
| `SPRING_DATASOURCE_PASSWORD` | Database password | - | Yes |
| `SPRING_REDIS_HOST` | Redis host | `localhost` | Yes |
| `SPRING_REDIS_PORT` | Redis port | `6379` | Yes |
| `JWT_SECRET` | JWT signing secret | - | Yes |
| `DEMO_MODE` | Enable demo data generation | `false` | No |
| `LOG_LEVEL` | Application log level | `INFO` | No |

### Database Configuration

```yaml
# application-prod.yml
spring:
  datasource:
    url: jdbc:postgresql://postgres:5432/threatdetection
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD}
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
  
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: false
        show_sql: false
```

### Redis Configuration

```yaml
spring:
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
    timeout: 2000ms
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0
```

## 📊 Monitoring & Observability

### Prometheus Metrics

The application exposes metrics at `/actuator/prometheus`:

- **Application Metrics**: Request rates, response times, error rates
- **AI Model Metrics**: Prediction accuracy, model performance, inference times
- **Business Metrics**: Threat detection rates, campaign analysis metrics
- **Infrastructure Metrics**: Database connections, cache hit rates, memory usage

### Grafana Dashboards

Pre-configured dashboards available at `/grafana`:

1. **Application Overview** - High-level system health and performance
2. **AI Model Performance** - ML model accuracy, prediction rates, training metrics
3. **Threat Intelligence** - Security metrics, threat detection rates, campaign analytics
4. **Infrastructure** - System resources, database performance, cache metrics

### Log Aggregation

Structured JSON logging with correlation IDs:

```json
{
  "timestamp": "2025-01-15T10:30:45.123Z",
  "level": "INFO",
  "logger": "com.mriridescent.threatdetection.iris.service.IrisAnalysisService",
  "message": "Email analyzed successfully",
  "correlationId": "abc123-def456-ghi789",
  "userId": "analyst@company.com",
  "threatScore": 0.85,
  "analysisTime": 245
}
```

## 🔒 Security Configuration

### SSL/TLS Setup

```bash
# Generate SSL certificates (for production use proper CA-signed certificates)
openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
  -keyout nginx/ssl/threatdetection.key \
  -out nginx/ssl/threatdetection.crt
```

### JWT Configuration

```yaml
jwt:
  secret: ${JWT_SECRET}
  expiration: 86400000  # 24 hours
  refresh-expiration: 604800000  # 7 days
```

### CORS Configuration

```yaml
cors:
  allowed-origins:
    - "https://demo.threatdetection.ai"
    - "https://threatdetection.company.com"
  allowed-methods:
    - GET
    - POST
    - PUT
    - DELETE
    - OPTIONS
  allowed-headers:
    - "*"
  allow-credentials: true
```

## 🧪 Health Checks & Readiness

### Application Health Endpoints

- **Health Check**: `GET /actuator/health`
- **Readiness**: `GET /actuator/health/readiness`
- **Liveness**: `GET /actuator/health/liveness`
- **Info**: `GET /actuator/info`

### Database Health Check

```sql
-- Custom health check query
SELECT 
  COUNT(*) as active_campaigns,
  COUNT(CASE WHEN status = 'ACTIVE' THEN 1 END) as active_threats,
  AVG(threat_score) as avg_threat_score
FROM phishing_campaigns 
WHERE discovered_at > NOW() - INTERVAL '24 hours';
```

## 🚨 Troubleshooting

### Common Issues

#### Application Won't Start
```bash
# Check logs
docker-compose logs app

# Common causes:
# 1. Database connection issues
# 2. Missing environment variables
# 3. Port conflicts
```

#### High Memory Usage
```bash
# Adjust JVM settings
export JAVA_OPTS="-Xmx2g -Xms1g -XX:+UseG1GC"

# Monitor memory usage
docker stats threatdetection-app
```

#### AI Model Loading Issues
```bash
# Check model files
ls -la /app/models/

# Verify model permissions
chmod 644 /app/models/*.pkl
chmod 644 /app/models/*.h5
```

### Performance Tuning

#### Database Optimization
```sql
-- Create indexes for better performance
CREATE INDEX CONCURRENTLY idx_campaigns_status_discovered 
ON phishing_campaigns(status, discovered_at);

CREATE INDEX CONCURRENTLY idx_nodes_campaign_type 
ON infrastructure_nodes(campaign_id, node_type);

-- Update statistics
ANALYZE phishing_campaigns;
ANALYZE infrastructure_nodes;
```

#### Redis Optimization
```yaml
redis:
  maxmemory: 1gb
  maxmemory-policy: allkeys-lru
  save: "900 1 300 10 60 10000"
```

## 📈 Scaling Guidelines

### Horizontal Scaling

```bash
# Scale application instances
docker-compose up -d --scale app=3

# Scale with load balancer
docker-compose -f docker-compose.prod.yml up -d --scale app=5
```

### Database Scaling

```yaml
# Read replicas configuration
spring:
  datasource:
    primary:
      url: jdbc:postgresql://postgres-primary:5432/threatdetection
    replica:
      url: jdbc:postgresql://postgres-replica:5432/threatdetection
```

### Cache Scaling

```yaml
# Redis Cluster configuration
spring:
  redis:
    cluster:
      nodes:
        - redis-node1:6379
        - redis-node2:6379
        - redis-node3:6379
```

## 🎯 Next Steps

After successful deployment:

1. **Configure Monitoring** - Set up alerts and dashboards
2. **Load Test** - Verify performance under load
3. **Security Scan** - Run security assessments
4. **Backup Strategy** - Implement data backup procedures
5. **Documentation** - Update operational runbooks

For additional support, see the [Operations Guide](OPERATIONS.md) or contact the development team.
