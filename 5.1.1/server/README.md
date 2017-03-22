### Docker image definition for Terracotta Server

#### Introduction

The Terracotta 5.x OSS offering includes the following:

 *  Ehcache 3.x compatibility
 *  Distributed In-Memory Data Management with fault-tolerance via Terracotta Server (1 stripe – active with optional mirror)
 *  In memory off-heap storage - take advantage of all the RAM in your server

The current image is based on the [openjdk:8-jdk-alpine image](https://hub.docker.com/_/openjdk/), and adds [Terracotta 5.1.1 OSS on top of it](http://terracotta.org/downloads/open-source/catalog)

#### How to start your Terracotta Server(s) in Docker containers

##### Quick start : one active node

    docker run --name tc-server -p 9510:9510 -d terracotta/terracotta-server-oss:5.1.1

A quick look at the logs :

    docker logs -f tc-server

Should return some logs ending with :

    [TC] 2017-03-22 03:39:26,627 INFO - Terracotta Server instance has started up as ACTIVE node on 0:0:0:0:0:0:0:0:9510 successfully, and is now ready for work.

It's now ready and waiting for clients !

#### How to build this image

To build this [Dockerfile](https://github.com/Terracotta-OSS/docker/blob/master/5.1.1/server/Dockerfile), clone this [git repository](https://github.com/Terracotta-OSS/docker) and run :

    $ cd 5.1.1/server
    $ docker build -t terracotta-server-oss:5.1.1 .