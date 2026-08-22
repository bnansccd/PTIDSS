FROM --platform=linux/amd64 openjdk:8
MAINTAINER ZHUQING "574728375@qq.com"
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo "Asia/Shanghai" > /etc/timezone
VOLUME /tmp
ADD target/*.jar auth.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Xmx512m","-Xms512m","-jar","/auth.jar"]