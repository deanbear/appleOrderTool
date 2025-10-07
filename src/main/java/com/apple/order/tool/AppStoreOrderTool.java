package com.apple.order.tool;

import com.apple.itunes.storekit.client.APIException;
import com.apple.itunes.storekit.model.*;
import com.apple.itunes.storekit.verification.VerificationException;
import com.apple.order.tool.config.AppStoreConfig;
import com.apple.order.tool.service.AppStoreService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * App Store 订单查询工具主程序
 */
public class AppStoreOrderTool {
    
    private static final Logger logger = LoggerFactory.getLogger(AppStoreOrderTool.class);
    private static final ObjectMapper objectMapper = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .enable(SerializationFeature.INDENT_OUTPUT);
    
    private final AppStoreService service;
    
    public AppStoreOrderTool(AppStoreConfig config) {
        this.service = new AppStoreService(config);
    }
    
    public static void main(String[] args) {
        try {
            // 加载配置
            String configPath = args.length > 0 ? args[0] : "src/main/resources/config.properties";
            logger.info("加载配置文件: {}", configPath);
            
            AppStoreConfig config = new AppStoreConfig(configPath);
            AppStoreOrderTool tool = new AppStoreOrderTool(config);
            
            // 启动交互式命令行
            tool.runInteractiveMode();
            
        } catch (Exception e) {
            logger.error("程序执行失败", e);
            System.err.println("错误: " + e.getMessage());
            System.exit(1);
        }
    }
    
    /**
     * 交互式命令行模式
     */
    public void runInteractiveMode() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        
        printWelcome();
        
        while (running) {
            printMenu();
            System.out.print("\n请输入选项: ");
            String choice = scanner.nextLine().trim();
            
            try {
                switch (choice) {
                    case "1":
                        handleTestNotification();
                        break;
                    case "2":
                        handleTransactionHistory(scanner);
                        break;
                    case "3":
                        handleOrderLookup(scanner);
                        break;
                    case "4":
                        handleSubscriptionStatus(scanner);
                        break;
                    case "5":
                        handleRefundHistory(scanner);
                        break;
                    case "6":
                        handleReceiptExtraction(scanner);
                        break;
                    case "7":
                        handleNotificationVerification(scanner);
                        break;
                    case "0":
                        running = false;
                        System.out.println("\n感谢使用，再见！");
                        break;
                    default:
                        System.out.println("\n无效的选项，请重新选择");
                }
            } catch (Exception e) {
                logger.error("操作失败", e);
                System.err.println("\n操作失败: " + e.getMessage());
            }
            
            if (running) {
                System.out.println("\n按回车键继续...");
                scanner.nextLine();
            }
        }
        
