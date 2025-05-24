#!/usr/bin/env python3
import http.server
import socketserver
import os

# Set the port
PORT = 12000

# Change to the directory containing the APK
os.chdir('/workspace/apk')

# Create a simple HTTP server
Handler = http.server.SimpleHTTPRequestHandler
httpd = socketserver.TCPServer(("0.0.0.0", PORT), Handler)

print(f"Serving APK at port {PORT}")
print(f"Access the download page at: https://work-1-bvemjqssescmnplf.prod-runtime.all-hands.dev")
httpd.serve_forever()