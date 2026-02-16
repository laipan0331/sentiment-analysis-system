# ✅ 启动前检查清单

## 系统要求检查

### 必需（任选一种方式）

**方式1：纯Java运行（最简单）**
- [ ] Java 17 或更高版本已安装
  - 检查命令：`java -version`
  - 下载地址：https://adoptium.net/

**方式2：Docker运行（完整功能）**
- [ ] Docker Desktop已安装并运行
  - 检查命令：`docker --version`
  - 下载地址：https://www.docker.com/products/docker-desktop/

**方式3：开发模式**  
- [ ] Java 17 或更高版本
- [ ] Maven 3.6+ 已安装
  - 检查命令：`mvn --version`

## 文件检查

- [ ] JAR文件存在：`target/sentiment-analysis-0.0.1-SNAPSHOT.jar`
  - 不存在？运行：`mvn clean package`
  
- [ ] 环境配置文件（Docker模式需要）：`.env`
  - 不存在？复制：`cp .env.example .env`

## 端口检查

- [ ] 端口 8080 未被占用
  ```powershell
  # Windows检查
  Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue
  
  # 如果被占用，可以：
  # 1. 停止占用进程
  # 2. 使用不同端口：--server.port=8081
  ```

- [ ] 端口 3306 未被占用（仅Docker模式需要）
- [ ] 端口 6379 未被占用（仅Docker模式需要）

## 配置检查（可选）

- [ ] OpenAI API密钥已配置（用于LLM验证）
  - 在 `.env` 文件中设置 `OPENAI_API_KEY`
  - 不配置也可以运行（仅使用CoreNLP）

- [ ] MySQL密码已设置（Docker模式）
  - 在 `.env` 文件中设置 `MYSQL_PASSWORD`

## 快速测试

### 1. 启动应用

**最简单方式**（Windows）：
```powershell
java -jar target/sentiment-analysis-0.0.1-SNAPSHOT.jar --spring.cache.type=none
```

**或使用启动脚本**：
- Windows: 双击 `start.bat`
- Mac/Linux: 运行 `./start.sh`

### 2. 验证启动成功

- [ ] 等待20-30秒启动完成
- [ ] 浏览器访问 http://localhost:8080
- [ ] 看到登录页面

### 3. 功能测试

- [ ] 使用 admin/admin123 登录成功
- [ ] 提交一条评论
- [ ] 看到情感分析结果（带表情符号）
- [ ] 查看情感分布图表
- [ ] 访问管理员面板

### 4. API测试（可选）

```bash
# 测试安全分析API
curl http://localhost:8080/api/security/stats
```

## 常见问题快速解决

### ❌ "Docker is not running"
**解决**：启动Docker Desktop，或使用快速模式（纯Java）

### ❌ "Port 8080 already in use"
**解决**：
```powershell
# 查找并停止占用进程
Get-Process -Id (Get-NetTCPConnection -LocalPort 8080).OwningProcess | Stop-Process

# 或使用其他端口
java -jar target/*.jar --server.port=8081
```

### ❌ "java: command not found"
**解决**：安装 Java 17 from https://adoptium.net/

### ❌ "JAR file not found"
**解决**：
```bash
mvn clean package
```

### ❌ "Application fails to start"
**解决**：
1. 检查Java版本（需要17+）
2. 查看完整错误日志
3. 确认端口未被占用
4. 尝试重启终端/命令行

## 成功标志

✅ 看到以下日志表示启动成功：
```
Started SentimentAnalysisApplication in XX seconds
Tomcat started on port 8080 (http) with context path '/'
```

✅ 浏览器能访问 http://localhost:8080

✅ 登录页面正常显示

## 下一步

1. 📖 阅读完整文档：[README.md](README.md)
2. 🧪 运行测试：`mvn test`
3. 🔧 配置生产环境：查看 [README.md#configuration](README.md#-configuration)
4. 📊 了解架构：[PROJECT_SUMMARY.md](PROJECT_SUMMARY.md)

---

**准备好了？** 运行 `start.bat`（Windows）或 `./start.sh`（Mac/Linux）开始！
