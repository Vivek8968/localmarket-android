#!/usr/bin/env python3
import http.server
import socketserver
import os
from urllib.parse import urlparse, parse_qs

# Set the port to 12000 as specified
PORT = 12000
APK_PATH = "/workspace/apk/localmarket.apk"

class APKHandler(http.server.SimpleHTTPRequestHandler):
    def do_GET(self):
        parsed_path = urlparse(self.path)
        
        # Serve the download page at root
        if parsed_path.path == "/" or parsed_path.path == "":
            self.send_response(200)
            self.send_header('Content-type', 'text/html')
            self.end_headers()
            
            html_content = f"""
            <!DOCTYPE html>
            <html>
            <head>
                <title>LocalMarket APK Download</title>
                <meta name="viewport" content="width=device-width, initial-scale=1">
                <style>
                    body {{
                        font-family: Arial, sans-serif;
                        max-width: 800px;
                        margin: 0 auto;
                        padding: 20px;
                        line-height: 1.6;
                    }}
                    .download-btn {{
                        display: inline-block;
                        background-color: #4CAF50;
                        color: white;
                        padding: 12px 24px;
                        text-align: center;
                        text-decoration: none;
                        font-size: 18px;
                        margin: 20px 0;
                        border-radius: 4px;
                        cursor: pointer;
                    }}
                    h1 {{
                        color: #333;
                    }}
                    .info {{
                        background-color: #f8f9fa;
                        border-left: 4px solid #4CAF50;
                        padding: 12px;
                        margin: 20px 0;
                    }}
                </style>
            </head>
            <body>
                <h1>LocalMarket Android App</h1>
                <div class="info">
                    <p><strong>File Size:</strong> {os.path.getsize(APK_PATH) / (1024*1024):.2f} MB</p>
                    <p><strong>Version:</strong> 1.0.0</p>
                </div>
                <p>This is the LocalMarket Android application that connects local shops with customers.</p>
                <a href="/download" class="download-btn">Download APK</a>
                
                <h2>Installation Instructions</h2>
                <ol>
                    <li>Download the APK file by clicking the button above</li>
                    <li>On your Android device, go to Settings > Security</li>
                    <li>Enable "Unknown Sources" to allow installation of apps from sources other than the Play Store</li>
                    <li>Open the downloaded APK file to install</li>
                    <li>Follow the on-screen instructions to complete installation</li>
                </ol>
                
                <h2>Features</h2>
                <ul>
                    <li>Browse local shops and products</li>
                    <li>Search for specific items</li>
                    <li>View shop details and product information</li>
                    <li>Place orders directly through the app</li>
                    <li>Track order status</li>
                </ul>
            </body>
            </html>
            """
            
            self.wfile.write(html_content.encode())
            return
            
        # Handle the download request
        elif parsed_path.path == "/download":
            if not os.path.exists(APK_PATH):
                self.send_error(404, "APK file not found")
                return
                
            # Serve the APK file
            self.send_response(200)
            self.send_header('Content-type', 'application/vnd.android.package-archive')
            self.send_header('Content-Disposition', 'attachment; filename="localmarket.apk"')
            self.send_header('Content-Length', str(os.path.getsize(APK_PATH)))
            self.end_headers()
            
            with open(APK_PATH, 'rb') as apk_file:
                self.wfile.write(apk_file.read())
            return
        
        # For any other path, return 404
        else:
            self.send_error(404, "File not found")
            return

# Set up the server to listen on all interfaces
Handler = APKHandler
httpd = socketserver.TCPServer(("0.0.0.0", PORT), Handler)

print(f"Serving APK at http://0.0.0.0:{PORT}/")
print(f"Download directly from http://0.0.0.0:{PORT}/download")
httpd.serve_forever()