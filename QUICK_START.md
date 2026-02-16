# 🚀 Quick Start Guide

## 最快启动方式（1分钟内）

### Windows用户

双击运行 `start.bat` 文件，选择模式1（快速模式）

或在PowerShell中运行：
```powershell
java -jar target/sentiment-analysis-0.0.1-SNAPSHOT.jar --spring.cache.type=none
```

### Mac/Linux用户

```bash
./start.sh
# 选择模式1（快速模式）
```

或直接运行：
```bash
java -jar target/sentiment-analysis-0.0.1-SNAPSHOT.jar --spring.cache.type=none
```

## 访问应用

打开浏览器访问：**http://localhost:8080**

### 默认账号
- 用户名：`admin`
- 密码：`admin123`

## 三种运行模式

### 1️⃣ 快速模式（推荐第一次使用）
- ✅ **无需Docker**，只需要Java
- ✅ **启动快**，20-30秒
- ⚠️ 数据存储在内存中（重启后清空）
- ⚠️ 无缓存，无LLM验证

```bash
java -jar target/*.jar --spring.cache.type=none
```

### 2️⃣ 完整模式（生产环境）
- ✅ **完整功能**：MySQL + Redis + LLM
- ✅ **数据持久化**
- ⚠️ 需要Docker Desktop

```bash
docker compose up -d
```

### 3️⃣ 开发模式
- ✅ **热重载**，适合开发
- ⚠️ 需要Maven

```bash
mvn spring-boot:run
```

## 常见问题

### Java未安装？
下载安装：https://adoptium.net/ （选择Java 17）

### Docker未安装？
使用快速模式（模式1）- 不需要Docker

### 端口8080被占用？
更改端口：
```bash
java -jar target/*.jar --server.port=8081
```

### 找不到JAR文件？
运行：
```bash
mvn clean package
```

## 核心功能测试

1. **登录系统** - 使用 admin/admin123
2. **提交评论** - 在首页文本框输入任意文本
3. **查看分析结果** - 查看情感分析、表情符号和颜色
4. **查看统计图表** - 点击"View Sentiment Distribution"
5. **管理员功能** - 点击"Admin Dashboard"管理用户和评论

## 安全分析API测试

使用Postman或curl测试：

```bash
# 分析文本安全威胁
curl -X POST http://localhost:8080/api/security/analyze \
  -H "Content-Type: application/json" \
  -d '{"text": "Critical vulnerability detected in the system"}'

# 分析威胁情报报告
curl -X POST http://localhost:8080/api/security/analyze-threat-report \
  -H "Content-Type: application/json" \
  -d '{"report": "Zero-day exploit affecting multiple systems"}'
```

## 停止应用

### 快速模式
按 `Ctrl+C` 在运行应用的终端窗口中

### Docker模式
```bash
docker compose down
```

## 更多信息

- 📖 完整文档：[README.md](README.md)
- 📋 更新日志：[CHANGELOG.md](CHANGELOG.md)
- ✅ 验证清单：[VERIFICATION_CHECKLIST.md](VERIFICATION_CHECKLIST.md)
- 📝 项目总结：[PROJECT_SUMMARY.md](PROJECT_SUMMARY.md)

---

**遇到问题？** 查看 [README.md的故障排除部分](README.md#-troubleshooting)
