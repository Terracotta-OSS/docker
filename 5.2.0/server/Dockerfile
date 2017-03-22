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

ENV JAVA_HOME /usr/lib/jvm/java-1.8-openjdk/

ADD config/ /terracotta/server/config/

# adding the user terracotta, to not run the server as root
RUN addgroup -S terracotta && adduser -h /terracotta -s /bin/bash -G terracotta -S -D terracotta
RUN chown -R terracotta:terracotta /terracotta
USER terracotta

# all below commands will now be relative to this path
WORKDIR /terracotta/server

# the tsa port (used by the clients to connect to the cluster)
EXPOSE 9510
# the group port (used to sync the passives with the active)
EXPOSE 9530

# default values for offheap, that you can override when starting your container with docker run -e OFFHEAP_RESOURCE1_SIZE=512 for example
ENV OFFHEAP_RESOURCE1_NAME "primary-server-resource"
ENV OFFHEAP_RESOURCE1_UNIT "MB"
ENV OFFHEAP_RESOURCE1_SIZE "128"

ENV OFFHEAP_RESOURCE2_NAME "secondary-server-resource"
ENV OFFHEAP_RESOURCE2_UNIT "MB"
ENV OFFHEAP_RESOURCE2_SIZE "96"

#ENTRYPOINT ["bin/start-tc-server.sh", "-f"]
#CMD ["config/tc-config-single-node.xml"]

# before starting the terracotta server, we update the tc-config.xml configuration file
ENTRYPOINT sed -i -r 's/OFFHEAP_RESOURCE1_NAME/'$OFFHEAP_RESOURCE1_NAME'/; s/OFFHEAP_RESOURCE1_UNIT/'$OFFHEAP_RESOURCE1_UNIT'/; s/OFFHEAP_RESOURCE1_SIZE/'$OFFHEAP_RESOURCE1_SIZE'/' config/tc-config*.xml \
  && sed -i -r 's/OFFHEAP_RESOURCE2_NAME/'$OFFHEAP_RESOURCE2_NAME'/; s/OFFHEAP_RESOURCE2_UNIT/'$OFFHEAP_RESOURCE2_UNIT'/; s/OFFHEAP_RESOURCE2_SIZE/'$OFFHEAP_RESOURCE2_SIZE'/' config/tc-config*.xml \
  && sed -i -r 's/TC_SERVER1/'$TC_SERVER1'/g; s/TC_SERVER2/'$TC_SERVER2'/g' config/tc-config*.xml \
  && if [ \( -n $TC_SERVER1 \) -o  \( -n $TC_SERVER2 \) ]; then bin/start-tc-server.sh -f config/tc-config-active-passive.xml -n $HOSTNAME; else bin/start-tc-server.sh -f config/tc-config-single-node.xml; fi \