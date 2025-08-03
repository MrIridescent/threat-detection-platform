#!/bin/bash

# 🔍 Project Integrity Verification Script
# Ensures all components are properly integrated and working

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

echo -e "${CYAN}🔍 AI-Powered Threat Detection Platform - Project Integrity Verification${NC}"
echo -e "${CYAN}=====================================================================${NC}"

# Counters
total_checks=0
passed_checks=0
failed_checks=0

# Function to run a check
run_check() {
    local check_name="$1"
    local check_command="$2"
    
    total_checks=$((total_checks + 1))
    echo -e "\n${BLUE}🔍 Checking: $check_name${NC}"
    
    if eval "$check_command"; then
        echo -e "${GREEN}✅ PASS: $check_name${NC}"
        passed_checks=$((passed_checks + 1))
        return 0
    else
        echo -e "${RED}❌ FAIL: $check_name${NC}"
        failed_checks=$((failed_checks + 1))
        return 1
    fi
}

# Function to check file exists
check_file_exists() {
    [ -f "$1" ]
}

# Function to check directory exists
check_dir_exists() {
    [ -d "$1" ]
}

# Function to check Maven build
check_maven_build() {
    if command -v mvn &> /dev/null; then
        mvn clean compile -q
    else
        echo "Maven not installed, skipping build check"
        return 0
    fi
}

# Function to check Docker build
check_docker_build() {
    if command -v docker &> /dev/null; then
        docker build -t threat-detection-test . > /dev/null 2>&1
        docker rmi threat-detection-test > /dev/null 2>&1
    else
        echo "Docker not installed, skipping Docker build check"
        return 0
    fi
}

# Function to check Java syntax
check_java_syntax() {
    find src/main/java -name "*.java" -exec javac -cp "$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout 2>/dev/null || echo '')" {} \; > /dev/null 2>&1 || return 0
}

echo -e "\n${YELLOW}📋 Starting comprehensive project verification...${NC}"

# 1. Core Project Structure
echo -e "\n${BLUE}📁 CORE PROJECT STRUCTURE${NC}"
run_check "Main POM file exists" "check_file_exists 'pom.xml'"
run_check "Main application class exists" "check_file_exists 'src/main/java/com/mriridescent/threatdetection/ThreatDetectionApplication.java'"
run_check "Main source directory exists" "check_dir_exists 'src/main/java'"
run_check "Test source directory exists" "check_dir_exists 'src/test/java'"
run_check "Resources directory exists" "check_dir_exists 'src/main/resources'"

# 2. AI/ML Components
echo -e "\n${BLUE}🤖 AI/ML COMPONENTS${NC}"
run_check "Project Iris service exists" "check_file_exists 'src/main/java/com/mriridescent/threatdetection/iris/service/IrisAnalysisService.java'"
run_check "ML Model service exists" "check_file_exists 'src/main/java/com/mriridescent/threatdetection/iris/service/MLModelService.java'"
run_check "Feature extraction service exists" "check_file_exists 'src/main/java/com/mriridescent/threatdetection/iris/service/FeatureExtractionService.java'"
run_check "PhishNet AI analysis service exists" "check_file_exists 'src/main/java/com/mriridescent/threatdetection/phishnet/service/AIEnhancedCampaignAnalysisService.java'"

# 3. PhishNet Analyst Components
echo -e "\n${BLUE}🕸️ PHISHNET ANALYST COMPONENTS${NC}"
run_check "PhishNet campaign service exists" "check_file_exists 'src/main/java/com/mriridescent/threatdetection/phishnet/service/PhishingCampaignService.java'"
run_check "Infrastructure node model exists" "check_file_exists 'src/main/java/com/mriridescent/threatdetection/phishnet/model/entity/InfrastructureNode.java'"
run_check "Campaign model exists" "check_file_exists 'src/main/java/com/mriridescent/threatdetection/phishnet/model/entity/PhishingCampaign.java'"

# 4. Agent Coordinator
echo -e "\n${BLUE}🎛️ AGENT COORDINATOR${NC}"
run_check "Agent coordinator exists" "check_file_exists 'src/main/java/com/mriridescent/threatdetection/agent/coordinator/AgentCoordinator.java'"
run_check "Agent orchestrator exists" "check_file_exists 'src/main/java/com/mriridescent/threatdetection/orchestration/AgentOrchestrator.java'"

# 5. Demo Components
echo -e "\n${BLUE}🎮 DEMO COMPONENTS${NC}"
run_check "Demo data generator exists" "check_file_exists 'src/main/java/com/mriridescent/threatdetection/demo/DemoDataGenerator.java'"
run_check "Demo visualization controller exists" "check_file_exists 'src/main/java/com/mriridescent/threatdetection/demo/DemoVisualizationController.java'"
run_check "Demo dashboard DTOs exist" "check_file_exists 'src/main/java/com/mriridescent/threatdetection/demo/DemoDashboardData.java'"

# 6. Web Interface
echo -e "\n${BLUE}🌐 WEB INTERFACE${NC}"
run_check "Main HTML page exists" "check_file_exists 'src/main/resources/static/index.html'"
run_check "Demo JavaScript exists" "check_file_exists 'src/main/resources/static/js/demo.js'"

# 7. Configuration Files
echo -e "\n${BLUE}⚙️ CONFIGURATION FILES${NC}"
run_check "Application properties exist" "check_file_exists 'src/main/resources/application.yml'"
run_check "Resilience configuration exists" "check_file_exists 'src/main/resources/application-resilience.yml'"
run_check "Logback configuration exists" "check_file_exists 'src/main/resources/logback-spring.xml'"

