# 详细配置指南

本文档详细说明如何配置和使用 Apple App Store 订单查询工具。

## 一、获取 App Store Connect API 密钥

### 1.1 登录 App Store Connect

访问 [App Store Connect](https://appstoreconnect.apple.com/) 并使用你的 Apple Developer 账号登录。

### 1.2 创建 API 密钥

1. 点击顶部导航栏的 **"用户和访问"**
2. 选择 **"集成"** 标签页
3. 在左侧菜单中选择 **"App 内购买"** (In-App Purchase)
4. 点击 **"生成 API 密钥"** 按钮

### 1.3 配置密钥权限

- 为密钥命名（例如：Production API Key）
- 选择适当的访问权限
- 点击 **"生成"** 按钮

### 1.4 下载并保存密钥

1. 下载 `.p8` 格式的私钥文件（只能下载一次，请妥善保管）
2. 记录以下信息：
   - **Issuer ID**：在"密钥"页面顶部显示
   - **Key ID**：密钥列表中显示的 10 位字符
   - **Bundle ID**：你的应用的唯一标识符

**⚠️ 重要提示**：
- 私钥文件只能下载一次，丢失后需要重新生成
- 绝对不要将私钥文件上传到公共代码仓库
- 建议将私钥文件存储在安全的位置

## 二、下载 Apple 根证书

### 2.1 访问 Apple PKI 网站

访问 [Apple PKI](https://www.apple.com/certificateauthority/)

### 2.2 下载所需证书

在页面中找到并下载以下证书：

1. **Apple Root CA - G2 Root**
   - 文件名：AppleRootCA-G2.cer
   - 用于验证较旧的签名

2. **Apple Root CA - G3 Root**
   - 文件名：AppleRootCA-G3.cer
   - 用于验证较新的签名

### 2.3 保存证书文件

将下载的证书文件保存到安全的位置，例如：
```
/Users/yourusername/Certificates/AppleRootCA-G2.cer
/Users/yourusername/Certificates/AppleRootCA-G3.cer
```

## 三、配置项目

### 3.1 复制配置文件模板

```bash
cd /path/to/appleOrderTool
cp src/main/resources/config.properties.example src/main/resources/config.properties
```

### 3.2 编辑配置文件

使用文本编辑器打开 `config.properties` 并填入实际信息：

```properties
# ===========================================
# Apple App Store Server API 配置
# ===========================================

# 从 App Store Connect 获取的 Issuer ID
appstore.issuer.id=99b16628-15e4-4668-972b-eeff55eeff55

# 从 App Store Connect 获取的 Key ID
appstore.key.id=ABCDEFGHIJ

# 你的应用 Bundle ID
appstore.bundle.id=com.yourcompany.yourapp

# 环境设置：SANDBOX 或 PRODUCTION
# 开发测试时使用 SANDBOX
# 生产环境使用 PRODUCTION
appstore.environment=SANDBOX

# ===========================================
# 私钥配置
# ===========================================

# 私钥文件的完整路径
# 示例：/Users/yourusername/Keys/SubscriptionKey_ABCDEFGHIJ.p8
appstore.private.key.path=/path/to/your/SubscriptionKey_XXXXX.p8

# ===========================================
# 根证书配置
# ===========================================

# Apple 根证书路径，多个证书用逗号分隔
appstore.root.ca.paths=/path/to/AppleRootCA-G2.cer,/path/to/AppleRootCA-G3.cer

# ===========================================
# 生产环境配置（可选）
# ===========================================

# 仅在生产环境（PRODUCTION）中需要
# App Apple ID 可以在 App Store Connect 的应用信息中找到
# appstore.app.apple.id=1234567890

# ===========================================
# 日志配置
# ===========================================

# 日志级别：TRACE, DEBUG, INFO, WARN, ERROR
logging.level=INFO
```

### 3.3 配置说明

| 配置项 | 必需 | 说明 |
|--------|------|------|
| `appstore.issuer.id` | ✅ | App Store Connect 中显示的 Issuer ID |
| `appstore.key.id` | ✅ | API 密钥的 Key ID（10位字符） |
| `appstore.bundle.id` | ✅ | 应用的 Bundle Identifier |
| `appstore.environment` | ✅ | `SANDBOX` 或 `PRODUCTION` |
| `appstore.private.key.path` | ✅ | `.p8` 私钥文件的绝对路径 |
| `appstore.root.ca.paths` | ✅ | Apple 根证书路径，逗号分隔 |
| `appstore.app.apple.id` | 生产环境必需 | 应用的 Apple ID（纯数字） |

## 四、环境选择

### 4.1 SANDBOX（沙盒环境）

**使用场景**：
- 开发和测试阶段
- 使用 TestFlight 测试账号的购买
- 不需要 App Apple ID

**特点**：
- 使用测试数据
- 可以快速测试购买流程
- 支付不会产生实际费用

### 4.2 PRODUCTION（生产环境）

**使用场景**：
- 应用已上架 App Store
- 处理真实用户的购买数据
- 必须提供 App Apple ID

**特点**：
- 使用真实交易数据
- 涉及真实用户的实际支付
- 需要更严格的安全措施

## 五、获取 App Apple ID

App Apple ID 仅在生产环境中需要。

### 5.1 查找方式

1. 登录 [App Store Connect](https://appstoreconnect.apple.com/)
2. 选择你的应用
3. 在 **"App 信息"** 页面查看 **"Apple ID"**
4. 这是一个纯数字 ID（例如：1234567890）

### 5.2 配置方式

在 `config.properties` 中添加：

```properties
appstore.app.apple.id=1234567890
```

## 六、安全最佳实践

### 6.1 私钥安全

✅ **应该做的**：
- 将私钥文件存储在安全的位置
- 设置适当的文件权限（仅所有者可读）
- 使用密钥管理系统（如 AWS Secrets Manager、HashiCorp Vault）
- 定期轮换 API 密钥

❌ **不应该做的**：
- 不要将私钥提交到 Git 仓库
- 不要在代码中硬编码私钥内容
- 不要通过不安全的渠道传输私钥
- 不要与他人分享私钥

### 6.2 配置文件安全

```bash
# 设置配置文件权限（仅所有者可读写）
chmod 600 src/main/resources/config.properties

# 设置私钥文件权限（仅所有者可读）
chmod 400 /path/to/your/SubscriptionKey_XXXXX.p8
```

### 6.3 环境变量方式（可选）

如果不想使用配置文件，可以通过环境变量传递敏感信息：

```bash
export APPSTORE_ISSUER_ID="your-issuer-id"
export APPSTORE_KEY_ID="your-key-id"
export APPSTORE_PRIVATE_KEY_PATH="/path/to/key.p8"
```

## 七、验证配置

### 7.1 运行测试通知

配置完成后，运行程序并选择 **"发送测试通知"** 功能：

```bash
./run.sh
```

选择菜单选项 `1`，如果配置正确，应该会收到成功响应。

### 7.2 常见错误

**错误：Unauthorized (401)**
- 检查 Issuer ID、Key ID 是否正确
- 确认私钥文件路径是否正确
- 验证私钥文件内容是否完整

**错误：Invalid Bundle ID**
- 确认 Bundle ID 与应用配置一致
- 检查是否有多余的空格或特殊字符

**错误：Certificate verification failed**
- 确认根证书文件路径正确
- 检查根证书文件是否损坏
- 重新下载根证书文件

## 八、故障排查

### 8.1 启用调试日志

修改 `logback.xml`：

```xml
<root level="DEBUG">
    <appender-ref ref="STDOUT" />
    <appender-ref ref="FILE" />
</root>
```

### 8.2 查看日志文件

日志文件位置：`logs/appstore-tool.log`

```bash
# 实时查看日志
tail -f logs/appstore-tool.log
```

### 8.3 验证私钥格式

私钥文件应该以以下格式开始：

```
-----BEGIN PRIVATE KEY-----
MIGTAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBHkwdwIBAQQg...
...
-----END PRIVATE KEY-----
```

## 九、进一步帮助

如果遇到问题，请：

1. 查看 [Apple 官方文档](https://developer.apple.com/documentation/appstoreserverapi)
2. 查看项目 README.md 文档
3. 提交 GitHub Issue
4. 查看日志文件以获取详细错误信息

---

配置完成后，你就可以开始使用工具查询订单、验证通知等功能了！🎉

