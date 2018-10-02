/*
 * Copyright (c) 2011-2017 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, and/or its subsidiaries and/or its affiliates and/or their licensors.
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided for in your License Agreement with Software AG.
 */

import org.ehcache.Cache;
import org.ehcache.PersistentCacheManager;
import org.ehcache.Status;
import org.ehcache.clustered.client.config.builders.ClusteredResourcePoolBuilder;
import org.ehcache.clustered.client.config.builders.ClusteringServiceConfigurationBuilder;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.net.URI;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Run an ehcache3 based client, against the Terracotta Server
 */
public class ClientDoingInsertionsAndRetrievals {

  private static final Logger LOGGER = LoggerFactory.getLogger(ClientDoingInsertionsAndRetrievals.class);
  private static final String CACHE_MANAGER_ENTITY_NAME = "my-application";
  private static final String[] TAGS = new String[]{"container", "ehcache"};

  public static void main(String[] args) {
    String terracottaServerUrl = System.getenv("TERRACOTTA_SERVER_URL");
    String offheap = System.getenv("OFFHEAP_RESOURCE1_NAME");

    if (terracottaServerUrl == null || terracottaServerUrl.trim().equals("")) {
      LOGGER.warn("The environment variable TERRACOTTA_SERVER_URL was not set; using terracotta://terracotta as the cluster url.");
      terracottaServerUrl = "terracotta";
    }
    URI clusterUri = URI.create("terracotta://" + terracottaServerUrl + "/");

    ClientDoingInsertionsAndRetrievals clientDoingInsertionsAndRetrievals = new ClientDoingInsertionsAndRetrievals();
    clientDoingInsertionsAndRetrievals.start(clusterUri, offheap, CACHE_MANAGER_ENTITY_NAME);
  }

  private void start(URI clusterUri, String offheap, String cacheManagerEntityName) {

    LOGGER.info("**** Programmatically configure an instance, configured to connect to : " + clusterUri + " ****");

    // from http://www.ehcache.org/documentation/3.3/clustered-cache.html
    final CacheManagerBuilder<PersistentCacheManager> clusteredCacheManagerBuilder =
        CacheManagerBuilder.newCacheManagerBuilder()
            .with(ClusteringServiceConfigurationBuilder.cluster(clusterUri.resolve(cacheManagerEntityName))
                .autoCreate()
                .defaultServerResource(offheap)
                .resourcePool("resource-pool-b", 32, MemoryUnit.MB, offheap))
            .withCache("clustered-cache", CacheConfigurationBuilder.newCacheConfigurationBuilder(Integer.class, String.class,
                ResourcePoolsBuilder.newResourcePoolsBuilder()
                    .with(ClusteredResourcePoolBuilder.clusteredDedicated(offheap, 32, MemoryUnit.MB))))
            .withCache("shared-cache-1", CacheConfigurationBuilder.newCacheConfigurationBuilder(Integer.class, String.class,
                ResourcePoolsBuilder.newResourcePoolsBuilder()
                    .with(ClusteredResourcePoolBuilder.clusteredShared("resource-pool-b"))))
            .withCache("shared-cache-2", CacheConfigurationBuilder.newCacheConfigurationBuilder(Integer.class, String.class,
                ResourcePoolsBuilder.newResourcePoolsBuilder()
                    .with(ClusteredResourcePoolBuilder.clusteredShared("resource-pool-b"))));

    final PersistentCacheManager cacheManager = clusteredCacheManagerBuilder.build(true);

    try {
      Cache<Integer, String> clusteredCache = cacheManager.getCache("clustered-cache", Integer.class, String.class);
      Cache<Integer, String> sharedCache1 = cacheManager.getCache("shared-cache-1", Integer.class, String.class);
      Cache<Integer, String> sharedCache2 = cacheManager.getCache("shared-cache-2", Integer.class, String.class);


      Random random = new Random();
      LOGGER.info("**** Starting inserting / getting elements **** ");
      while (!Thread.currentThread().isInterrupted() && cacheManager.getStatus() == Status.AVAILABLE) {
        // indexes spread between 0 and 999
        int index = random.nextInt(1000);
        if (random.nextInt(10) < 3) {
          // put
          String value = new BigInteger(1024 * 128 * (1 + random.nextInt(10)), random).toString(16);
          LOGGER.info("Inserting at key  " + index + " String of size : " + value.length() + " bytes");
          clusteredCache.put(index, value); // construct a big string of 256k data
          sharedCache1.put(index, value);
          sharedCache2.put(index, value);
        } else {
          // get
          String elementFromClusteredCache = clusteredCache.get(index);
          sharedCache1.get(index);
          sharedCache2.get(index);
          LOGGER.info("Getting key  " + index + (elementFromClusteredCache == null ? ", that was a miss" : ", THAT WAS A HIT !"));
        }
        TimeUnit.MILLISECONDS.sleep(100);
      }
    } catch (InterruptedException e) {
      LOGGER.error("Interrupted !", e);
    } finally {
      if (cacheManager != null) {
        cacheManager.close();
      }
    }
  }

}
