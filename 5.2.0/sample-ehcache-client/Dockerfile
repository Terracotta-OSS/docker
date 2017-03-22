# using the latest OpenJDK 8 update (see https://hub.docker.com/_/openjdk/ for more details)
FROM openjdk:8-jdk-alpine
MAINTAINER Anthony Dahanne <anthony.dahanne@softwareag.com>

# add few utilities, upgrade tar
RUN apk --update add tar openssl ca-certificates bash

# downloading, untarring and removing the archive, link from https://github.com/ehcache/ehcache3/releases
RUN wget -q https://github.com/ehcache/ehcache3/releases/download/v3.3.0/ehcache-clustered-3.3.0-kit.tgz \
  && mkdir /terracotta \
  && tar xvzf ehcache-clustered-3.3.0-kit.tgz -C /terracotta --strip-components=1 \
  && rm ehcache-clustered-3.3.0-kit.tgz

ADD https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/1.7.7/slf4j-simple-1.7.7.jar /terracotta/client/lib/slf4j-simple-1.7.7.jar

ENV JAVA_HOME /usr/lib/jvm/java-1.8-openjdk/

COPY ./src/ /terracotta/

# adding the user terracotta, to not run the server as root
RUN addgroup -S terracotta && adduser -h /terracotta -s /bin/bash -G terracotta -S -D terracotta
RUN chown -R terracotta:terracotta /terracotta
USER terracotta

# This is the current working directory
WORKDIR /terracotta/

ENV CLASSPATH ".:/terracotta/client/ehcache/*:/terracotta/client/lib/*"

#ENV TERRACOTTA_SERVER_URL

RUN javac ClientDoingInsertionsAndRetrievals.java

ENTRYPOINT ["java", "ClientDoingInsertionsAndRetrievals"]