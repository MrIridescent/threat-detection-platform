#!/bin/bash

echo "🎨 Deploying Threat Detection Platform to Render"

echo "1. Go to https://render.com"
echo "2. Connect your GitHub account"
echo "3. Click 'New +' -> 'Blueprint'"
echo "4. Connect your repository: MrIridescent/threat-detection-platform"
echo "5. Render will automatically detect render.yaml and deploy!"

echo "📋 Manual Setup (if needed):"
echo "   - Service Type: Web Service"
echo "   - Build Command: mvn clean package -DskipTests"
echo "   - Start Command: java -Dserver.port=\$PORT -Dspring.profiles.active=render -jar target/threat-detection-platform-1.0.0.jar"
echo "   - Add PostgreSQL database"
echo "   - Add Redis instance"

echo "🌐 Your app will be available at: https://your-app.onrender.com"
