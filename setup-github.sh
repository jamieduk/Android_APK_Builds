#!/bin/bash

# FiTrack - Quick Start Script
# This script helps you set up the project for GitHub

set -e

echo "🚀 FiTrack - GitHub Setup Helper"
echo "=================================="
echo ""

# Check if git is installed
if ! command -v git &> /dev/null; then
    echo "❌ Git is not installed. Installing..."
    sudo apt update && sudo apt install -y git
fi

echo "✅ Git found: $(git --version)"
echo ""

# Check if we're in a git repo
if [ ! -d ".git" ]; then
    echo "📦 Initializing Git repository..."
    git init
    git branch -M main
fi

# Configure git user (if not set)
if [ -z "$(git config --global user.name)" ]; then
    echo ""
    echo "👤 Setting up Git user..."
    read -p "Enter your Git username: " git_user
    read -p "Enter your Git email: " git_email
    git config --global user.name "$git_user"
    git config --global user.email "$git_email"
fi

echo ""
echo "📝 Creating initial commit..."
git add .
git commit -m "Initial commit: FiTrack Android App with GitHub Actions" || echo "Nothing to commit (already committed)"

echo ""
echo "🔗 Add your GitHub repository as remote:"
echo ""
echo "   1. Go to https://github.com/new"
echo "   2. Create a new repository named 'fitrack'"
echo "   3. Don't initialize with README"
echo "   4. Copy the repository URL"
echo ""
read -p "Paste your GitHub repository URL: " repo_url

git remote add origin "$repo_url" 2>/dev/null || echo "Remote 'origin' already exists, updating..."
git remote set-url origin "$repo_url"

echo ""
echo "🚀 Pushing to GitHub..."
git push -u origin main

echo ""
echo "✅ Success!"
echo ""
echo "📱 Next steps:"
echo "   1. Go to your repository on GitHub"
echo "   2. Click the 'Actions' tab"
echo "   3. Wait for the build to complete (~5-10 minutes)"
echo "   4. Download the APK from the 'Artifacts' section"
echo ""
echo "📖 For detailed instructions, see GITHUB_ACTIONS_SETUP.md"
echo ""
echo "Happy coding! 💪"
