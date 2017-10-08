### Terracotta Server OSS running in a Docker container

#### Before proceeding, make sure to
* have a Docker / Kubernetes set up ready OR
* have installed the latest [Docker Toolbox](https://www.docker.com/docker-toolbox) OR
* have installed [Docker for Mac](https://docs.docker.com/docker-for-mac/) or [Docker For Windows](https://docs.docker.com/docker-for-windows/)


#### Terracotta Server OSS images versions

* [4.3.4](/4.3.4), matches latest BigMemoryMax OSS, available from : http://terracotta.org/downloads/open-source/catalog
* [5.1.1](/5.1.1), matches Ehcache 3.2.3, available from : https://github.com/ehcache/ehcache3/releases
* [5.2.4](/5.2.4), matches Ehcache 3.3.2, available from : https://github.com/ehcache/ehcache3/releases
* [5.3.2](/5.3.2), matches Ehcache 3.4.0, available from : https://github.com/ehcache/ehcache3/releases

#### Important notes

Those instructions are targeted at Docker version 1.13 and onwards - your host needs to have at least 2GB of RAM