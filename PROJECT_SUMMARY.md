# 项目总结

## 项目信息

- **项目名称**: Apple App Store 订单查询工具
- **英文名称**: Apple App Store Order Query Tool
- **版本**: 1.0.0
- **语言**: Java 11+
- **构建工具**: Gradle 8.5
- **主要依赖**: Apple App Store Server Library 3.6.0

## 项目结构

```
appleOrderTool/
├── build.gradle                              # Gradle 构建配置
├── settings.gradle                           # Gradle 项目设置
├── gradle.properties                         # Gradle 属性配置
├── gradlew                                   # Unix/Linux Gradle Wrapper
├── gradlew.bat                               # Windows Gradle Wrapper
├── run.sh                                    # 启动脚本（Unix/Linux）
├── .gitignore                                # Git 忽略文件配置
│
├── gradle/
│   └── wrapper/
│       └── gradle-wrapper.properties         # Gradle Wrapper 配置
│
├── docs/                                     # 文档目录
│   ├── SETUP_GUIDE.md                        # 详细配置指南
│   └── API_EXAMPLES.md                       # API 使用示例
│
├── README.md                                 # 中文项目说明
├── README_EN.md                              # 英文项目说明
├── QUICKSTART.md                             # 快速开始指南
├── PROJECT_SUMMARY.md                        # 项目总结（本文件）
│
└── src/
    ├── main/
    │   ├── java/com/apple/order/tool/
    │   │   ├── AppStoreOrderTool.java        # 主程序入口
    │   │   │
    │   │   ├── config/
    │   │   │   └── AppStoreConfig.java       # 配置管理类
    │   │   │
    │   │   ├── service/
    │   │   │   └── AppStoreService.java      # 核心业务服务
    │   │   │
    │   │   └── util/
    │   │       └── PromotionalOfferUtil.java # 促销优惠工具
    │   │
    │   └── resources/
    │       ├── config.properties.example     # 配置文件模板
    │       └── logback.xml                   # 日志配置
    │
    └── test/
        └── java/com/apple/order/tool/
            └── AppStoreServiceTest.java      # 单元测试
```

## 核心功能

### 1. 交易管理
- ✅ 查询交易历史（自动分页）
- ✅ 验证交易签名
- ✅ 解码交易信息

### 2. 订单管理
- ✅ 根据订单ID查询订单信息
- ✅ 批量订单查询支持

### 3. 订阅管理
- ✅ 查询所有订阅状态
- ✅ 检查订阅有效性
- ✅ 获取续订信息

### 4. 退款管理
- ✅ 查询退款历史
- ✅ 解析退款交易

### 5. 通知验证
- ✅ 验证 App Store Server Notifications V2
- ✅ 解码通知负载
- ✅ 发送测试通知

### 6. 收据处理
- ✅ 从应用收据提取交易ID
- ✅ 迁移旧版收据到新版 API

### 7. 促销优惠
- ✅ 创建促销优惠签名
- ✅ 支持自定义 nonce 和时间戳

## 技术架构

### 核心依赖

| 依赖 | 版本 | 用途 |
|------|------|------|
| app-store-server-library | 3.6.0 | Apple 官方库 |
| slf4j-api | 2.0.9 | 日志接口 |
| logback-classic | 1.4.11 | 日志实现 |
| jackson-databind | 2.15.2 | JSON 处理 |
| jackson-datatype-jsr310 | 2.15.2 | Java 8 时间类型支持 |
| junit-jupiter | 5.10.0 | 单元测试 |

### 设计模式

- **配置管理**: 使用 Properties 文件集中管理配置
- **服务层**: 封装所有 API 调用逻辑
- **工具类**: 提供可复用的辅助功能
- **异常处理**: 统一的错误处理和日志记录

## 主要类说明

### 1. AppStoreOrderTool
- **路径**: `src/main/java/com/apple/order/tool/AppStoreOrderTool.java`
- **职责**: 主程序入口，提供交互式命令行界面
- **功能**:
  - 加载配置
  - 显示菜单
  - 处理用户输入
  - 调用服务层方法

### 2. AppStoreConfig
- **路径**: `src/main/java/com/apple/order/tool/config/AppStoreConfig.java`
- **职责**: 配置管理
- **功能**:
  - 加载配置文件
  - 验证必需参数
  - 读取私钥和证书
  - 提供配置访问接口

### 3. AppStoreService
- **路径**: `src/main/java/com/apple/order/tool/service/AppStoreService.java`
- **职责**: 核心业务逻辑
- **功能**:
  - 初始化 API 客户端
  - 查询交易、订单、订阅
  - 验证签名数据
  - 处理退款和通知

### 4. PromotionalOfferUtil
- **路径**: `src/main/java/com/apple/order/tool/util/PromotionalOfferUtil.java`
- **职责**: 促销优惠签名
- **功能**:
  - 创建优惠签名
  - 支持自定义参数

