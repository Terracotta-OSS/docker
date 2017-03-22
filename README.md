### Terracotta Server OSS running in a Docker container

#### Before proceeding, make sure to
* have installed the latest [Docker Toolbox](https://www.docker.com/docker-toolbox) OR
* have installed [Docker for Mac](https://docs.docker.com/docker-for-mac/) or [Docker For Windows](https://docs.docker.com/docker-for-windows/)


#### Terracotta Server OSS images versions

* [4.3.1](/4.3.1)
* [4.3.2](/4.3.2)
* [4.3.3](/4.3.3)
* [5.1.1](/5.1.1)
* [5.2.0](/5.2.0)

#### Important notes

Those instructions are targeted at Docker version 1.13 and onwards.

### Using Docker Machine
You will need a host with at least 2GB of memory

    docker-machine create --driver virtualbox --virtualbox-memory "2048" dev

````
$ docker-machine ls
NAME   ACTIVE   DRIVER       STATE     URL                         SWARM   DOCKER   ERRORS
dev    -        virtualbox   Running   tcp://192.168.99.103:2376           v1.12
````

so in my case, my DOCKER_HOST is reachable at 192.168.99.103