#!/bin/bash

echo "🚀 Threat Detection Platform - Live Demo Deployment Guide"
echo "========================================================="

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

print_step() {
    echo -e "${BLUE}📋 Step $1: $2${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

# Check if required tools are installed
check_requirements() {
    print_step "1" "Checking Requirements"
    
    if ! command -v java &> /dev/null; then
        print_error "Java is not installed. Please install Java 17 or higher."
        exit 1
    fi
    
    if ! command -v mvn &> /dev/null; then
        print_error "Maven is not installed. Please install Maven."
        exit 1
    fi
    
    print_success "Requirements check completed"
}

# Build the application
build_application() {
    print_step "2" "Building Application"
    
    echo "Building with Maven..."
    mvn clean package -DskipTests
    
    if [ $? -eq 0 ]; then
        print_success "Application built successfully"
    else
        print_error "Build failed"
        exit 1
    fi
}

# Choose deployment option
choose_deployment() {
    print_step "3" "Choose Live Demo Deployment Platform"
    
    echo -e "${CYAN}🌐 Available deployment platforms for live demo:${NC}"
    echo ""
    echo "1. 🚂 Railway (RECOMMENDED - Free tier, easy setup)"
    echo "   ✅ Free PostgreSQL + Redis included"
    echo "   ✅ Auto-deployment from GitHub"
    echo "   ✅ Custom domain support"
    echo "   ✅ Zero configuration needed"
    echo ""
    echo "2. 🎨 Render (Great free tier)"
    echo "   ✅ Free PostgreSQL + Redis"
    echo "   ✅ Automatic SSL certificates"
    echo "   ✅ Blueprint deployment"
    echo "   ⚠️  Cold starts on free tier"
    echo ""
    echo "3. 🟣 Heroku (Classic choice)"
    echo "   ✅ Reliable platform"
    echo "   ⚠️  Requires credit card"
    echo "   ⚠️  Limited free tier"
    echo ""
    echo "4. 🌊 DigitalOcean App Platform"
    echo "   ✅ Professional grade"
    echo "   ⚠️  Paid service ($5/month)"
    echo ""
    echo "5. ☁️  AWS/GCP/Azure (Enterprise)"
    echo "   ✅ Production ready"
    echo "   ⚠️  Complex setup, paid"
    echo ""
    echo "6. 🐳 Local Docker (Testing only)"
    echo "   ✅ Free, full control"
    echo "   ❌ Not accessible from internet"
    echo ""
    
    read -p "Enter your choice (1-6): " choice
    
    case $choice in
        1) deploy_railway ;;
        2) deploy_render ;;
        3) deploy_heroku ;;
        4) deploy_digitalocean ;;
        5) deploy_cloud ;;
        6) deploy_docker ;;
        *) print_error "Invalid choice" && exit 1 ;;
    esac
}

# Railway deployment (RECOMMENDED)
deploy_railway() {
    print_step "4" "Deploying to Railway (RECOMMENDED)"
    
    echo -e "${GREEN}🚂 Railway - Perfect for live demos!${NC}"
    echo ""
    echo -e "${CYAN}✅ Why Railway is the best choice:${NC}"
    echo "   • 🆓 Generous free tier (500 hours/month)"
    echo "   • 🗄️ Free PostgreSQL + Redis databases"
    echo "   • 🚀 Zero-config deployment from GitHub"
    echo "   • 🌐 Custom domain support"
    echo "   • 📊 Built-in monitoring and logs"
    echo "   • ⚡ Fast deployment (2-3 minutes)"
    echo ""
    echo -e "${BLUE}📋 Step-by-step deployment:${NC}"
    echo ""
    echo "1. 🌐 Go to https://railway.app"
    echo "2. 🔐 Click 'Login with GitHub'"
    echo "3. ➕ Click 'New Project'"
    echo "4. 📂 Select 'Deploy from GitHub repo'"
    echo "5. 🔍 Choose: MrIridescent/threat-detection-platform"
    echo "6. 🚀 Railway auto-detects Java and starts building!"
    echo ""
    echo -e "${YELLOW}🗄️ Add databases (IMPORTANT):${NC}"
    echo "7. ➕ In your project, click 'New' → 'Database' → 'Add PostgreSQL'"
    echo "8. ➕ Click 'New' → 'Database' → 'Add Redis'"
    echo ""
    echo -e "${YELLOW}⚙️ Set environment variables:${NC}"
    echo "9. 🔧 Go to your app service → 'Variables' tab"
    echo "10. ➕ Add these variables:"
    echo "    SPRING_PROFILES_ACTIVE=railway"
    echo "    JWT_SECRET=$(openssl rand -base64 32)"
    echo ""
    echo -e "${GREEN}🌐 Your live demo will be available at:${NC}"
    echo "   Main app: https://your-app.railway.app"
    echo "   Demo page: https://your-app.railway.app/demo.html"
    echo "   API docs: https://your-app.railway.app/swagger-ui.html"
    echo "   Health check: https://your-app.railway.app/actuator/health"
    echo ""
    echo -e "${CYAN}💡 Pro tips:${NC}"
    echo "   • Railway provides a custom domain for free"
    echo "   • Check deployment logs in Railway dashboard"
    echo "   • Database URLs are automatically injected"
    echo "   • Redeploy automatically on GitHub pushes"
    
    print_success "Railway deployment guide provided - This is the easiest option!"
}

