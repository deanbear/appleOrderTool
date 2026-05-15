package com.apple.order.tool.service;

import com.apple.itunes.storekit.client.APIException;
import com.apple.itunes.storekit.client.AppStoreServerAPIClient;
import com.apple.itunes.storekit.client.GetTransactionHistoryVersion;
import com.apple.itunes.storekit.migration.ReceiptUtility;
import com.apple.itunes.storekit.model.*;
import com.apple.itunes.storekit.verification.SignedDataVerifier;
import com.apple.itunes.storekit.verification.VerificationException;
import com.apple.order.tool.config.AppStoreConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * App Store 服务类
 * 提供订单查询、交易历史、通知验证等功能
 */
public class AppStoreService {
    
    private static final Logger logger = LoggerFactory.getLogger(AppStoreService.class);
    
    private final AppStoreServerAPIClient apiClient;
    private final SignedDataVerifier dataVerifier;
    private final ReceiptUtility receiptUtility;
    
    public AppStoreService(AppStoreConfig config) {
        this.apiClient = new AppStoreServerAPIClient(
            config.getPrivateKey(),
            config.getKeyId(),
            config.getIssuerId(),
            config.getBundleId(),
            config.getEnvironment()
        );
        
        this.dataVerifier = new SignedDataVerifier(
            config.getRootCertificates(),
            config.getBundleId(),
            config.getAppAppleId(),
            config.getEnvironment(),
            true
        );
        
        this.receiptUtility = new ReceiptUtility();
    }
    
    /**
     * 发送测试通知
     */
    public SendTestNotificationResponse sendTestNotification() throws APIException, IOException {
        logger.info("发送测试通知");
        SendTestNotificationResponse response = apiClient.requestTestNotification();
        logger.info("测试通知已发送，测试通知令牌: {}", response.getTestNotificationToken());
        return response;
    }
    
    /**
     * 根据原始交易ID查询交易历史
     */
    public List<String> getTransactionHistory(String originalTransactionId) 
            throws APIException, IOException {
        logger.info("查询交易历史，原始交易ID: {}", originalTransactionId);
        
        List<String> allTransactions = new ArrayList<>();
        TransactionHistoryRequest request = new TransactionHistoryRequest()
            .sort(TransactionHistoryRequest.Order.ASCENDING)
            .revoked(false);
        
        HistoryResponse response = null;
        do {
            String revision = response != null ? response.getRevision() : null;
            response = apiClient.getTransactionHistory(
                originalTransactionId, 
                revision, 
                request, 
                GetTransactionHistoryVersion.V2
            );
            
            if (response.getSignedTransactions() != null) {
                allTransactions.addAll(response.getSignedTransactions());
            }
            
            logger.debug("获取到 {} 条交易记录，还有更多: {}", 
                response.getSignedTransactions() != null ? response.getSignedTransactions().size() : 0,
                response.getHasMore());
                
        } while (Boolean.TRUE.equals(response.getHasMore()));
        
        logger.info("共获取 {} 条交易记录", allTransactions.size());
        return allTransactions;
    }
    
    /**
     * 查询订单信息
     */
    public OrderLookupResponse lookupOrder(String orderId) throws APIException, IOException {
        logger.info("查询订单信息，订单ID: {}", orderId);
        OrderLookupResponse response = apiClient.lookUpOrderId(orderId);
        logger.info("订单查询成功，状态: {}", response.getStatus());
        return response;
    }
    
    /**
     * 查询所有订阅状态
     */
    public StatusResponse getAllSubscriptionStatuses(String transactionId) 
            throws APIException, IOException {
        logger.info("查询所有订阅状态，交易ID: {}", transactionId);
        StatusResponse response = apiClient.getAllSubscriptionStatuses(transactionId, null);
        logger.info("订阅状态查询成功");
        return response;
    }
    
    /**
     * 获取退款历史（自动处理分页）
     */
    public List<String> getRefundHistory(String transactionId)
            throws APIException, IOException {
        logger.info("查询退款历史，交易ID: {}", transactionId);

        List<String> allTransactions = new ArrayList<>();
        RefundHistoryResponse response = null;
        do {
            String revision = response != null ? response.getRevision() : null;
            response = apiClient.getRefundHistory(transactionId, revision);

            if (response.getSignedTransactions() != null) {
                allTransactions.addAll(response.getSignedTransactions());
            }

            logger.debug("获取到 {} 条退款记录，还有更多: {}",
                response.getSignedTransactions() != null ? response.getSignedTransactions().size() : 0,
                response.getHasMore());

        } while (Boolean.TRUE.equals(response.getHasMore()));

        logger.info("共获取 {} 条退款记录", allTransactions.size());
        return allTransactions;
    }
    
    /**
     * 从应用收据中提取交易ID
     */
    public String extractTransactionIdFromReceipt(String appReceipt) throws IOException {
        logger.info("从收据中提取交易ID");
        String transactionId = receiptUtility.extractTransactionIdFromAppReceipt(appReceipt);
        if (transactionId != null) {
            logger.info("提取到交易ID: {}", transactionId);
        } else {
            logger.warn("未能从收据中提取交易ID");
        }
        return transactionId;
    }
    
    /**
     * 验证并解码通知
     */
    public ResponseBodyV2DecodedPayload verifyAndDecodeNotification(String signedPayload) 
            throws VerificationException {
        logger.info("验证并解码通知");
        ResponseBodyV2DecodedPayload payload = dataVerifier.verifyAndDecodeNotification(signedPayload);
        logger.info("通知验证成功，类型: {}, 子类型: {}", 
            payload.getNotificationType(), 
            payload.getSubtype());
        return payload;
    }
    
    /**
     * 验证并解码交易信息
     */
    public JWSTransactionDecodedPayload verifyAndDecodeTransaction(String signedTransaction) 
            throws VerificationException {
        logger.info("验证并解码交易信息");
        JWSTransactionDecodedPayload transaction = dataVerifier.verifyAndDecodeTransaction(signedTransaction);
        logger.info("交易验证成功，产品ID: {}, 交易ID: {}", 
            transaction.getProductId(), 
            transaction.getTransactionId());
        return transaction;
    }
    
    /**
     * 验证并解码续订信息
     */
    public JWSRenewalInfoDecodedPayload verifyAndDecodeRenewalInfo(String signedRenewalInfo) 
            throws VerificationException {
        logger.info("验证并解码续订信息");
        JWSRenewalInfoDecodedPayload renewalInfo = dataVerifier.verifyAndDecodeRenewalInfo(signedRenewalInfo);
        logger.info("续订信息验证成功");
        return renewalInfo;
    }
}

