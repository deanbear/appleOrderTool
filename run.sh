#!/bin/bash

# Apple App Store 订单查询工具启动脚本

# 检查 Java 版本
if ! command -v java &> /dev/null; then
    echo "错误: 未找到 Java，请先安装 Java 11 或更高版本"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | awk -F '.' '{print $1}')
if [ "$JAVA_VERSION" -lt 11 ]; then
    echo "错误: Java 版本过低，需要 Java 11 或更高版本"
    exit 1
fi

echo "==================================================="
echo "  Apple App Store 订单查询工具"
echo "  App Store Order Query Tool"
echo "==================================================="
echo ""

# 设置配置文件路径
CONFIG_FILE=${1:-"src/main/resources/config.properties"}

# 检查配置文件是否存在
if [ ! -f "$CONFIG_FILE" ]; then
    echo "警告: 配置文件不存在: $CONFIG_FILE"
    echo "请从 config.properties.example 复制并修改配置"
    echo ""
    echo "执行以下命令创建配置文件:"
    echo "  cp src/main/resources/config.properties.example src/main/resources/config.properties"
    echo "  然后编辑 config.properties 填入实际配置"
    exit 1
fi

echo "使用配置文件: $CONFIG_FILE"
echo ""

# 构建并运行
if [ -f "./gradlew" ]; then
    echo "使用 Gradle Wrapper 构建项目..."
    ./gradlew build -q
    
    echo ""
    echo "启动应用程序..."
    ./gradlew run --args="$CONFIG_FILE" -q --console=plain
else
    echo "使用 Gradle 构建项目..."
    gradle build -q
    
    echo ""
    echo "启动应用程序..."
    gradle run --args="$CONFIG_FILE" -q --console=plain
fi