## 配置说明

### 必需配置项

```properties
# API 认证
appstore.issuer.id=<从 App Store Connect 获取>
appstore.key.id=<从 App Store Connect 获取>
appstore.bundle.id=<应用 Bundle ID>

# 环境设置
appstore.environment=SANDBOX  # 或 PRODUCTION

# 密钥和证书
appstore.private.key.path=<.p8 私钥文件路径>
appstore.root.ca.paths=<根证书路径，逗号分隔>
```

### 可选配置项

```properties
# 仅生产环境需要
appstore.app.apple.id=<App Apple ID>

# 日志级别
logging.level=INFO
```

## 使用方式

### 1. 交互式命令行（推荐新手）

```bash
./run.sh
```

### 2. 编程方式（推荐集成）

```java
AppStoreConfig config = new AppStoreConfig("config.properties");
AppStoreService service = new AppStoreService(config);

// 查询交易历史
List<String> transactions = service.getTransactionHistory("transaction_id");

// 验证通知
ResponseBodyV2DecodedPayload payload = 
    service.verifyAndDecodeNotification(signedPayload);
```

### 3. Gradle 命令

```bash
# 构建项目
./gradlew build

# 运行测试
./gradlew test

# 运行应用
./gradlew run

# 清理构建
./gradlew clean
```

## 安全特性

### 1. 私钥保护
- ✅ 配置文件不纳入版本控制
- ✅ 支持文件权限检查建议
- ✅ 私钥仅在内存中使用

### 2. 签名验证
- ✅ 使用 Apple 根证书验证所有签名数据
- ✅ 验证环境匹配（SANDBOX/PRODUCTION）
- ✅ 验证 Bundle ID 匹配

### 3. 错误处理
- ✅ 完整的异常捕获和日志记录
- ✅ 敏感信息不输出到日志
- ✅ 友好的错误提示

## 日志系统

### 日志配置
- **配置文件**: `src/main/resources/logback.xml`
- **日志文件**: `logs/appstore-tool.log`
- **滚动策略**: 每天一个文件，保留 30 天

### 日志级别
- **TRACE**: 最详细的调试信息
- **DEBUG**: 调试信息
- **INFO**: 常规信息（默认）
- **WARN**: 警告信息
- **ERROR**: 错误信息

## 测试

### 单元测试
- **框架**: JUnit 5
- **位置**: `src/test/java/com/apple/order/tool/`
- **运行**: `./gradlew test`

### 测试覆盖
- ⚠️ 当前测试需要有效的 API 配置才能运行
- 建议使用 SANDBOX 环境进行测试

## 文档

### 用户文档
1. **README.md** - 项目概述和完整功能说明（中文）
2. **README_EN.md** - 项目概述和完整功能说明（英文）
3. **QUICKSTART.md** - 5分钟快速上手指南
4. **docs/SETUP_GUIDE.md** - 详细配置步骤
5. **docs/API_EXAMPLES.md** - 各种使用场景的代码示例

### 开发文档
- 代码注释（JavaDoc 风格）
- 清晰的类和方法命名
- 完整的异常处理说明

## 后续计划

### 短期目标
- [ ] 添加更完善的单元测试
- [ ] 支持更多 API 端点
- [ ] 添加性能监控

### 中期目标
- [ ] 开发 Web 管理界面
- [ ] 支持数据导出（CSV/Excel）
- [ ] 添加数据统计和分析功能

### 长期目标
- [ ] 支持多应用管理
- [ ] 实现自动化订阅分析
- [ ] 构建 RESTful API 服务

## 兼容性

### 操作系统
- ✅ macOS
- ✅ Linux
- ✅ Windows

### Java 版本
- ✅ Java 11
- ✅ Java 17
- ✅ Java 21

### 构建工具
- ✅ Gradle 8.x
- ✅ Maven（需手动配置 pom.xml）

## 常见问题

### Q1: 如何切换环境？
编辑 `config.properties`，修改 `appstore.environment` 为 `SANDBOX` 或 `PRODUCTION`

### Q2: 支持哪些 App Store Server API？
支持所有主要的 App Store Server API v2 端点，包括交易历史、订单查询、订阅状态等

### Q3: 如何集成到现有项目？
将 `config`、`service`、`util` 包复制到你的项目，添加相应依赖即可

### Q4: 如何获取帮助？
1. 查看文档目录下的详细指南
2. 查看示例代码
3. 提交 GitHub Issue

## 许可证

MIT License - 可自由用于商业和个人项目

## 致谢

- Apple Inc. - 提供官方 App Store Server Library
- 开源社区 - 提供各种优秀的依赖库

## 联系方式

如有问题或建议，请通过以下方式联系：
- GitHub Issues
- Email: [待补充]

---

**项目创建日期**: 2025-10-07  
**最后更新**: 2025-10-07  
**状态**: ✅ 已完成基础功能

感谢使用本工具！🎉

