package com.apple.order.tool.util;

import com.apple.itunes.storekit.offers.PromotionalOfferSignatureCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

/**
 * 促销优惠签名工具类
 * 用于创建App内促销优惠的签名
 */
public class PromotionalOfferUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(PromotionalOfferUtil.class);
    
    private final PromotionalOfferSignatureCreator signatureCreator;
    
    public PromotionalOfferUtil(String privateKeyPath, String keyId, String bundleId) throws IOException {
        String encodedKey = Files.readString(Path.of(privateKeyPath));
        this.signatureCreator = new PromotionalOfferSignatureCreator(encodedKey, keyId, bundleId);
    }
    
    /**
     * 创建促销优惠签名
     * 
     * @param productId 产品ID
     * @param subscriptionOfferId 订阅优惠ID
     * @param appAccountToken 应用账户令牌
     * @return 编码后的签名
     */
    public String createSignature(String productId, String subscriptionOfferId, String appAccountToken) {
        UUID nonce = UUID.randomUUID();
        long timestamp = System.currentTimeMillis();
        
        logger.info("创建促销优惠签名 - 产品ID: {}, 优惠ID: {}", productId, subscriptionOfferId);
        
        String signature = signatureCreator.createSignature(
            productId, 
            subscriptionOfferId, 
            appAccountToken, 
            nonce, 
            timestamp
        );
        
        logger.info("签名创建成功");
        return signature;
    }
    
    /**
     * 创建促销优惠签名（使用指定的nonce和时间戳）
     */
    public String createSignature(String productId, String subscriptionOfferId, 
                                  String appAccountToken, UUID nonce, long timestamp) {
        logger.info("创建促销优惠签名 - 产品ID: {}, 优惠ID: {}, Nonce: {}, 时间戳: {}", 
            productId, subscriptionOfferId, nonce, timestamp);
        
        String signature = signatureCreator.createSignature(
            productId, 
            subscriptionOfferId, 
            appAccountToken, 
            nonce, 
            timestamp
        );
        
        logger.info("签名创建成功");
        return signature;
    }
}

