### Terracotta Server 4.3.5 Docker images
Instructions to build and execute are in those 2 sub folders :

 1. [server](/server/) : instructions to build and execute a Terracotta Server Array in Docker containers, according to several deployment scenarios
 2. [sample-ehcache-client](sample-ehcache-client) : instructions to build and execute a Java app connecting to a Terracotta Server Array, in a Docker container

It is strongly suggested that you follow the steps in the above order.
In each folder, you'll find a README document explaining you how to create a Docker image, how to spawn it as a Docker container, with various deployment scenarios

### Orchestration example with Docker compose

We've also provided a docker-compose.yml file, to allow you to run a 2 nodes (1 active, 1 passive) Terracotta cluster, with a client connected to it.

Just run :

    docker-compose up -d
    
All services (client, terracotta-1, terracotta-2) should be running :
   
    docker-compose ps
           Name                     Command               State                      Ports                    
    ---------------------------------------------------------------------------------------------------------
    431_client_1         java ClientDoingInsertions ...   Up                                                  
    431_terracotta-1_1   /bin/sh -c sed -i -r 's/OF ...   Up      9510/tcp, 9530/tcp, 0.0.0.0:9540->9540/tcp  
    431_terracotta-2_1   /bin/sh -c sed -i -r 's/OF ...   Up      9510/tcp, 9530/tcp, 0.0.0.0:19540->9540/tcp 

You can scale the clients :

    docker-compose scale client=5
    Creating and starting 431_client_2 ... done
    Creating and starting 431_client_3 ... done
    Creating and starting 431_client_4 ... done
    Creating and starting 431_client_5 ... done
    
Have a look at what they're up to :

    docker-compose logs -f client
    
And also have a look at the current topology, using the management REST api :

    http://DOCKER_HOST:9540/tc-management-api/v2/agents/topologies
    
    
### Orchestration example with Docker stack

If you're using Docker swarm mode, you may want to use docker stack to deploy across all nodes part of your Docker swarm

You can re use the same docker-compose.yml file :

    docker stack deploy terracotta --compose-file docker-compose.yml
    Creating network terracotta_terracotta-net
    Creating service terracotta_terracotta-1
    Creating service terracotta_terracotta-2
    Creating service terracotta_client

    $ docker stack ps terracotta
    ID            NAME                       IMAGE                                   NODE  DESIRED STATE  CURRENT STATE           ERROR  PORTS
    i88gj5reok4x  terracotta_client.1        terracotta/sample-ehcache-client:4.3.5  moby  Running        Running 23 seconds ago
    hrzyj5z2uwkh  terracotta_terracotta-2.1  terracotta/terracotta-server-oss:4.3.5  moby  Running        Running 24 seconds ago
    944cftq26cis  terracotta_terracotta-1.1  terracotta/terracotta-server-oss:4.3.5  moby  Running        Running 25 seconds ago

You want to scale, right ? 

    docker service scale terracotta_client=4
    terracotta_client scaled to 4

Wondering what those services are up to ?

    docker service logs -f terracotta_client
    
And also you can have a look at the current topology, using the management REST api :

    http://DOCKER_HOST:9540/tc-management-api/v2/agents/topologies
    
    
     