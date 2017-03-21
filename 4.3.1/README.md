### Terracotta Server 4.3.1 Docker images
Instructions to build and execute are in those 2 sub folders :

 1. [server](/server/) : instructions to build and execute a Terracotta Server Array in Docker containers, according to several deployment scenarios
 2. [sample-ehcache-client](sample-ehcache-client) : instructions to build and execute a Java app connecting to a Terracotta Server Array, in a Docker container

It is strongly suggested that you follow the steps in the above order.
In each folder, you'll find a README document explaining you how to create a Docker image, how to spawn it as a Docker container, with various deployment scenarios

### Orchestration example with Docker compose

We've also provided a docker-compose.yml file, to allow you to run a 2 nodes (1 active, 1 passive) Terracotta cluster, with a client connected to it.

Just run :

    docker-compose up -d
    
All services (client, terracotta_1, terracotta_2) should be running :
   
    docker-compose ps
           Name                     Command               State                      Ports                    
    ---------------------------------------------------------------------------------------------------------
    431_client_1         java ClientDoingInsertions ...   Up                                                  
    431_terracotta_1_1   /bin/sh -c sed -i -r 's/OF ...   Up      9510/tcp, 9530/tcp, 0.0.0.0:9540->9540/tcp  
    431_terracotta_2_1   /bin/sh -c sed -i -r 's/OF ...   Up      9510/tcp, 9530/tcp, 0.0.0.0:19540->9540/tcp 

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