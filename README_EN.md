# Apple App Store Order Query Tool

A Java-based App Store order query tool that uses Apple's official App Store Server Library to query and manage in-app purchase orders, subscription status, transaction history, and more.

## Features

✨ **Core Features**
- 🔍 Query transaction history
- 📦 Query order information
- 💳 Query subscription status
- 💰 Query refund history
- 🔔 Verify server notifications
- 📱 Extract transaction ID from receipt
- 🎁 Create promotional offer signatures
- 🧪 Send test notifications

## Tech Stack

- **Java 11+**
- **Gradle** - Build tool
- **Apple App Store Server Library** - Official library
- **SLF4J + Logback** - Logging framework
- **Jackson** - JSON processing

## Quick Start

### 1. Prerequisites

- Java 11 or higher
- Gradle 7.0+ (or use included gradlew)
- App Store Connect API key

### 2. Get App Store Connect API Key

1. Sign in to [App Store Connect](https://appstoreconnect.apple.com/)
2. Go to **Users and Access > Integrations > In-App Purchase**
3. Create a new key or use an existing one
4. Download the `.p8` private key file
5. Record the following information:
   - Issuer ID
   - Key ID
   - Bundle ID

### 3. Download Apple Root Certificates

Download root certificates from [Apple PKI](https://www.apple.com/certificateauthority/):
- AppleRootCA-G2.cer
- AppleRootCA-G3.cer

### 4. Configure

Copy the example config file and fill in your information:

```bash
cd src/main/resources
touch config.properties
```

Edit `config.properties`:

```properties
# Apple App Store Server API Configuration
appstore.issuer.id=your-issuer-id
appstore.key.id=your-key-id
appstore.bundle.id=com.yourcompany.yourapp
appstore.environment=SANDBOX
# appstore.environment=PRODUCTION

# Private key file path
appstore.private.key.path=/path/to/SubscriptionKey_XXXXX.p8

# Apple root certificate paths (comma-separated)
appstore.root.ca.paths=/path/to/AppleRootCA-G2.cer,/path/to/AppleRootCA-G3.cer

# Required for production environment
# appstore.app.apple.id=1234567890
```

### 5. Build

```bash
# Using Gradle Wrapper
./gradlew build

# Or using Gradle directly
gradle build
```

### 6. Run

```bash
# Using default config file path
./gradlew run

# Or specify config file path
./gradlew run --args="path/to/config.properties"

# Run JAR directly
java -jar build/libs/appleOrderTool-1.0.0.jar
```

## Usage

### Interactive CLI

After running the program, an interactive menu will be displayed:

```
╔═══════════════════════════════════════════╗
║   Apple App Store Order Query Tool        ║
╚═══════════════════════════════════════════╝

=============================================
Menu:
=============================================
1. Send test notification
2. Query transaction history
3. Query order information
4. Query subscription status
5. Query refund history
6. Extract transaction ID from receipt
7. Verify server notification
0. Exit
=============================================
```

## License

MIT License

## Documentation

For more information, see:
- [App Store Server API Documentation](https://developer.apple.com/documentation/appstoreserverapi)
- [App Store Server Notifications V2](https://developer.apple.com/documentation/appstoreservernotifications)

---

**Happy Coding! 🚀**

