# Local Market Android App

A professional, scalable Android application for the "Organised Local Market" project. This app connects local shops with customers in their vicinity, allowing users to discover nearby shops, browse products, and contact vendors directly.

## Features

### Customer Features
- **Home Screen**: Lists all shops sorted by real-time distance from the user
- **Shop Details**: View shop information, products, and contact options
- **Product Details**: Detailed product information with specifications
- **Search & Filter**: Find shops and products easily
- **WhatsApp Integration**: Contact shops directly via WhatsApp

### Vendor Features
- **Vendor Dashboard**: Manage shop and products
- **Shop Management**: Create and edit shop details
- **Product Management**: Add, edit, and remove products
- **Catalog Integration**: Add products from a preloaded catalog of electronics items

### Authentication
- Mobile OTP login via Firebase Auth
- Gmail login
- Apple login (future-ready)

## Technical Details

### Architecture
- **Pattern**: MVVM (Model-View-ViewModel)
- **Language**: Kotlin
- **Minimum SDK**: API 21 (Android 5.0)
- **Target SDK**: API 33 (Android 13)

### Libraries & Dependencies
- **UI Components**: AndroidX, Material Design Components
- **Navigation**: Navigation Component with SafeArgs
- **Dependency Injection**: Koin
- **Networking**: Retrofit, OkHttp
- **Image Loading**: Glide
- **Authentication**: Firebase Authentication
- **Location**: Google Play Services Location
- **Async Operations**: Kotlin Coroutines, LiveData

## Setup Instructions

### Prerequisites
- Android Studio Arctic Fox (2021.3.1) or newer
- JDK 11 or newer
- Android SDK with API 33 platform

### Building the Project
1. Clone the repository:
   ```
   git clone https://github.com/yourusername/localmarket-android.git
   ```

2. Open the project in Android Studio

3. Configure Firebase:
   - Create a Firebase project at [Firebase Console](https://console.firebase.google.com/)
   - Add an Android app to your Firebase project
   - Download the `google-services.json` file and place it in the app directory
   - Enable Phone Authentication and Google Sign-In in the Firebase Authentication section

4. Configure Google Maps API:
   - Get a Google Maps API key from [Google Cloud Console](https://console.cloud.google.com/)
   - Add the API key to `local.properties`:
     ```
     MAPS_API_KEY=your_api_key_here
     ```

5. Build the project:
   - Select `Build > Make Project` from the menu
   - Or run `./gradlew assembleDebug` from the command line

### Running the App
- Connect an Android device or use an emulator
- Select `Run > Run 'app'` from the menu
- Or run `./gradlew installDebug` from the command line

## API Integration

The app connects to a FastAPI backend service. The API endpoints are defined in the `ApiService.kt` interface.

Key API features:
- Authentication and user management
- Shop listing and details
- Product catalog and management
- Vendor shop management

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/localmarket/app/
│   │   │   ├── data/
│   │   │   │   ├── api/          # API service interfaces
│   │   │   │   ├── model/        # Data models
│   │   │   │   └── repository/   # Repository classes
│   │   │   ├── di/               # Dependency injection modules
│   │   │   ├── ui/               # UI components
│   │   │   │   ├── auth/         # Authentication screens
│   │   │   │   ├── home/         # Home screen
│   │   │   │   ├── shop/         # Shop details screens
│   │   │   │   └── vendor/       # Vendor management screens
│   │   │   └── utils/            # Utility classes
│   │   └── res/                  # Resources
│   └── test/                     # Unit tests
└── build.gradle                  # App-level build file
```

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- Backend API: [hyperlocalbymanus](https://github.com/Vivek8968/hyperlocalbymanus.git)
- Frontend Web: [localmarket-frontend](https://github.com/Vivek8968/localmarket-frontend.git)