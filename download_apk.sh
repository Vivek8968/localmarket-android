#!/bin/bash

# Check if the APK exists
if [ ! -f "/workspace/apk/localmarket.apk" ]; then
    echo "APK not found. Building it first..."
    /workspace/build_apk.sh
    
    # Check if build was successful
    if [ ! -f "/workspace/apk/localmarket.apk" ]; then
        echo "Failed to build APK. Please check the build logs."
        exit 1
    fi
fi

# Create a simple HTML page to download the APK
cat > /workspace/apk/download.html << EOL
<!DOCTYPE html>
<html>
<head>
    <title>Local Market APK Download</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 800px;
            margin: 0 auto;
            padding: 20px;
            text-align: center;
        }
        .download-btn {
            display: inline-block;
            background-color: #4CAF50;
            color: white;
            padding: 15px 25px;
            text-decoration: none;
            font-size: 18px;
            border-radius: 4px;
            margin: 20px 0;
        }
        .note {
            background-color: #f8f9fa;
            border-left: 4px solid #ccc;
            padding: 15px;
            margin: 20px 0;
            text-align: left;
        }
    </style>
</head>
<body>
    <h1>Local Market App</h1>
    <p>A hyperlocal marketplace application that connects local shops with customers in their vicinity.</p>
    
    <a href="localmarket.apk" class="download-btn" download>Download APK</a>
    
    <div class="note">
        <h3>Installation Instructions:</h3>
        <ol>
            <li>Download the APK file to your Android device</li>
            <li>Open the file to start installation</li>
            <li>If prompted, allow installation from unknown sources in your device settings</li>
            <li>Follow the on-screen instructions to complete installation</li>
        </ol>
    </div>
    
    <div class="note">
        <h3>App Features:</h3>
        <h4>Customer Features</h4>
        <ul>
            <li>Browse nearby shops</li>
            <li>View shop details and products</li>
            <li>Add products to cart</li>
            <li>Place orders</li>
            <li>Track order status</li>
            <li>Contact shops via WhatsApp</li>
        </ul>
        
        <h4>Vendor Features</h4>
        <ul>
            <li>Create and manage shop profile</li>
            <li>Add and manage products</li>
            <li>View and manage orders</li>
            <li>Update order status</li>
        </ul>
    </div>
</body>
</html>
EOL

echo "Download page created at /workspace/apk/download.html"
echo "You can access it at: https://work-1-bvemjqssescmnplf.prod-runtime.all-hands.dev/view?path=/workspace/apk/download.html"