# Render deployment
deploy_render() {
    print_step "4" "Deploying to Render"
    
    echo -e "${GREEN}🎨 Render - Great free tier alternative!${NC}"
    echo ""
    echo -e "${CYAN}✅ Render advantages:${NC}"
    echo "   • 🆓 Generous free tier"
    echo "   • 🔒 Automatic SSL certificates"
    echo "   • 🗄️ Free PostgreSQL and Redis"
    echo "   • 📄 Blueprint deployment (render.yaml)"
    echo "   • 🔄 Auto-deploy from GitHub"
    echo ""
    echo -e "${YELLOW}⚠️ Considerations:${NC}"
    echo "   • Cold starts on free tier (30s delay after inactivity)"
    echo "   • Limited to 750 hours/month on free tier"
    echo ""
    echo -e "${BLUE}📋 Deployment steps:${NC}"
    echo "1. 🌐 Go to https://render.com"
    echo "2. 🔐 Sign up/Login with GitHub"
    echo "3. ➕ Click 'New +' → 'Blueprint'"
    echo "4. 📂 Connect repository: MrIridescent/threat-detection-platform"
    echo "5. 🚀 Render uses render.yaml for automatic setup!"
    echo ""
    echo -e "${YELLOW}🔧 Manual setup (if Blueprint doesn't work):${NC}"
    echo "   • Service Type: Web Service"
    echo "   • Build Command: mvn clean package -DskipTests"
    echo "   • Start Command: java -Dserver.port=\$PORT -Dspring.profiles.active=render -jar target/threat-detection-platform-1.0.0.jar"
    echo "   • Add PostgreSQL and Redis services"
    echo ""
    echo -e "${GREEN}🌐 Your app will be at: https://your-app.onrender.com${NC}"
    
    print_success "Render deployment guide provided"
}

# Heroku deployment
deploy_heroku() {
    print_step "4" "Deploying to Heroku"
    
    echo -e "${GREEN}🟣 Heroku - The classic platform${NC}"
    echo ""
    echo -e "${YELLOW}⚠️ Note: Heroku requires a credit card even for free tier${NC}"
    echo ""
    
    if ! command -v heroku &> /dev/null; then
        print_warning "Heroku CLI not installed."
        echo "Install from: https://devcenter.heroku.com/articles/heroku-cli"
        echo ""
        echo "After installation, run this script again."
        return
    fi
    
    echo -e "${BLUE}📋 Heroku deployment:${NC}"
    echo "1. heroku login"
    echo "2. heroku create threat-detection-platform-demo"
    echo "3. heroku addons:create heroku-postgresql:hobby-dev"
    echo "4. heroku addons:create heroku-redis:hobby-dev"
    echo "5. heroku config:set SPRING_PROFILES_ACTIVE=heroku"
    echo "6. heroku config:set JWT_SECRET=\$(openssl rand -base64 32)"
    echo "7. git push heroku main"
    echo ""
    echo -e "${GREEN}🌐 Your app: https://threat-detection-platform-demo.herokuapp.com${NC}"
    
    print_success "Heroku deployment guide provided"
}

# DigitalOcean deployment
deploy_digitalocean() {
    print_step "4" "Deploying to DigitalOcean App Platform"
    
    echo -e "${GREEN}🌊 DigitalOcean - Professional grade platform${NC}"
    echo ""
    echo -e "${CYAN}✅ DigitalOcean advantages:${NC}"
    echo "   • 🏢 Professional grade infrastructure"
    echo "   • 🔒 Excellent security"
    echo "   • 📊 Great monitoring"
    echo "   • 💰 Predictable pricing (\$5/month)"
    echo ""
    echo -e "${BLUE}📋 Deployment steps:${NC}"
    echo "1. 🌐 Go to https://cloud.digitalocean.com/apps"
    echo "2. ➕ Click 'Create App'"
    echo "3. 📂 Connect GitHub and select your repository"
    echo "4. 🔧 DigitalOcean detects .do/app.yaml configuration"
    echo "5. 💳 Add payment method (required)"
    echo "6. 🚀 Review and deploy!"
    echo ""
    echo -e "${GREEN}🌐 Your app: https://your-app.ondigitalocean.app${NC}"
    
    print_success "DigitalOcean deployment guide provided"
}

