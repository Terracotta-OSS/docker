# using the latest OpenJDK 8 update (see https://hub.docker.com/_/openjdk/ for more details)
FROM openjdk:8-jdk-alpine
MAINTAINER Anthony Dahanne <anthony.dahanne@softwareag.com>

# add few utilities, upgrade tar
RUN apk --update add tar openssl ca-certificates bash

ENV JAVA_HOME /usr/lib/jvm/java-1.8-openjdk/

# downloading, untarring and removing the archive
RUN wget -q https://d2zwv9pap9ylyd.cloudfront.net/terracotta-4.3.1.tar.gz \
  && mkdir /terracotta \
  && tar xvzf terracotta-4.3.1.tar.gz -C /terracotta --strip-components=1 \
  && rm terracotta-4.3.1.tar.gz


ADD config/tc-config-single-node.xml /terracotta/server/config/
ADD config/tc-config-active-passive.xml /terracotta/server/config/
ADD config/tc.custom.log4j.properties /terracotta/.tc.custom.log4j.properties

# adding the user terracotta, to not run the server as root
RUN addgroup -S terracotta && adduser -h /terracotta -s /bin/bash -G terracotta -S -D terracotta
RUN chown -R terracotta:terracotta /terracotta
USER terracotta

# all below commands will now be relative to this path
WORKDIR /terracotta/server

# the management port
EXPOSE 9540
# the tsa port (used by the clients to connect to the cluster)
EXPOSE 9510
# the group port (used to sync the passives with the active)
EXPOSE 9530

# default values for offheap, that you can override when starting your container with docker run -e OFFHEAP_MAX_SIZE=512g for example
ENV OFFHEAP_ENABLED "true"
ENV OFFHEAP_MAX_SIZE "1g"

# before starting the terracotta server, we update the tc-config.xml configuration file
ENTRYPOINT sed -i -r 's/OFFHEAP_ENABLED/'$OFFHEAP_ENABLED'/; s/OFFHEAP_MAX_SIZE/'$OFFHEAP_MAX_SIZE'/; s/TC_SERVER1/'$TC_SERVER1'/g; s/TC_SERVER2/'$TC_SERVER2'/g' config/tc-config*.xml \
  && if [ \( -n $TC_SERVER1 \) -o  \( -n $TC_SERVER2 \) ]; then bin/start-tc-server.sh -f config/tc-config-active-passive.xml -n $HOSTNAME; else bin/start-tc-server.sh -f config/tc-config-single-node.xml; fi \
