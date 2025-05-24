#!/bin/bash

cd /workspace/localmarket/localmarket-android

# Clean the project
echo "Cleaning the project..."
gradle clean

# Build the release APK
echo "Building release APK..."
gradle assembleRelease

# Check if the build was successful
if [ $? -eq 0 ]; then
    echo "APK build successful!"
    
    # Copy the APK to a more accessible location
    mkdir -p /workspace/apk
    cp app/build/outputs/apk/release/app-release.apk /workspace/apk/localmarket.apk
    
    echo "APK is available at: /workspace/apk/localmarket.apk"
else
    echo "APK build failed!"
    exit 1
fi