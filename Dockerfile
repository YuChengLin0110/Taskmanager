# 以 Eclipse Temurin Java 21 做為基底映像
FROM eclipse-temurin:21-jre-jammy

# 建立 app 資料夾
WORKDIR /app

# 把 jar 檔複製進容器裡 到 /app/TaskmanagerApp.jar
COPY target/taskmanager-0.0.1-SNAPSHOT.jar TaskmanagerApp.jar

# 開放端口
EXPOSE 8080

# 啟動指令
ENTRYPOINT ["java", "-jar", "/app/TaskmanagerApp.jar"]