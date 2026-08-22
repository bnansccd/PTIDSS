FROM openjdk:8
MAINTAINER ZHUQING "574728375@qq.com"
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo "Asia/Shanghai" > /etc/timezone
VOLUME /tmp
ADD target/*.jar gen.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Xmx1024m","-Xms1024m","-jar","/gen.jar"]