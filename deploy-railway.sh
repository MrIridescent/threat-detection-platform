#!/bin/bash

echo "🚂 Deploying Threat Detection Platform to Railway"

# Install Railway CLI
npm install -g @railway/cli

# Login to Railway
railway login

# Initialize project
railway init

# Add PostgreSQL database
railway add postgresql

# Add Redis cache
railway add redis

# Set environment variables
railway variables set SPRING_PROFILES_ACTIVE=railway
railway variables set JWT_SECRET=$(openssl rand -base64 32)

# Deploy the application
railway up

echo "✅ Deployment complete!"
echo "🌐 Your app will be available at: https://your-app.railway.app"
echo "📊 Check deployment status: railway status"
echo "📝 View logs: railway logs"
