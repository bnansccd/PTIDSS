# ============================================================
# PTIDSS 后端镜像（生产，V3.2 自包含部署）
# 构建：docker compose build（context=./server，仅含 jar，离线可构建）
# 运行配置：环境变量见 application-prod.yml（DB_HOST/TOKEN_SECRET 等）
# ============================================================
FROM eclipse-temurin:21-jre

LABEL maintainer="PTIDSS Team" description="电力交易智能辅助决策系统后端"

ENV TZ=Asia/Shanghai \
    LANG=C.UTF-8

# 健康检查依赖 wget（temurin jammy 基础镜像不含，显式安装）
RUN apt-get update && apt-get install -y --no-install-recommends wget \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY ptidss-server.jar /app/app.jar

EXPOSE 9080

HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
  CMD ["sh", "-c", "wget -q -O /dev/null http://127.0.0.1:9080/ptidss/auth/captcha || exit 1"]

ENTRYPOINT ["java", "-Xms512m", "-Xmx1024m", \
  "-jar", "/app/app.jar", "--spring.profiles.active=prod"]
