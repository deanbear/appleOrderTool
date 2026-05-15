# Apple App Store 订单查询工具

一个基于 Java 的 App Store 订单查询工具，使用 Apple 官方的 App Store Server Library 来查询和管理应用内购买订单、订阅状态、交易历史等信息。

## 功能特性

✨ **核心功能**
- 🔍 查询交易历史
- 📦 查询订单信息
- 💳 查询订阅状态
- 💰 查询退款历史
- 🔔 验证服务器通知
- 📱 从收据提取交易ID
- 🎁 创建促销优惠签名
- 🧪 发送测试通知

## 技术栈

- **Java 11+**
- **Gradle** - 构建工具
- **Apple App Store Server Library** - 官方库
- **SLF4J + Logback** - 日志框架
- **Jackson** - JSON 处理

## 项目结构

```
appleOrderTool/
├── build.gradle                    # Gradle 构建配置
├── settings.gradle                 # Gradle 设置
├── gradle.properties               # Gradle 属性
├── .gitignore                      # Git 忽略文件
├── README.md                       # 项目文档
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/apple/order/tool/
    │   │       ├── AppStoreOrderTool.java           # 主程序入口
    │   │       ├── config/
    │   │       │   └── AppStoreConfig.java          # 配置管理类
    │   │       ├── service/
    │   │       │   └── AppStoreService.java         # 核心服务类
    │   │       └── util/
    │   │           └── PromotionalOfferUtil.java    # 促销优惠工具类
    │   └── resources/
    │       ├── config.properties.example            # 配置文件示例
    │       └── logback.xml                          # 日志配置
    └── test/
        └── java/
            └── com/apple/order/tool/                # 测试代码
```

## 快速开始

### 1. 前置要求

- Java 11 或更高版本
- Gradle 7.0+ (或使用项目自带的 gradlew)
- App Store Connect API 密钥

### 2. 获取 App Store Connect API 密钥

