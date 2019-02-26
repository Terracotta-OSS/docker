### Terracotta Server OSS running in a Docker container

#### Before proceeding, make sure to
* have a Docker / Kubernetes set up ready OR
* have installed the latest [Docker Toolbox](https://www.docker.com/docker-toolbox) OR
* have installed [Docker for Mac](https://docs.docker.com/docker-for-mac/) or [Docker For Windows](https://docs.docker.com/docker-for-windows/)


#### Terracotta Server OSS images versions

* [4.3.5](/4.3.5), matches latest BigMemoryMax OSS, available from : http://terracotta.org/downloads/open-source/catalog
* [5.1.1](/5.1.1), matches Ehcache 3.2.3, available from : https://github.com/ehcache/ehcache3/releases
* [5.2.4](/5.2.4), matches Ehcache 3.3.2, available from : https://github.com/ehcache/ehcache3/releases
* [5.3.2](/5.3.2), matches Ehcache 3.4.0, available from : https://github.com/ehcache/ehcache3/releases
* [5.4.3](/5.4.3), matches Ehcache 3.5.3, available from : https://github.com/ehcache/ehcache3/releases
* [5.5.1](/5.5.1), matches Ehcache 3.6.1, available from : https://github.com/ehcache/ehcache3/releases
* [5.6.0](/5.6.0), matches Ehcache 3.7.0, available from : https://github.com/ehcache/ehcache3/releases
[//]: # (needle_version)

__latest tag currently points at ehcache 3.7.0 / Terracotta Server OSS 5.6.0__

#### Kubernetes / Helm instructions

* Look for the kubernetes folder in the latest version folder to have an example Kubernetes manifest file.
* Using Helm ? Go and checkout our [Terracotta OSS Helm chart!](https://github.com/helm/charts/tree/master/stable/terracotta)

#### Important notes

Those instructions are targeted at Docker version 1.13 and onwards - your host needs to have at least 2GB of RAM
Terracotta default ports changed from version 5.3 :
 * tsa port (clients to servers) from 9510 to 9410
 * group port (servers sync.) from 9530 to 9430
