#!/bin/bash

# Script to deploy Firestore indexes
# Run this script from project root directory

echo "=========================================="
echo "Creating Firestore Indexes for Comments"
echo "=========================================="
echo ""

# Check if firebase CLI is installed
if ! command -v firebase &> /dev/null
then
    echo "❌ Firebase CLI not found!"
    echo ""
    echo "Please install Firebase CLI first:"
    echo "npm install -g firebase-tools"
    echo ""
    echo "Then run: firebase login"
    exit
fi

# Deploy indexes
echo "Deploying Firestore indexes..."
firebase deploy --only firestore:indexes

echo ""
echo "✅ Done! Wait 2-5 minutes for indexes to build."
echo ""
echo "Check status at:"
echo "https://console.firebase.google.com/project/bem-unsoed-badfc/firestore/indexes"

