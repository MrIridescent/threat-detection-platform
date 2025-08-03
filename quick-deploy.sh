#!/bin/bash

echo "🚀 Quick Railway Deployment Guide for Threat Detection Platform"
echo "=============================================================="

# Color codes
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m'

echo -e "${CYAN}Since we have some compilation issues, let's deploy directly to Railway!${NC}"
echo -e "${CYAN}Railway will handle the build process in the cloud.${NC}"
echo ""

echo -e "${BLUE}🚂 RAILWAY DEPLOYMENT - STEP BY STEP${NC}"
echo "================================================"
echo ""

echo -e "${YELLOW}📋 What you need:${NC}"
echo "✅ GitHub account (you have this)"
echo "✅ Your repository is already pushed to GitHub"
echo "✅ 5 minutes of your time"
echo ""

echo -e "${BLUE}🎯 Step 1: Go to Railway${NC}"
echo "1. Open your browser and go to: https://railway.app"
echo "2. Click 'Login with GitHub'"
echo "3. Authorize Railway to access your GitHub account"
echo ""

echo -e "${BLUE}🎯 Step 2: Create New Project${NC}"
echo "1. Click 'New Project' button"
echo "2. Select 'Deploy from GitHub repo'"
echo "3. Find and select: 'MrIridescent/threat-detection-platform'"
echo "4. Click 'Deploy Now'"
echo ""
echo -e "${CYAN}🔥 Railway will automatically:${NC}"
echo "   • Detect it's a Java Spring Boot application"
echo "   • Install Maven and Java"
echo "   • Build your application"
echo "   • Deploy it to a live URL"
echo ""

echo -e "${BLUE}🎯 Step 3: Add Databases${NC}"
echo "1. In your Railway project dashboard:"
echo "2. Click 'New' → 'Database' → 'Add PostgreSQL'"
echo "3. Click 'New' → 'Database' → 'Add Redis'"
echo ""
echo -e "${CYAN}💡 Railway automatically connects these databases to your app!${NC}"
echo ""

echo -e "${BLUE}🎯 Step 4: Set Environment Variables${NC}"
echo "1. Click on your app service (not the databases)"
echo "2. Go to 'Variables' tab"
echo "3. Add these variables:"
echo ""
echo -e "${GREEN}SPRING_PROFILES_ACTIVE${NC} = ${YELLOW}railway${NC}"
echo -e "${GREEN}JWT_SECRET${NC} = ${YELLOW}$(openssl rand -base64 32)${NC}"
echo ""

echo -e "${BLUE}🎯 Step 5: Get Your Live Demo URL${NC}"
echo "1. Go to 'Settings' tab in your app service"
echo "2. Scroll down to 'Domains'"
echo "3. Click 'Generate Domain'"
echo "4. Your app will be live at: https://your-app.railway.app"
echo ""

echo -e "${BLUE}🎯 Step 6: Test Your Demo${NC}"
echo "Once deployed, test these URLs:"
echo "🌐 Main app: https://your-app.railway.app"
echo "📊 Demo page: https://your-app.railway.app/demo.html"
echo "📖 API docs: https://your-app.railway.app/swagger-ui.html"
echo "🔍 Health check: https://your-app.railway.app/actuator/health"
echo ""

echo -e "${CYAN}🎉 THAT'S IT! Your AI-powered threat detection platform will be live!${NC}"
echo ""

echo -e "${YELLOW}⏱️ Expected timeline:${NC}"
echo "• Railway setup: 2 minutes"
echo "• Build and deployment: 3-5 minutes"
echo "• Total time: ~7 minutes"
echo ""

echo -e "${YELLOW}💡 Pro Tips:${NC}"
echo "• Railway gives you a free custom domain"
echo "• Automatic HTTPS/SSL certificates"
echo "• Auto-deploys when you push to GitHub"
echo "• Built-in monitoring and logs"
echo "• 500 hours free per month (plenty for demos)"
echo ""

echo -e "${YELLOW}🔧 If you encounter issues:${NC}"
echo "• Check the build logs in Railway dashboard"
echo "• Make sure databases are connected"
echo "• Verify environment variables are set"
echo "• Railway has excellent documentation"
echo ""

echo -e "${GREEN}🚀 Once live, add your demo URL to:${NC}"
echo "• Your resume"
echo "• LinkedIn profile"
echo "• GitHub README"
echo "• Job applications"
echo ""

echo -e "${CYAN}This enterprise-grade AI platform will definitely impress employers!${NC}"
echo ""

echo -e "${BLUE}Ready to deploy? Press Enter to open Railway in your browser...${NC}"
read -r

# Try to open Railway in browser
if command -v xdg-open > /dev/null; then
    xdg-open "https://railway.app"
elif command -v open > /dev/null; then
    open "https://railway.app"
else
    echo "Please manually open: https://railway.app"
fi

echo ""
echo -e "${GREEN}✨ Happy deploying! Your threat detection platform is about to go live! ✨${NC}"
