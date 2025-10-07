# API 使用示例

本文档提供了各种 API 使用场景的详细示例代码。

## 目录

- [基础配置](#基础配置)
- [查询交易历史](#查询交易历史)
- [订单查询](#订单查询)
- [订阅管理](#订阅管理)
- [退款处理](#退款处理)
- [通知验证](#通知验证)
- [收据处理](#收据处理)
- [促销优惠](#促销优惠)

## 基础配置

### 加载配置并初始化服务

```java
import com.apple.order.tool.config.AppStoreConfig;
import com.apple.order.tool.service.AppStoreService;

public class Example {
    public static void main(String[] args) throws Exception {
        // 加载配置
        AppStoreConfig config = new AppStoreConfig("config.properties");
        
        // 创建服务实例
        AppStoreService service = new AppStoreService(config);
        
        // 现在可以使用服务了
    }
}
```

## 查询交易历史

### 1. 查询所有交易记录

```java
import com.apple.itunes.storekit.model.JWSTransactionDecodedPayload;
import java.util.List;

public class TransactionHistoryExample {
    public void getAllTransactions(AppStoreService service) throws Exception {
        String originalTransactionId = "1000000123456789";
        
        // 获取所有交易记录（自动分页）
        List<String> signedTransactions = service.getTransactionHistory(originalTransactionId);
        
        System.out.println("总交易数: " + signedTransactions.size());
        
        // 解析并处理每条交易
        for (String signedTransaction : signedTransactions) {
            JWSTransactionDecodedPayload transaction = 
                service.verifyAndDecodeTransaction(signedTransaction);
            
            System.out.println("交易ID: " + transaction.getTransactionId());
            System.out.println("产品ID: " + transaction.getProductId());
            System.out.println("购买日期: " + transaction.getPurchaseDate());
            System.out.println("---");
        }
    }
}
```

### 2. 筛选特定产品的交易

```java
import com.apple.itunes.storekit.model.JWSTransactionDecodedPayload;
import java.util.List;
import java.util.stream.Collectors;

public class FilterTransactionsExample {
    public void filterByProduct(AppStoreService service, String productId) throws Exception {
        String originalTransactionId = "1000000123456789";
        
        List<String> signedTransactions = service.getTransactionHistory(originalTransactionId);
        
        // 筛选特定产品的交易
        List<JWSTransactionDecodedPayload> productTransactions = signedTransactions.stream()
            .map(signed -> {
                try {
                    return service.verifyAndDecodeTransaction(signed);
                } catch (Exception e) {
                    return null;
                }
            })
            .filter(t -> t != null && productId.equals(t.getProductId()))
            .collect(Collectors.toList());
        
        System.out.println("产品 " + productId + " 的交易数: " + productTransactions.size());
    }
}
```

## 订单查询

### 1. 查询单个订单

```java
import com.apple.itunes.storekit.model.OrderLookupResponse;
import com.apple.itunes.storekit.model.OrderLookupStatus;

public class OrderLookupExample {
    public void lookupOrder(AppStoreService service) throws Exception {
        String orderId = "M123456789";
        
        OrderLookupResponse response = service.lookupOrder(orderId);
        
        System.out.println("订单状态: " + response.getStatus());
        
        if (response.getStatus() == OrderLookupStatus.VALID) {
            System.out.println("签名交易: " + response.getSignedTransactions());
        }
    }
}
```

### 2. 批量查询订单

```java
import com.apple.itunes.storekit.model.OrderLookupResponse;
import java.util.List;
import java.util.ArrayList;

public class BatchOrderLookupExample {
    public void lookupOrders(AppStoreService service, List<String> orderIds) {
        List<OrderLookupResponse> results = new ArrayList<>();
        
        for (String orderId : orderIds) {
            try {
                OrderLookupResponse response = service.lookupOrder(orderId);
                results.add(response);
                System.out.println("订单 " + orderId + " 状态: " + response.getStatus());
            } catch (Exception e) {
                System.err.println("查询订单 " + orderId + " 失败: " + e.getMessage());
            }
        }
        
        System.out.println("成功查询 " + results.size() + "/" + orderIds.size() + " 个订单");
    }
}
```

## 订阅管理

### 1. 查询订阅状态

```java
import com.apple.itunes.storekit.model.StatusResponse;
import com.apple.itunes.storekit.model.SubscriptionGroupIdentifierItem;

public class SubscriptionStatusExample {
    public void checkSubscriptionStatus(AppStoreService service) throws Exception {
        String transactionId = "1000000123456789";
        
        StatusResponse response = service.getAllSubscriptionStatuses(transactionId);
        
        System.out.println("环境: " + response.getEnvironment());
        System.out.println("Bundle ID: " + response.getBundleId());
        System.out.println("App Account Token: " + response.getAppAccountToken());
        
        // 遍历订阅组
        if (response.getData() != null) {
            for (SubscriptionGroupIdentifierItem item : response.getData()) {
                System.out.println("订阅组 ID: " + item.getSubscriptionGroupIdentifier());
                System.out.println("最后交易: " + item.getLastTransactions());
            }
        }
    }
}
```

### 2. 检查订阅是否有效

```java
import com.apple.itunes.storekit.model.*;
import com.apple.itunes.storekit.verification.VerificationException;

public class CheckActiveSubscriptionExample {
    public boolean isSubscriptionActive(AppStoreService service, String transactionId) 
            throws Exception {
        StatusResponse response = service.getAllSubscriptionStatuses(transactionId);
        
        if (response.getData() == null || response.getData().isEmpty()) {
            return false;
        }
        
        for (SubscriptionGroupIdentifierItem item : response.getData()) {
            for (LastTransactionsItem transaction : item.getLastTransactions()) {
                String signedTransaction = transaction.getSignedTransaction();
                JWSTransactionDecodedPayload decoded = 
                    service.verifyAndDecodeTransaction(signedTransaction);
                
                // 检查是否在有效期内
                if (decoded.getExpiresDate() != null) {
                    long expiresTime = decoded.getExpiresDate();
                    long currentTime = System.currentTimeMillis();
                    
                    if (expiresTime > currentTime) {
                        return true; // 订阅仍然有效
                    }
                }
            }
        }
        
        return false;
    }
}
```

## 退款处理

### 1. 查询退款历史

```java
import com.apple.itunes.storekit.model.RefundHistoryResponse;

public class RefundHistoryExample {
    public void getRefundHistory(AppStoreService service) throws Exception {
        String transactionId = "1000000123456789";
        
        RefundHistoryResponse response = service.getRefundHistory(transactionId);
        
        System.out.println("是否有更多: " + response.getHasMore());
        System.out.println("退款交易: " + response.getSignedTransactions());
        
        // 解析退款交易
        if (response.getSignedTransactions() != null) {
            for (String signedTransaction : response.getSignedTransactions()) {
                var transaction = service.verifyAndDecodeTransaction(signedTransaction);
                System.out.println("退款交易 ID: " + transaction.getTransactionId());
                System.out.println("退款日期: " + transaction.getRevocationDate());
            }
        }
    }
}
```

## 通知验证

### 1. 验证 V2 版本通知

```java
import com.apple.itunes.storekit.model.*;
import com.apple.itunes.storekit.verification.VerificationException;

public class NotificationVerificationExample {
    public void verifyNotification(AppStoreService service, String signedPayload) {
        try {
            ResponseBodyV2DecodedPayload payload = 
                service.verifyAndDecodeNotification(signedPayload);
            
            System.out.println("通知类型: " + payload.getNotificationType());
            System.out.println("子类型: " + payload.getSubtype());
            
            // 根据通知类型处理
            switch (payload.getNotificationType()) {
                case SUBSCRIBED:
                    handleSubscribed(payload);
                    break;
                case DID_RENEW:
                    handleDidRenew(payload);
                    break;
                case EXPIRED:
                    handleExpired(payload);
                    break;
                case REFUND:
                    handleRefund(payload);
                    break;
                default:
                    System.out.println("其他通知类型: " + payload.getNotificationType());
            }
            
        } catch (VerificationException e) {
            System.err.println("通知验证失败: " + e.getMessage());
        }
    }
    
    private void handleSubscribed(ResponseBodyV2DecodedPayload payload) {
        System.out.println("用户订阅了服务");
        // 处理新订阅逻辑
    }
    
    private void handleDidRenew(ResponseBodyV2DecodedPayload payload) {
        System.out.println("订阅已续订");
        // 处理续订逻辑
    }
    
    private void handleExpired(ResponseBodyV2DecodedPayload payload) {
        System.out.println("订阅已过期");
        // 处理过期逻辑
    }
    
    private void handleRefund(ResponseBodyV2DecodedPayload payload) {
        System.out.println("发生退款");
        // 处理退款逻辑
    }
}
```

### 2. 处理测试通知

```java
import com.apple.itunes.storekit.model.SendTestNotificationResponse;

public class TestNotificationExample {
    public void sendAndVerifyTestNotification(AppStoreService service) throws Exception {
        // 发送测试通知
        SendTestNotificationResponse response = service.sendTestNotification();
        String testNotificationToken = response.getTestNotificationToken();
        
        System.out.println("测试通知已发送");
        System.out.println("测试通知令牌: " + testNotificationToken);
        
        // 在实际场景中，你会从服务器接收到通知，然后验证它
        // String receivedPayload = ... // 从服务器接收
        // service.verifyAndDecodeNotification(receivedPayload);
    }
}
```

## 收据处理

### 1. 从收据提取交易ID

```java
public class ReceiptProcessingExample {
    public void extractTransactionId(AppStoreService service) throws Exception {
        // Base64 编码的应用收据
        String appReceipt = "MIITtgYJKoZIhvcNAQcCoIITpzCCE6MCAQEx...";
        
        String transactionId = service.extractTransactionIdFromReceipt(appReceipt);
        
        if (transactionId != null) {
            System.out.println("提取的交易ID: " + transactionId);
            
            // 使用交易ID查询历史
            var transactions = service.getTransactionHistory(transactionId);
            System.out.println("找到 " + transactions.size() + " 条交易");
        } else {
            System.out.println("无法从收据中提取交易ID");
        }
    }
}
```

## 促销优惠

### 1. 创建促销优惠签名

```java
import com.apple.order.tool.util.PromotionalOfferUtil;
import java.util.UUID;

public class PromotionalOfferExample {
    public void createOfferSignature() throws Exception {
        String privateKeyPath = "/path/to/SubscriptionKey_XXXXX.p8";
        String keyId = "ABCDEFGHIJ";
        String bundleId = "com.example.app";
        
        PromotionalOfferUtil offerUtil = new PromotionalOfferUtil(
            privateKeyPath, keyId, bundleId
        );
        
        String productId = "com.example.premium";
        String subscriptionOfferId = "PROMO2024";
        String appAccountToken = "user_12345";
        
        // 方式1：自动生成 nonce 和时间戳
        String signature1 = offerUtil.createSignature(
            productId, subscriptionOfferId, appAccountToken
        );
        
        System.out.println("签名 1: " + signature1);
        
        // 方式2：指定 nonce 和时间戳
        UUID nonce = UUID.randomUUID();
        long timestamp = System.currentTimeMillis();
        
        String signature2 = offerUtil.createSignature(
            productId, subscriptionOfferId, appAccountToken, nonce, timestamp
        );
        
        System.out.println("签名 2: " + signature2);
        System.out.println("Nonce: " + nonce);
        System.out.println("Timestamp: " + timestamp);
    }
}
```

## 完整示例：订阅生命周期管理

```java
import com.apple.order.tool.config.AppStoreConfig;
import com.apple.order.tool.service.AppStoreService;
import com.apple.itunes.storekit.model.*;

public class SubscriptionLifecycleExample {
    
    private final AppStoreService service;
    
    public SubscriptionLifecycleExample(String configPath) throws Exception {
        AppStoreConfig config = new AppStoreConfig(configPath);
        this.service = new AppStoreService(config);
    }
    
    /**
     * 处理完整的订阅生命周期
     */
    public void handleSubscriptionLifecycle(String transactionId) {
        try {
            // 1. 查询订阅状态
            StatusResponse status = service.getAllSubscriptionStatuses(transactionId);
            System.out.println("当前订阅状态已获取");
            
            // 2. 查询交易历史
            var transactions = service.getTransactionHistory(transactionId);
            System.out.println("交易历史: " + transactions.size() + " 条记录");
            
            // 3. 查询退款历史
            RefundHistoryResponse refunds = service.getRefundHistory(transactionId);
            if (refunds.getSignedTransactions() != null && !refunds.getSignedTransactions().isEmpty()) {
                System.out.println("发现退款记录: " + refunds.getSignedTransactions().size() + " 条");
            }
            
            // 4. 分析订阅状态
            boolean isActive = analyzeSubscriptionStatus(status);
            if (isActive) {
                System.out.println("订阅当前处于活跃状态");
            } else {
                System.out.println("订阅已失效或过期");
            }
            
        } catch (Exception e) {
            System.err.println("处理订阅生命周期失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private boolean analyzeSubscriptionStatus(StatusResponse status) throws Exception {
        if (status.getData() == null || status.getData().isEmpty()) {
            return false;
        }
        
        for (SubscriptionGroupIdentifierItem item : status.getData()) {
            for (LastTransactionsItem transaction : item.getLastTransactions()) {
                JWSTransactionDecodedPayload decoded = 
                    service.verifyAndDecodeTransaction(transaction.getSignedTransaction());
                
                if (decoded.getExpiresDate() != null) {
                    long expiresTime = decoded.getExpiresDate();
                    if (expiresTime > System.currentTimeMillis()) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    
    public static void main(String[] args) throws Exception {
        SubscriptionLifecycleExample example = 
            new SubscriptionLifecycleExample("config.properties");
        
        String transactionId = "1000000123456789";
        example.handleSubscriptionLifecycle(transactionId);
    }
}
```

## 错误处理最佳实践

```java
import com.apple.itunes.storekit.client.APIException;
import com.apple.itunes.storekit.verification.VerificationException;
import java.io.IOException;

public class ErrorHandlingExample {
    
    public void robustApiCall(AppStoreService service, String transactionId) {
        try {
            var transactions = service.getTransactionHistory(transactionId);
            System.out.println("成功获取 " + transactions.size() + " 条交易");
            
        } catch (APIException e) {
            // API 错误（如 401, 404, 500 等）
            System.err.println("API 错误: " + e.getHttpStatusCode());
            System.err.println("错误消息: " + e.getMessage());
            
            if (e.getHttpStatusCode() == 401) {
                System.err.println("认证失败，请检查 API 密钥配置");
            } else if (e.getHttpStatusCode() == 404) {
                System.err.println("交易不存在");
            }
            
        } catch (IOException e) {
            // 网络错误
            System.err.println("网络错误: " + e.getMessage());
            
        } catch (VerificationException e) {
            // 签名验证错误
            System.err.println("签名验证失败: " + e.getMessage());
            
        } catch (Exception e) {
            // 其他未知错误
            System.err.println("未知错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
```

## 性能优化建议

### 1. 批量处理

```java
import java.util.concurrent.*;
import java.util.List;
import java.util.ArrayList;

public class BatchProcessingExample {
    
    private final ExecutorService executor = Executors.newFixedThreadPool(5);
    
    public List<StatusResponse> batchGetSubscriptionStatus(
            AppStoreService service, 
            List<String> transactionIds) {
        
        List<Future<StatusResponse>> futures = new ArrayList<>();
        
        for (String transactionId : transactionIds) {
            Future<StatusResponse> future = executor.submit(() -> 
                service.getAllSubscriptionStatuses(transactionId)
            );
            futures.add(future);
        }
        
        List<StatusResponse> results = new ArrayList<>();
        for (Future<StatusResponse> future : futures) {
            try {
                results.add(future.get(30, TimeUnit.SECONDS));
            } catch (Exception e) {
                System.err.println("获取状态失败: " + e.getMessage());
            }
        }
        
        return results;
    }
    
    public void shutdown() {
        executor.shutdown();
    }
}
```

---

更多示例和最佳实践，请参考 [Apple 官方文档](https://developer.apple.com/documentation/appstoreserverapi)。

