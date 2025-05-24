# Local Market App

A hyperlocal marketplace application that connects local shops with customers in their vicinity.

## Components

1. **Android App**: Native Android application built with Kotlin
2. **Backend**: FastAPI microservices
3. **Frontend**: React web application

## Setup Instructions

### Starting the Services

1. Run the start_services.sh script to start both backend and frontend:

```bash
./start_services.sh
```

This will start:
- Backend services:
  - User service (authentication, registration)
  - Seller service (shop management)
  - Customer service (orders, cart)
  - Catalog service (products, categories)
  - Admin service (administration)
- Frontend on port 12001 (https://work-2-bvemjqssescmnplf.prod-runtime.all-hands.dev)

The backend services are accessible through their respective endpoints:
- User service: https://work-1-bvemjqssescmnplf.prod-runtime.all-hands.dev/api/users
- Seller service: https://work-1-bvemjqssescmnplf.prod-runtime.all-hands.dev/api/sellers
- Customer service: https://work-1-bvemjqssescmnplf.prod-runtime.all-hands.dev/api/customers
- Catalog service: https://work-1-bvemjqssescmnplf.prod-runtime.all-hands.dev/api/catalog
- Admin service: https://work-1-bvemjqssescmnplf.prod-runtime.all-hands.dev/api/admin

### Building and Downloading the Android APK

1. Run the build_apk.sh script to build the Android APK:

```bash
./build_apk.sh
```

This will create an APK file at `/workspace/apk/localmarket.apk`.

2. To create a download page for the APK, run:

```bash
./download_apk.sh
```

This will create a download page at:
https://work-1-bvemjqssescmnplf.prod-runtime.all-hands.dev/view?path=/workspace/apk/download.html

3. Alternatively, you can serve the APK using a simple HTTP server:

```bash
python /workspace/serve_apk.py
```

This will start a server at port 12000, and you can access the APK at:
https://work-1-bvemjqssescmnplf.prod-runtime.all-hands.dev

You can access either of these pages from your Android device to download and install the APK directly.

## Features

### Customer Features
- Browse nearby shops
- View shop details and products
- Add products to cart
- Place orders
- Track order status
- Contact shops via WhatsApp

### Vendor Features
- Create and manage shop profile
- Add and manage products
- View and manage orders
- Update order status

## Testing the App

1. Install the APK on your Android device
2. Register as a customer or vendor
3. If registering as a vendor, create a shop and add products
4. If registering as a customer, browse shops and products, and place orders

## Troubleshooting

If you encounter any issues:

1. Check the backend logs for a specific service:
```bash
cd /workspace/localmarket/hyperlocalbymanus
python run_service.py user --log-level debug  # Replace 'user' with the service name
```

2. Check the frontend logs:
```bash
cd /workspace/localmarket/localmarket-frontend
npm run dev
```

3. Rebuild the Android APK:
```bash
./build_apk.sh
```

4. Common issues and solutions:

   a. **API Connection Issues**: Make sure the backend services are running and the Android app is using the correct API URL.
   
   b. **Database Issues**: The backend is configured to use SQLite for simplicity. Check if the database file exists and has the correct permissions.
   
   c. **Google Maps Issues**: Ensure the Google Maps API key is correctly set in the Android app.
   
   d. **Authentication Issues**: If you can't log in, try registering a new account or check the user service logs.
   
   e. **APK Installation Issues**: Make sure your Android device allows installation from unknown sources.

## Fixed Issues

1. Updated API URL configuration in NetworkModule.kt
2. Fixed network security configuration for proper API access
3. Updated Google Maps API key
4. Fixed keystore file location for app signing
5. Modified AndroidManifest.xml to allow cleartext traffic
6. Updated backend .env file to use SQLite
7. Fixed frontend configuration with correct API URL
8. Fixed type mismatch issues in ShopRepository.kt
9. Added missing resources (icons, colors, strings)
10. Fixed adapter position issues
11. Added proper price formatting for different numeric types
12. Fixed shop distance formatting
13. Updated ViewModelModule.kt with proper dependencies