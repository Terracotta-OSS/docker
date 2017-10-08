# using the latest OpenJDK 8 update (see https://hub.docker.com/_/openjdk/ for more details)
FROM openjdk:8-jdk-alpine
MAINTAINER Anthony Dahanne <anthony.dahanne@softwareag.com>

# add few utilities, upgrade tar
RUN apk --update add tar openssl ca-certificates bash

# downloading, untarring and removing the archive, link from https://github.com/ehcache/ehcache3/releases
RUN wget -q https://github.com/ehcache/ehcache3/releases/download/v3.4.0/ehcache-clustered-3.4.0-kit.tgz \
  && mkdir /terracotta \
  && tar xvzf ehcache-clustered-3.4.0-kit.tgz -C /terracotta --strip-components=1 \
  && rm ehcache-clustered-3.4.0-kit.tgz

ENV JAVA_HOME /usr/lib/jvm/java-1.8-openjdk/

COPY conf/tc-config-active-passive.xml /terracotta/server/config/tc-config-active-passive.xml
COPY conf/tc-config-single-node.xml /terracotta/server/config/tc-config-single-node.xml
COPY conf/logback.xml /terracotta/server/lib/logback-test.xml
COPY entrypoint.sh /terracotta/entrypoint.sh

# adding the user terracotta, to not run the server as root
RUN addgroup -S terracotta && adduser -h /terracotta -s /bin/bash -G terracotta -S -D terracotta

# all below commands will now be relative to this path
WORKDIR /terracotta/server

# the tsa port (used by the clients to connect to the cluster)
EXPOSE 9410
# the group port (used to sync the passives with the active)
EXPOSE 9430

# default values for offheap, that you can override when starting your container with docker run -e OFFHEAP_RESOURCE1_SIZE=512 for example
ENV OFFHEAP_RESOURCE1_NAME "offheap-1"
ENV OFFHEAP_RESOURCE1_UNIT "MB"
ENV OFFHEAP_RESOURCE1_SIZE "512"

ENV OFFHEAP_RESOURCE2_NAME "offheap-2"
ENV OFFHEAP_RESOURCE2_UNIT "MB"
ENV OFFHEAP_RESOURCE2_SIZE "128"

#ENTRYPOINT ["bin/start-tc-server.sh", "-f"]
#CMD ["config/tc-config-single-node.xml"]

# before starting the terracotta server, we update the tc-config.xml configuration file
ENTRYPOINT /terracotta/entrypoint.sh