package com.apple.order.tool.config;

import com.apple.itunes.storekit.model.Environment;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * App Store API 配置类
 * 用于加载和管理 App Store Server API 的配置信息
 */
public class AppStoreConfig {
    
    private final String issuerId;
    private final String keyId;
    private final String bundleId;
    private final String privateKey;
    private final Environment environment;
    private final Set<InputStream> rootCertificates;
    private final Long appAppleId;
    
    public AppStoreConfig(String configPath) throws IOException {
        Properties props = new Properties();
        
        // 加载配置文件
        Path path = Paths.get(configPath);
        if (!Files.exists(path)) {
            throw new IOException("配置文件不存在: " + configPath);
        }
        
        try (InputStream input = Files.newInputStream(path)) {
            props.load(input);
        }
        
        // 读取配置项
        this.issuerId = getRequiredProperty(props, "appstore.issuer.id");
        this.keyId = getRequiredProperty(props, "appstore.key.id");
        this.bundleId = getRequiredProperty(props, "appstore.bundle.id");
        
        // 读取私钥
        String privateKeyPath = getRequiredProperty(props, "appstore.private.key.path");
        this.privateKey = Files.readString(Paths.get(privateKeyPath));
        
        // 读取环境配置
        String envStr = getRequiredProperty(props, "appstore.environment");
        this.environment = Environment.valueOf(envStr.toUpperCase());
        
        // 读取 App Apple ID (仅生产环境需要)
        String appAppleIdStr = props.getProperty("appstore.app.apple.id");
        this.appAppleId = (appAppleIdStr != null && !appAppleIdStr.isEmpty()) 
            ? Long.parseLong(appAppleIdStr) : null;
        
        // 读取根证书
        String rootCaPathsStr = getRequiredProperty(props, "appstore.root.ca.paths");
        this.rootCertificates = Stream.of(rootCaPathsStr.split(","))
            .map(String::trim)
            .map(this::loadCertificate)
            .collect(Collectors.toSet());
    }
    
    private String getRequiredProperty(Properties props, String key) {
        String value = props.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("缺少必需的配置项: " + key);
        }
        return value.trim();
    }
    
    private InputStream loadCertificate(String path) {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(path));
            return new ByteArrayInputStream(bytes);
        } catch (IOException e) {
            throw new RuntimeException("无法加载根证书: " + path, e);
        }
    }
    
    public String getIssuerId() {
        return issuerId;
    }
    
    public String getKeyId() {
        return keyId;
    }
    
    public String getBundleId() {
        return bundleId;
    }
    
    public String getPrivateKey() {
        return privateKey;
    }
    
    public Environment getEnvironment() {
        return environment;
    }
    
    public Set<InputStream> getRootCertificates() {
        return rootCertificates;
    }
    
    public Long getAppAppleId() {
        return appAppleId;
    }
}

