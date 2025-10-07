# 快速开始指南

5 分钟快速上手 Apple App Store 订单查询工具！

## 第一步：准备工作 ✅

### 1. 确认 Java 环境

```bash
java -version
# 需要 Java 11 或更高版本
```

如果未安装 Java，请访问：https://adoptium.net/

### 2. 获取必要的密钥和证书

你需要准备以下文件：

| 文件 | 获取方式 | 说明 |
|------|----------|------|
| **API 私钥** (.p8) | App Store Connect → 用户和访问 → 集成 → App 内购买 | 只能下载一次 |
| **Issuer ID** | App Store Connect API 密钥页面 | 类似：`99b16628-15e4-4668-972b-eeff55eeff55` |
| **Key ID** | App Store Connect API 密钥页面 | 10 位字符，如：`ABCDEFGHIJ` |
| **Bundle ID** | App Store Connect 应用信息 | 如：`com.example.yourapp` |
| **Apple 根证书** | https://www.apple.com/certificateauthority/ | AppleRootCA-G2.cer, AppleRootCA-G3.cer |

📖 详细获取步骤请参考：[详细配置指南](docs/SETUP_GUIDE.md)

## 第二步：配置项目 ⚙️

### 1. 克隆或下载项目

```bash
cd /path/to/appleOrderTool
```

### 2. 创建配置文件

```bash
cp src/main/resources/config.properties.example src/main/resources/config.properties
```

### 3. 编辑配置文件

使用你喜欢的编辑器打开 `src/main/resources/config.properties`：

```bash
# macOS/Linux
nano src/main/resources/config.properties

# 或使用其他编辑器
code src/main/resources/config.properties  # VS Code
```

填入你的配置信息：

```properties
# 从 App Store Connect 获取
appstore.issuer.id=你的-Issuer-ID
appstore.key.id=你的-Key-ID
appstore.bundle.id=com.yourcompany.yourapp

# 开发测试使用 SANDBOX，生产环境使用 PRODUCTION
appstore.environment=SANDBOX

# 私钥文件的完整路径
appstore.private.key.path=/完整路径/到/SubscriptionKey_XXXXX.p8

# 根证书路径（用逗号分隔）
appstore.root.ca.paths=/路径/到/AppleRootCA-G2.cer,/路径/到/AppleRootCA-G3.cer
```

## 第三步：运行程序 🚀

### 方式 1：使用启动脚本（推荐）

```bash
# 赋予执行权限（首次运行）
chmod +x run.sh

# 运行程序
./run.sh
```

### 方式 2：使用 Gradle

```bash
# 构建项目
./gradlew build

# 运行程序
./gradlew run
```

### 方式 3：指定配置文件路径

```bash
./run.sh /path/to/your/config.properties
```

## 第四步：开始使用 🎉

程序启动后，你会看到交互式菜单：

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

请输入选项:
```

### 测试配置是否正确

选择选项 `1` 发送测试通知：

```
请输入选项: 1

>>> 发送测试通知...

✓ 测试通知已发送
测试通知令牌: test-notification-token-xxxxx

按回车键继续...
```

如果看到成功消息，说明配置正确！🎊

## 常用功能示例 📝

### 1. 查询交易历史

```
请输入选项: 2
请输入原始交易ID: 1000000123456789

>>> 查询交易历史...

✓ 共找到 5 条交易记录

是否显示详细信息？(y/n): y
```

### 2. 查询订单信息

```
请输入选项: 3
请输入订单ID: M123456789

>>> 查询订单信息...

✓ 订单查询成功
{
  "status": "VALID",
  "signedTransactions": [...]
}
```

### 3. 查询订阅状态

```
请输入选项: 4
请输入交易ID: 1000000123456789

>>> 查询订阅状态...

✓ 订阅状态查询成功
```

## 常见问题 ❓

### Q1: 提示"配置文件不存在"

**解决方案**：
```bash
# 检查配置文件是否存在
ls -la src/main/resources/config.properties

# 如果不存在，从示例复制
cp src/main/resources/config.properties.example src/main/resources/config.properties
```

### Q2: 提示"Unauthorized (401)"

**可能原因**：
- Issuer ID 或 Key ID 不正确
- 私钥文件路径错误
- 私钥文件内容不完整

**解决方案**：
```bash
# 1. 检查私钥文件是否存在
cat /path/to/SubscriptionKey_XXXXX.p8

# 2. 确认配置文件中的 ID 是否正确
grep "appstore\." src/main/resources/config.properties

# 3. 确保私钥文件格式正确（应以 -----BEGIN PRIVATE KEY----- 开头）
```

### Q3: Java 版本过低

**错误信息**：
```
错误: Java 版本过低，需要 Java 11 或更高版本
```

**解决方案**：
```bash
# 安装 Java 11 或更高版本
# macOS (使用 Homebrew)
brew install openjdk@11

# Ubuntu/Debian
sudo apt-get install openjdk-11-jdk

# 或下载安装包
# https://adoptium.net/
```

### Q4: 如何切换环境（沙盒 ↔ 生产）

编辑配置文件：

```properties
# 沙盒环境（开发测试）
appstore.environment=SANDBOX

# 生产环境（需要添加 app.apple.id）
appstore.environment=PRODUCTION
appstore.app.apple.id=1234567890
```

## 下一步 📚

### 学习更多

- 📖 [详细配置指南](docs/SETUP_GUIDE.md) - 完整的配置说明
- 💻 [API 使用示例](docs/API_EXAMPLES.md) - 各种场景的代码示例
- 📄 [README](README.md) - 项目完整文档

### 在代码中使用

```java
import com.apple.order.tool.config.AppStoreConfig;
import com.apple.order.tool.service.AppStoreService;

public class YourApp {
    public static void main(String[] args) throws Exception {
        // 加载配置
        AppStoreConfig config = new AppStoreConfig("config.properties");
        AppStoreService service = new AppStoreService(config);
        
        // 查询交易历史
        var transactions = service.getTransactionHistory("1000000123456789");
        System.out.println("交易数: " + transactions.size());
    }
}
```

### 开发环境建议

推荐使用以下 IDE：
- **IntelliJ IDEA** - Java 开发首选
- **Eclipse** - 免费开源
- **VS Code** - 轻量级，需安装 Java 扩展

## 获取帮助 🆘

如果遇到问题：

1. 📋 查看日志文件：`logs/appstore-tool.log`
2. 🔍 搜索 [已知问题](https://github.com/apple/app-store-server-library-java/issues)
3. 📖 阅读 [Apple 官方文档](https://developer.apple.com/documentation/appstoreserverapi)
4. 💬 提交 GitHub Issue

## 安全提醒 🔒

⚠️ **重要**：
- 不要将 `.p8` 私钥文件提交到 Git
- 不要分享配置文件（包含敏感信息）
- 定期轮换 API 密钥
- 生产环境使用更严格的权限控制

## 项目结构概览 📁

```
appleOrderTool/
├── src/main/java/          # Java 源代码
├── src/main/resources/     # 配置文件
├── src/test/java/          # 测试代码
├── docs/                   # 文档
├── build.gradle            # 构建配置
├── run.sh                  # 启动脚本
└── README.md               # 项目说明
```

---

**祝你使用愉快！** 🎉

如有问题，欢迎反馈！

