### Docker image definition for Terracotta Server

#### Introduction

The Terracotta 4.x OSS offering includes the following:

 *  Ehcache 2.x compatibility
 *  Distributed In-Memory Data Management with fault-tolerance via Terracotta Server (1 stripe – active with optional mirror)
 *  In memory off-heap storage - take advantage of all the RAM in your server

The current image is based on the [openjdk:8-jdk-alpine image](https://hub.docker.com/_/openjdk/), and adds [Terracotta 4.3.2 OSS on top of it](http://terracotta.org/downloads/open-source/catalog)

#### How to start your Terracotta Server(s) in Docker containers

##### Quick start : one active node

    docker run --name tc-server -p 9510:9510 -d terracotta/terracotta-server-oss:4.3.2

A quick look at the logs :

    docker logs -f tc-server

Should return some logs ending with :

    [TC] 2015-12-05 04:17:31,299 INFO - Management server started on 0.0.0.0:9540

You can also load [http://DOCKER_HOST:9510/config](http://DOCKER_HOST:9510/config) to see the configuration of your Terracotta Server Array.

It's now ready and waiting for clients !

#### How to build this image

To build this [Dockerfile](https://github.com/Terracotta-OSS/docker/blob/master/4.3.2/server/Dockerfile), clone this [git repository](https://github.com/Terracotta-OSS/docker) and run :

    $ cd 4.3.2/server
    $ docker build -t terracotta-server-oss:4.3.2 .


#### Getting serious : one active, and one passive - multi-host networking

##### Setting up the hosts
We will use the Docker overlay network plugin, introduced in Docker 1.9

Follow [the official overlay network driver documentation](https://docs.docker.com/engine/userguide/networking/get-started-overlay/) until the step 4 (do not apply step 4)

If everything went fine, you should have a small cluster of Docker hosts at your disposal :


````
$ docker-machine ls
NAME          ACTIVE   DRIVER       STATE     URL                         SWARM                DOCKER   ERRORS
dev           -        virtualbox   Running   tcp://192.168.99.103:2376                        v1.9.1
mh-keystore   *        virtualbox   Running   tcp://192.168.99.104:2376                        v1.9.1
mhs-demo0     -        virtualbox   Running   tcp://192.168.99.105:2376   mhs-demo0 (master)   v1.9.1
mhs-demo1     -        virtualbox   Running   tcp://192.168.99.106:2376   mhs-demo0            v1.9.1
````

And also an overlay network ready to be used :

````
docker network ls
NETWORK ID          NAME                DRIVER
[...]
87fde954a83b        my-net              overlay
[...]
````

We would need first to stop mhs-demo0 and mhs-demo1 [to give them some more RAM](http://stackoverflow.com/a/32834453/24069) :

    $ docker-machine stop mhs-demo0 mhs-demo1

And then, in

    ~/.docker/machine/machines/mhs-demo0/config.json

and

    ~/.docker/machine/machines/mhs-demo1/config.json

Adjust from

    "Memory":1024

to

    "Memory":2048

Now you can restart those 2 hosts :

    $ docker-machine start mhs-demo0 mhs-demo1

And also create and start another one :
````
docker-machine create -d virtualbox \
    --virtualbox-memory "2048" \
    --swarm \
    --swarm-discovery="consul://$(docker-machine ip mh-keystore):8500" \
    --engine-opt="cluster-store=consul://$(docker-machine ip mh-keystore):8500" \
    --engine-opt="cluster-advertise=eth1:2376" \
  mhs-demo2
````

Now we should have such a setup :

````
$ docker-machine ls
NAME          ACTIVE   DRIVER       STATE     URL                         SWARM                DOCKER   ERRORS
dev           -        virtualbox   Running   tcp://192.168.99.103:2376                        v1.9.1
mh-keystore   -        virtualbox   Running   tcp://192.168.99.104:2376                        v1.9.1
mhs-demo0     *        virtualbox   Running   tcp://192.168.99.105:2376   mhs-demo0 (master)   v1.9.1
mhs-demo1     -        virtualbox   Running   tcp://192.168.99.106:2376   mhs-demo0            v1.9.1
mhs-demo2     -        virtualbox   Running   tcp://192.168.99.107:2376   mhs-demo0            v1.9.1
````

##### Starting the containers !

    $ docker run --hostname tsa --name tsa -d -e TC_SERVER1=tsa -e TC_SERVER2=tsa2 --net=my-net --env="constraint:node==mhs-demo1" terracotta/terracotta-server-oss:4.3.2
    $ docker run --hostname tsa2 --name tsa2 -d -e TC_SERVER2=tsa2 -e TC_SERVER1=tsa --net=my-net --env="constraint:node==mhs-demo2" terracotta/terracotta-server-oss:4.3.2
    $ docker run --name petclinic -d --net=my-net --env="constraint:node==mhs-demo0" terracotta/sample-ehcache-client:4.3.2

You should end up with something similar to :
````
$ docker ps
CONTAINER ID        IMAGE                                             COMMAND                  CREATED             STATUS              PORTS                           NAMES
8dbf24cfaae3        terracotta/sample-ehcache-client:4.3.2   "mvn tomcat7:run -Dts"   56 seconds ago      Up 55 seconds                                            mhs-demo0/petclinic
b7e10058a909        terracotta/terracotta-server-oss:4.3.2        "/bin/sh -c 'sed -i -"   3 minutes ago       Up 3 minutes        9510/tcp, 9530/tcp, 9540/tcp    mhs-demo1/tsa2
8999a50ef131        terracotta/terracotta-server-oss:4.3.2        "/bin/sh -c 'sed -i -"   9 minutes ago       Up 9 minutes        9510/tcp, 9530/tcp, 9540/tcp    mhs-demo2/tsa
````


Want to make sure it's a cluster ? How about restarting a server ?

    $ docker restart tsa

and then :

    $ docker logs -f tsa

    04:51:08,998  INFO console:90 - Moved to State[ PASSIVE-STANDBY ]
    [TC] 2015-12-15 04:51:08,998 INFO - Moved to State[ PASSIVE-STANDBY ]


Congratulations ! You successfully started an Active / Passive Terracotta Cluster with one client !

