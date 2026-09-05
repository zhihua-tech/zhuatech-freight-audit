# 部署、初始化与恢复

上海如静知华信息科技有限公司 · https://www.zhuatech.cn/

## 本机演示

```bash
docker compose --env-file .env.demo.example up --build -d
docker compose --env-file .env.demo.example logs --tail=100 backend
```

访问 http://localhost:8088 ，后端健康检查 http://localhost:18080/api/health 。默认仅绑定 127.0.0.1。可在环境文件修改 WEB_PORT / API_PORT，避免多项目并行时端口冲突。演示数据与所有修改持久保存在 MySQL 数据卷。

## 正式环境

先取得商业授权。将 .env.example 复制为不提交版本库的 .env，填写 6 项强密码（应用账号至少 12 位、不超过 72 个 UTF-8 字节），设置 DEMO_MODE=false。使用全新数据库，不要将演示数据库转为生产库。

```bash
cp .env.example .env
# 手工填写密码后运行
docker compose up --build -d
```

初始化账号只在不存在时创建。更改环境中的初始化密码不会更改已存在账号；请在管理端重置密码。生产还应配置 HTTPS、可信域名、反向代理、最小数据库权限、网络隔离、日志监控与备份。后端进程以非 root 用户运行，业务数据库不对宿主机暴露端口。

## 本地开发

Java 21、Maven 3.9+、Node.js 22+、MySQL 8.4。配置 DB_URL、DB_USER、DB_PASSWORD、ADMIN_PASSWORD、REVIEWER_PASSWORD、OPERATOR_PASSWORD、VIEWER_PASSWORD 后启动后端；前端开发代理默认 http://127.0.0.1:8080，可用 API_TARGET 修改。

```bash
cd backend
mvn spring-boot:run
# 另一终端
cd frontend
npm ci --ignore-scripts
npm run dev
```

## 测试数据库

`sh scripts/test.sh` 默认使用内存 H2。真实 MySQL 测试时仅连接专用、全新测试数据库，配置 TEST_DB_URL、TEST_DB_USER、TEST_DB_PASSWORD 并执行 `mvn test`。不要把测试配置指向生产库；测试会创建虚构业务数据和账号。CI 已配置一次性 MySQL 服务。

## 备份和恢复

业务记录、文件内容、角色与审计都在 MySQL 中。请使用企业既有的 MySQL 备份方式，定期完成全量备份与恢复演练；备份文件包含业务数据，不能上传代码仓库。恢复到隔离环境后检查 /api/health、账号登录、各模块数量、附件摘要与一条完整业务流程。更新前先备份，再执行容器构建；Flyway 校验已应用的迁移，禁止直接修改已上线迁移脚本。

停止服务可使用 `docker compose down`，不会删除命名数据卷。不要随意添加 `-v`，否则会删除业务数据。