1. 登录 [App Store Connect](https://appstoreconnect.apple.com/)
2. 进入 **用户和访问 > 集成 > App 内购买**
3. 创建新密钥或使用现有密钥
4. 下载 `.p8` 私钥文件
5. 记录以下信息：
   - Issuer ID (颁发者ID)
   - Key ID (密钥ID)
   - Bundle ID (应用包ID)

### 3. 下载 Apple 根证书

从 [Apple PKI](https://www.apple.com/certificateauthority/) 下载根证书：
- AppleRootCA-G2.cer
- AppleRootCA-G3.cer

### 4. 配置项目

复制配置文件示例并填入真实信息：

```bash
cp src/main/resources/config.properties.example src/main/resources/config.properties
```

编辑 `config.properties`：

```properties
# Apple App Store Server API 配置
appstore.issuer.id=你的颁发者ID
appstore.key.id=你的密钥ID
appstore.bundle.id=com.yourcompany.yourapp
appstore.environment=SANDBOX
# appstore.environment=PRODUCTION

# 私钥文件路径
appstore.private.key.path=/path/to/SubscriptionKey_XXXXX.p8

# Apple 根证书路径（多个证书用逗号分隔）
appstore.root.ca.paths=/path/to/AppleRootCA-G2.cer,/path/to/AppleRootCA-G3.cer

# 生产环境需要 App Apple ID
# appstore.app.apple.id=1234567890
```

### 5. 构建项目

```bash
# 使用 Gradle Wrapper
./gradlew build

# 或直接使用 Gradle
gradle build
```

### 6. 运行程序

```bash
# 使用默认配置文件路径
./gradlew run

# 或指定配置文件路径
./gradlew run --args="path/to/config.properties"

# 直接运行 JAR
java -jar build/libs/appleOrderTool-1.0.0.jar
```

## 使用指南

### 交互式命令行界面

运行程序后，会显示交互式菜单：

```
╔═══════════════════════════════════════════╗
║   Apple App Store 订单查询工具            ║
║   App Store Order Query Tool              ║
╚═══════════════════════════════════════════╝

=============================================
功能菜单:
=============================================
1. 发送测试通知
2. 查询交易历史
3. 查询订单信息
4. 查询订阅状态
5. 查询退款历史
6. 从收据提取交易ID
7. 验证服务器通知
0. 退出
=============================================
```

### 代码示例

#### 1. 查询交易历史

```java
AppStoreConfig config = new AppStoreConfig("config.properties");
AppStoreService service = new AppStoreService(config);

String originalTransactionId = "1000000123456789";
List<String> transactions = service.getTransactionHistory(originalTransactionId);

for (String signedTransaction : transactions) {
    JWSTransactionDecodedPayload transaction = 
        service.verifyAndDecodeTransaction(signedTransaction);
    System.out.println("产品ID: " + transaction.getProductId());
    System.out.println("交易ID: " + transaction.getTransactionId());
}
```

#### 2. 查询订单信息

```java
String orderId = "M123456789";
OrderLookupResponse response = service.lookupOrder(orderId);
System.out.println("订单状态: " + response.getStatus());
```

#### 3. 验证服务器通知

```java
String signedPayload = "eyJhbGc..."; // 从服务器收到的签名负载
ResponseBodyV2DecodedPayload payload = 
    service.verifyAndDecodeNotification(signedPayload);
    
System.out.println("通知类型: " + payload.getNotificationType());
System.out.println("子类型: " + payload.getSubtype());
```

#### 4. 创建促销优惠签名

```java
PromotionalOfferUtil offerUtil = new PromotionalOfferUtil(
    "/path/to/key.p8",
    "KEYID12345",
    "com.example.app"
);

String signature = offerUtil.createSignature(
    "product_id",
    "offer_id",
    "app_account_token"
);
```

## API 参考

### AppStoreService

主要服务类，提供以下方法：

| 方法 | 说明 |
|------|------|
| `sendTestNotification()` | 发送测试通知 |
| `getTransactionHistory(transactionId)` | 查询交易历史 |
| `lookupOrder(orderId)` | 查询订单信息 |
| `getAllSubscriptionStatuses(transactionId)` | 查询订阅状态 |
| `getRefundHistory(transactionId)` | 查询退款历史 |
| `extractTransactionIdFromReceipt(receipt)` | 从收据提取交易ID |
| `verifyAndDecodeNotification(signedPayload)` | 验证并解码通知 |
| `verifyAndDecodeTransaction(signedTransaction)` | 验证并解码交易 |
| `verifyAndDecodeRenewalInfo(signedRenewalInfo)` | 验证并解码续订信息 |

## 环境说明

### SANDBOX (沙盒环境)
- 用于开发和测试
- 使用测试账号的购买数据
- 不需要 App Apple ID

### PRODUCTION (生产环境)
- 用于正式环境
- 使用真实用户的购买数据
- 必须提供 App Apple ID

## 安全注意事项

⚠️ **重要提示**

1. **私钥安全**：
   - 绝对不要将 `.p8` 私钥文件提交到版本控制系统
   - 妥善保管私钥文件，防止泄露
   - 建议使用环境变量或密钥管理服务

2. **配置文件**：
   - `config.properties` 已添加到 `.gitignore`，不会被提交到版本控制
   - 请基于 `config.properties.example` 创建自己的配置文件

3. **根证书验证**：
   - 始终验证服务器通知和交易数据的签名
   - 使用最新的 Apple 根证书

## 常见问题

### Q1: 如何获取原始交易ID？
A: 可以从客户端的购买收据、服务器通知或历史订单中获取。

### Q2: SANDBOX 和 PRODUCTION 环境有什么区别？
A: SANDBOX 用于测试，PRODUCTION 用于生产环境。两者使用不同的 API 端点和证书。

### Q3: 如何处理分页的交易历史？
A: `getTransactionHistory()` 方法已自动处理分页，返回所有交易记录。

### Q4: 签名验证失败怎么办？
A: 检查根证书是否正确、环境配置是否匹配、Bundle ID 是否一致。

## 开发计划

- [ ] 支持批量查询
- [ ] 添加 Web 管理界面
- [ ] 导出报表功能
- [ ] 数据统计分析
- [ ] 单元测试完善

## 依赖库

- [Apple App Store Server Library](https://github.com/apple/app-store-server-library-java) - 3.6.0
- [SLF4J](https://www.slf4j.org/) - 2.0.9
- [Logback](https://logback.qos.ch/) - 1.4.11
- [Jackson](https://github.com/FasterXML/jackson) - 2.15.2

## 相关文档

- [App Store Server API 官方文档](https://developer.apple.com/documentation/appstoreserverapi)
- [App Store Server Notifications V2](https://developer.apple.com/documentation/appstoreservernotifications)
- [App Store Receipt Validation](https://developer.apple.com/documentation/appstorereceipts)

## 许可证

MIT License

## 贡献

欢迎提交 Issue 和 Pull Request！

## 联系方式

如有问题或建议，请通过 GitHub Issues 反馈。

---

**Happy Coding! 🚀**