# Cloud deployment (AWS/GCP/Azure)
deploy_cloud() {
    print_step "4" "Enterprise Cloud Deployment"
    
    echo -e "${GREEN}☁️ Enterprise Cloud Platforms${NC}"
    echo ""
    echo -e "${CYAN}🏢 For production enterprise deployment:${NC}"
    echo ""
    echo -e "${BLUE}🔷 AWS Deployment:${NC}"
    echo "   • Use AWS App Runner or Elastic Beanstalk"
    echo "   • RDS for PostgreSQL"
    echo "   • ElastiCache for Redis"
    echo "   • CloudWatch for monitoring"
    echo ""
    echo -e "${BLUE}🔷 Google Cloud Platform:${NC}"
    echo "   • Use Cloud Run or App Engine"
    echo "   • Cloud SQL for PostgreSQL"
    echo "   • Memorystore for Redis"
    echo "   • Cloud Monitoring"
    echo ""
    echo -e "${BLUE}🔷 Microsoft Azure:${NC}"
    echo "   • Use Container Instances or App Service"
    echo "   • Azure Database for PostgreSQL"
    echo "   • Azure Cache for Redis"
    echo "   • Azure Monitor"
    echo ""
    echo -e "${YELLOW}💡 Recommendation for demo:${NC}"
    echo "   Use Railway or Render for quick demo deployment"
    echo "   Use cloud platforms for production enterprise deployment"
    
    print_success "Enterprise cloud deployment options provided"
}

# Docker deployment
deploy_docker() {
    print_step "4" "Local Docker Deployment"
    
    echo -e "${GREEN}🐳 Docker - Local testing and development${NC}"
    echo ""
    echo -e "${YELLOW}⚠️ Note: This is for local testing only, not accessible from internet${NC}"
    echo ""
    
    if ! command -v docker &> /dev/null; then
        print_error "Docker is not installed. Please install Docker first."
        return
    fi
    
    echo "🐳 Starting Docker deployment..."
    
    # Build Docker image
    echo "Building Docker image..."
    docker build -t threat-detection-platform .
    
    # Run with docker-compose
    echo "Starting services with docker-compose..."
    docker-compose up -d
    
    echo "Waiting for services to start..."
    sleep 30
    
    echo ""
    echo -e "${GREEN}🌐 Local application URLs:${NC}"
    echo "   Main app: http://localhost:8080"
    echo "   Demo page: http://localhost:8080/demo.html"
    echo "   API docs: http://localhost:8080/swagger-ui.html"
    echo "   Health check: http://localhost:8080/actuator/health"
    echo "   Grafana: http://localhost:3000 (admin/admin)"
    echo "   Prometheus: http://localhost:9090"
    echo ""
    echo -e "${CYAN}💡 To stop: docker-compose down${NC}"
    
    print_success "Docker deployment completed"
}

# Post-deployment verification
verify_deployment() {
    print_step "5" "Post-Deployment Verification"
    
    echo -e "${BLUE}🔍 Verification checklist:${NC}"
    echo "□ Application starts successfully"
    echo "□ Health endpoint responds: /actuator/health"
    echo "□ Demo page loads: /demo.html"
    echo "□ API documentation accessible: /swagger-ui.html"
    echo "□ Database connection working"
    echo "□ Redis cache working"
    echo "□ AI agents initialized"
    echo ""
    echo -e "${CYAN}🧪 Test your demo:${NC}"
    echo "1. Open the demo page in your browser"
    echo "2. Test the 'Check System Health' button"
    echo "3. Try the API explorer with sample data"
    echo "4. Check the monitoring endpoints"
    echo "5. Test the demo scenarios"
    
    print_success "Verification checklist provided"
}

# Main execution
main() {
    echo -e "${CYAN}🛡️ Welcome to Threat Detection Platform Live Demo Deployment!${NC}"
    echo ""
    echo -e "${YELLOW}This script will help you deploy your AI-powered threat detection platform${NC}"
    echo -e "${YELLOW}as a live demo that you can share with employers and showcase your skills.${NC}"
    echo ""
    
    check_requirements
    build_application
    choose_deployment
    verify_deployment
    
    echo ""
    echo -e "${GREEN}🎉 Deployment process completed!${NC}"
    echo ""
    echo -e "${CYAN}📚 Next steps after deployment:${NC}"
    echo "1. 🧪 Test your live demo thoroughly"
    echo "2. 📝 Update your resume with the live demo URL"
    echo "3. 💼 Add to your LinkedIn profile"
    echo "4. 📊 Share on GitHub README"
    echo "5. 🎯 Use in job applications and interviews"
    echo ""
    echo -e "${YELLOW}🆘 Need help?${NC}"
    echo "   • Check the deployment platform's documentation"
    echo "   • Review application logs for errors"
    echo "   • Test locally with Docker first"
    echo "   • Create an issue on GitHub if needed"
    echo ""
    echo -e "${GREEN}🚀 Your AI-powered threat detection platform is ready to impress!${NC}"
}

# Run main function
main
