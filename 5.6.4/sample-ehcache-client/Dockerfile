# using the latest OpenJDK 8 update (see https://hub.docker.com/_/openjdk/ for more details)
FROM openjdk:8-jdk-alpine
LABEL maintainers="Anthony Dahanne <anthony.dahanne@softwareag.com>"

# add few utilities, upgrade tar
RUN apk --update add tar openssl ca-certificates bash

# downloading, untarring and removing the archive, link from https://github.com/ehcache/ehcache3/releases
RUN wget -q https://repo1.maven.org/maven2/org/ehcache/ehcache-clustered/3.8.0/ehcache-clustered-3.8.0-kit.tgz \
  && mkdir /terracotta \
  && tar xvzf ehcache-clustered-3.8.0-kit.tgz -C /terracotta --strip-components=1 \
  && rm ehcache-clustered-3.8.0-kit.tgz

ADD https://repo1.maven.org/maven2/ch/qos/logback/logback-classic/1.2.3/logback-classic-1.2.3.jar /terracotta/client/lib/logback-classic-1.2.3.jar
ADD https://repo1.maven.org/maven2/ch/qos/logback/logback-core/1.2.3/logback-core-1.2.3.jar /terracotta/client/lib/logback-core-1.2.3.jar

COPY ./src/ /terracotta/
COPY logback.xml /terracotta/logback.xml

# adding the user terracotta, to not run the server as root
RUN addgroup -S terracotta && adduser -h /terracotta -s /bin/bash -G terracotta -S -D terracotta
RUN chown -R terracotta:terracotta /terracotta
USER terracotta

# This is the current working directory
WORKDIR /terracotta/

ENV CLASSPATH ".:/terracotta/client/ehcache/*:/terracotta/client/lib/*"
ENV OFFHEAP_RESOURCE1_NAME "offheap-1"
ENV TERRACOTTA_SERVER_URL=terracotta:9410

RUN javac ClientDoingInsertionsAndRetrievals.java

ENTRYPOINT ["java", "-Dlogback.configurationFile=/terracotta/logback.xml", "ClientDoingInsertionsAndRetrievals"]