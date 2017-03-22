#What is the sample ehcache client ?

Terracotta Server supports a distributed in-memory data-storage topology, which enables the sharing of data among multiple caches and in-memory

The client java app connects to the Terracotta Server and starts putting and getting key/value mappings.

You can have a look at its code at : src/ClientDoingInsertionsAndRetrievals.java ; it's configured to hold few elements (50) on heap, and of course use the Terracotta server at terracotta:9510 as its clustered tier.

The client will either insert or retrieve values every 0.1 seconds


#How to use this image: QuickStart

You can start it up simply with :

    docker run --name client -d terracotta/sample-ehcache-client:4.3.2

But you would get such an error message :
    
    The environment variable TERRACOTTA_SERVER_URL was not set; using terracotta:9510 as the cluster url.

followed by :

    WARN - We couldn't load configuration data from the server at 'terracotta:9510'; retrying. (Error: terracotta.)


That would be because you need a Terracotta server running.

You can run a terracotta server using (provided you built the terracotta image) :

    docker run -d -p 9510:9510 -p 9540:9540 --name terracotta terracotta/terracotta-server-oss:4.3.2

and then re try running the client, with :

    docker run -d --link terracotta:terracotta --name client terracotta/sample-ehcache-client:4.3.2
    
and checkout what's happening with :

    docker logs -f client

You can also have a look at the metrics, load from your browser :

 * [client metrics REST response](http://localhost:9540/tc-management-api/v2/agents/cacheManagers/caches?show=CacheHitRatio&show=CacheHitRate&show=CacheMissRate&show=Size&show=LocalHeapSize&show=LocalHeapSize&show=AverageGetTime)
 * [server metrics REST response](http://localhost:9540/tc-management-api/v2/agents/statistics/servers)




#### How to build this image

To build this [Dockerfile](https://github.com/Terracotta-OSS/docker/blob/master/4.3.2/server/Dockerfile), clone this [git repository](https://github.com/Terracotta-OSS/docker) and run :

    $ cd 4.3.2/sample-ehcache-client
    $ docker build -t sample-ehcache-client:4.3.2 .