        scanner.close();
    }
    
    private void printWelcome() {
        System.out.println("\n╔═══════════════════════════════════════════╗");
        System.out.println("║   Apple App Store 订单查询工具            ║");
        System.out.println("║   App Store Order Query Tool              ║");
        System.out.println("╚═══════════════════════════════════════════╝");
    }
    
    private void printMenu() {
        System.out.println("\n" + "=".repeat(45));
        System.out.println("功能菜单:");
        System.out.println("=".repeat(45));
        System.out.println("1. 发送测试通知");
        System.out.println("2. 查询交易历史");
        System.out.println("3. 查询订单信息");
        System.out.println("4. 查询订阅状态");
        System.out.println("5. 查询退款历史");
        System.out.println("6. 从收据提取交易ID");
        System.out.println("7. 验证服务器通知");
        System.out.println("0. 退出");
        System.out.println("=".repeat(45));
    }
    
    private void handleTestNotification() throws APIException, IOException {
        System.out.println("\n>>> 发送测试通知...");
        SendTestNotificationResponse response = service.sendTestNotification();
        System.out.println("\n✓ 测试通知已发送");
        System.out.println("测试通知令牌: " + response.getTestNotificationToken());
    }
    
    private void handleTransactionHistory(Scanner scanner) throws APIException, IOException, VerificationException {
        System.out.print("\n请输入原始交易ID: ");
        String transactionId = scanner.nextLine().trim();
        
        if (transactionId.isEmpty()) {
            System.out.println("交易ID不能为空");
            return;
        }
        
        System.out.println("\n>>> 查询交易历史...");
        List<String> transactions = service.getTransactionHistory(transactionId);
        
        System.out.println("\n✓ 共找到 " + transactions.size() + " 条交易记录");
        
        if (!transactions.isEmpty()) {
            System.out.print("\n是否显示详细信息？(y/n): ");
            String show = scanner.nextLine().trim().toLowerCase();
            
            if ("y".equals(show)) {
                for (int i = 0; i < transactions.size(); i++) {
                    System.out.println("\n--- 交易 " + (i + 1) + " ---");
                    var decodedTransaction = service.verifyAndDecodeTransaction(transactions.get(i));
                    System.out.println(objectMapper.writeValueAsString(decodedTransaction));
                }
            }
        }
    }
    
    private void handleOrderLookup(Scanner scanner) throws APIException, IOException {
        System.out.print("\n请输入订单ID: ");
        String orderId = scanner.nextLine().trim();
        
        if (orderId.isEmpty()) {
            System.out.println("订单ID不能为空");
            return;
        }
        
        System.out.println("\n>>> 查询订单信息...");
        OrderLookupResponse response = service.lookupOrder(orderId);
        
        System.out.println("\n✓ 订单查询成功");
        System.out.println(objectMapper.writeValueAsString(response));
    }
    
    private void handleSubscriptionStatus(Scanner scanner) throws APIException, IOException {
        System.out.print("\n请输入交易ID: ");
        String transactionId = scanner.nextLine().trim();
        
        if (transactionId.isEmpty()) {
            System.out.println("交易ID不能为空");
            return;
        }
        
        System.out.println("\n>>> 查询订阅状态...");
        StatusResponse response = service.getAllSubscriptionStatuses(transactionId);
        
        System.out.println("\n✓ 订阅状态查询成功");
        System.out.println(objectMapper.writeValueAsString(response));
    }
    
    private void handleRefundHistory(Scanner scanner) throws APIException, IOException {
        System.out.print("\n请输入交易ID: ");
        String transactionId = scanner.nextLine().trim();
        
        if (transactionId.isEmpty()) {
            System.out.println("交易ID不能为空");
            return;
        }
        
        System.out.println("\n>>> 查询退款历史...");
        RefundHistoryResponse response = service.getRefundHistory(transactionId);
        
        System.out.println("\n✓ 退款历史查询成功");
        System.out.println(objectMapper.writeValueAsString(response));
    }
    
    private void handleReceiptExtraction(Scanner scanner) throws IOException {
        System.out.print("\n请输入应用收据 (Base64格式): ");
        String receipt = scanner.nextLine().trim();
        
        if (receipt.isEmpty()) {
            System.out.println("收据不能为空");
            return;
        }
        
        System.out.println("\n>>> 提取交易ID...");
        String transactionId = service.extractTransactionIdFromReceipt(receipt);
        
        if (transactionId != null) {
            System.out.println("\n✓ 提取成功");
            System.out.println("交易ID: " + transactionId);
        } else {
            System.out.println("\n✗ 未能从收据中提取交易ID");
        }
    }
    
    private void handleNotificationVerification(Scanner scanner) throws VerificationException {
        System.out.print("\n请输入签名的通知负载: ");
        String signedPayload = scanner.nextLine().trim();
        
        if (signedPayload.isEmpty()) {
            System.out.println("通知负载不能为空");
            return;
        }
        
        System.out.println("\n>>> 验证通知...");
        ResponseBodyV2DecodedPayload payload = service.verifyAndDecodeNotification(signedPayload);
        
        System.out.println("\n✓ 通知验证成功");
        System.out.println("通知类型: " + payload.getNotificationType());
        System.out.println("子类型: " + payload.getSubtype());
        
        System.out.print("\n是否显示完整负载？(y/n): ");
        String show = scanner.nextLine().trim().toLowerCase();
        
        if ("y".equals(show)) {
            try {
                System.out.println("\n" + objectMapper.writeValueAsString(payload));
            } catch (Exception e) {
                logger.error("序列化失败", e);
            }
        }
    }
}