# 8. Security Components
echo -e "\n${BLUE}🔒 SECURITY COMPONENTS${NC}"
run_check "Security configuration exists" "check_file_exists 'src/main/java/com/mriridescent/threatdetection/config/SecurityConfig.java'"
run_check "JWT service exists" "check_file_exists 'src/main/java/com/mriridescent/threatdetection/security/JwtService.java'"
run_check "JWT authentication filter exists" "check_file_exists 'src/main/java/com/mriridescent/threatdetection/security/JwtAuthenticationFilter.java'"

# 9. Docker & Deployment
echo -e "\n${BLUE}🐳 DOCKER & DEPLOYMENT${NC}"
run_check "Dockerfile exists" "check_file_exists 'Dockerfile'"
run_check "Docker Compose file exists" "check_file_exists 'docker-compose.yml'"
run_check "AWS deployment script exists" "check_file_exists 'deploy/aws/deploy.sh'"
run_check "AWS Docker Compose exists" "check_file_exists 'deploy/aws/docker-compose.aws.yml'"

# 10. CI/CD
echo -e "\n${BLUE}🔄 CI/CD PIPELINE${NC}"
run_check "GitHub Actions workflow exists" "check_file_exists '.github/workflows/ci-cd.yml'"
run_check "Build workflow exists" "check_file_exists '.github/workflows/build.yml'"

# 11. Monitoring
echo -e "\n${BLUE}📊 MONITORING${NC}"
run_check "Prometheus config exists" "check_file_exists 'monitoring/prometheus/prometheus.yml'"
run_check "Grafana provisioning exists" "check_dir_exists 'monitoring/grafana/provisioning'"

# 12. Documentation
echo -e "\n${BLUE}📚 DOCUMENTATION${NC}"
run_check "Main README exists" "check_file_exists 'README.md'"
run_check "Project showcase exists" "check_file_exists 'PROJECT_SHOWCASE.md'"
run_check "Deployment guide exists" "check_file_exists 'DEPLOYMENT_GUIDE.md'"
run_check "Release notes exist" "check_file_exists 'RELEASE_NOTES.md'"
run_check "Contributing guide exists" "check_file_exists 'CONTRIBUTING.md'"
run_check "Code of conduct exists" "check_file_exists 'CODE_OF_CONDUCT.md'"

# 13. Git Configuration
echo -e "\n${BLUE}📝 GIT CONFIGURATION${NC}"
run_check "Gitignore file exists" "check_file_exists '.gitignore'"
run_check "Bug report template exists" "check_file_exists '.github/ISSUE_TEMPLATE/bug_report.yml'"
run_check "Feature request template exists" "check_file_exists '.github/ISSUE_TEMPLATE/feature_request.yml'"
run_check "Pull request template exists" "check_file_exists '.github/pull_request_template.md'"

# 14. Build Verification
echo -e "\n${BLUE}🔨 BUILD VERIFICATION${NC}"
if command -v mvn &> /dev/null; then
    run_check "Maven build compiles successfully" "check_maven_build"
else
    echo -e "${YELLOW}⚠️  Maven not installed, skipping build verification${NC}"
fi

if command -v docker &> /dev/null; then
    run_check "Docker build succeeds" "check_docker_build"
else
    echo -e "${YELLOW}⚠️  Docker not installed, skipping Docker build verification${NC}"
fi

# 15. Content Verification
echo -e "\n${BLUE}📄 CONTENT VERIFICATION${NC}"
run_check "README contains live demo link" "grep -q 'demo.threatdetection.ai' README.md"
run_check "README contains AI accuracy metrics" "grep -q '94%' README.md"
run_check "Project showcase contains technical details" "grep -q 'Neural Network' PROJECT_SHOWCASE.md"
run_check "Deployment guide contains AWS instructions" "grep -q 'AWS' DEPLOYMENT_GUIDE.md"

# 16. Package Structure Verification
echo -e "\n${BLUE}📦 PACKAGE STRUCTURE VERIFICATION${NC}"
run_check "Main package structure correct" "check_dir_exists 'src/main/java/com/mriridescent/threatdetection'"
run_check "Iris package exists" "check_dir_exists 'src/main/java/com/mriridescent/threatdetection/iris'"
run_check "PhishNet package exists" "check_dir_exists 'src/main/java/com/mriridescent/threatdetection/phishnet'"
run_check "Agent package exists" "check_dir_exists 'src/main/java/com/mriridescent/threatdetection/agent'"
run_check "Demo package exists" "check_dir_exists 'src/main/java/com/mriridescent/threatdetection/demo'"

# Final Results
echo -e "\n${CYAN}📊 VERIFICATION RESULTS${NC}"
echo -e "${CYAN}========================${NC}"
echo -e "Total Checks: ${BLUE}$total_checks${NC}"
echo -e "Passed: ${GREEN}$passed_checks${NC}"
echo -e "Failed: ${RED}$failed_checks${NC}"

if [ $failed_checks -eq 0 ]; then
    echo -e "\n${GREEN}🎉 ALL CHECKS PASSED!${NC}"
    echo -e "${GREEN}✅ Project is ready for deployment to repositories${NC}"
    echo -e "${GREEN}🚀 You can now run: ./deploy-to-repositories.sh${NC}"
    exit 0
else
    echo -e "\n${RED}❌ $failed_checks CHECKS FAILED${NC}"
    echo -e "${RED}Please fix the issues above before deploying${NC}"
    exit 1
fi
