package com.apple.order.tool;

import com.apple.order.tool.config.AppStoreConfig;
import com.apple.order.tool.service.AppStoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

import static org.junit.jupiter.api.Assertions.*;

/**
 * App Store 服务测试类
 * 注意：这些测试需要有效的配置文件才能运行
 */
@Disabled("需要有效的 App Store Connect API 配置")
public class AppStoreServiceTest {
    
    private AppStoreService service;
    
    @BeforeEach
    public void setUp() throws Exception {
        AppStoreConfig config = new AppStoreConfig("src/main/resources/config.properties");
        service = new AppStoreService(config);
    }
    
    @Test
    public void testSendTestNotification() {
        assertDoesNotThrow(() -> {
            var response = service.sendTestNotification();
            assertNotNull(response);
            assertNotNull(response.getTestNotificationToken());
        });
    }
    
    @Test
    public void testGetTransactionHistory() {
        // 使用实际的交易ID进行测试
        String transactionId = "1000000000000000";
        
        assertDoesNotThrow(() -> {
            var transactions = service.getTransactionHistory(transactionId);
            assertNotNull(transactions);
        });
    }
}

