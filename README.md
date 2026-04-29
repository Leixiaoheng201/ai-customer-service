# AI 客服系统

一个基于 Java 的智能客服系统，具备请求路由、数据压缩和安全数据闭包功能。

## 核心功能
- **智能请求路由**：高效地将客户咨询分配给合适的处理模块
- **数据压缩**：在保持数据完整性的同时减少网络传输负载
- **安全数据闭包**：确保客户敏感信息得到妥善处理和保护

## 技术架构
- **开发语言**：Java 17
- **核心框架**：Spring Boot 3.2
- **构建工具**：Maven
- **AI 集成**：TensorFlow Lite（用于设备端推理）
- **API 网关**：Spring Cloud Gateway
- **数据库**：PostgreSQL + JDBC Template
- **日志系统**：SLF4J with Logback

## 安装指南

```bash
# 克隆仓库
git clone https://github.com/Leixiaoheng201/ai-customer-service.git

# 构建项目
mvn clean install

# 运行应用
java -jar target/ai-customer-service.jar
```

## 使用说明

启动后，服务将在 `http://localhost:8080` 可用。

- 通过 POST 请求提交咨询：`/api/v1/inquiries`
- 查看系统状态：`/actuator/health`

## 许可证

本项目采用 MIT 许可证 - 详情请参阅 [LICENSE](LICENSE) 文件。