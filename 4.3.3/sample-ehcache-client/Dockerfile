# using the latest OpenJDK 8 update (see https://hub.docker.com/_/openjdk/ for more details)
FROM openjdk:8-jdk-alpine
MAINTAINER Anthony Dahanne <anthony.dahanne@softwareag.com>

# add few utilities, upgrade tar
RUN apk --update add tar openssl ca-certificates bash

ENV JAVA_HOME /usr/lib/jvm/java-1.8-openjdk/

# downloading, untarring and removing the archive
RUN wget -q https://d2zwv9pap9ylyd.cloudfront.net/terracotta-4.3.3.tar.gz \
  && mkdir /terracotta \
  && tar xvzf terracotta-4.3.3.tar.gz -C /terracotta --strip-components=1 \
  && rm terracotta-4.3.3.tar.gz

COPY ./src/ /terracotta/

# adding the user terracotta, to not run the server as root
RUN addgroup -S terracotta && adduser -h /terracotta -s /bin/bash -G terracotta -S -D terracotta
RUN chown -R terracotta:terracotta /terracotta
USER terracotta

# This is the current working directory
WORKDIR /terracotta/

ENV CLASSPATH ".:/terracotta/apis/ehcache-terracotta-client-all.jar\
:/terracotta/apis/ehcache/lib/\
:/terracotta/apis/toolkit/lib/\
:/terracotta/code-samples/lib/slf4j-jdk14-1.7.7.jar"

#ENV TERRACOTTA_SERVER_URL

RUN javac ClientDoingInsertionsAndRetrievals.java

ENTRYPOINT ["java", "ClientDoingInsertionsAndRetrievals"]