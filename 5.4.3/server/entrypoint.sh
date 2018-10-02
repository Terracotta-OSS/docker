#!/bin/bash
sed -i -r 's/OFFHEAP_RESOURCE1_NAME/'$OFFHEAP_RESOURCE1_NAME'/; s/OFFHEAP_RESOURCE1_UNIT/'$OFFHEAP_RESOURCE1_UNIT'/; s/OFFHEAP_RESOURCE1_SIZE/'$OFFHEAP_RESOURCE1_SIZE'/' conf/tc-config*.xml \
&& sed -i -r 's/OFFHEAP_RESOURCE2_NAME/'$OFFHEAP_RESOURCE2_NAME'/; s/OFFHEAP_RESOURCE2_UNIT/'$OFFHEAP_RESOURCE2_UNIT'/; s/OFFHEAP_RESOURCE2_SIZE/'$OFFHEAP_RESOURCE2_SIZE'/' conf/tc-config*.xml \
&& sed -i -r 's/TC_SERVER1/'$TC_SERVER1'/g; s/TC_SERVER2/'$TC_SERVER2'/g' conf/tc-config*.xml

chown -R terracotta:terracotta /terracotta/
su terracotta
if [[ -z $TC_SERVER1 || -z $TC_SERVER2 ]];
    then  bin/start-tc-server.sh -f conf/tc-config-single-node.xml;
else bin/start-tc-server.sh -f conf/tc-config-active-passive.xml -n $HOSTNAME;
fi