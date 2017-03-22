import org.ehcache.Cache;
import org.ehcache.PersistentCacheManager;
import org.ehcache.Status;
import org.ehcache.clustered.client.config.builders.ClusteredResourcePoolBuilder;
import org.ehcache.clustered.client.config.builders.ClusteringServiceConfigurationBuilder;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.util.Random;

public class ClientDoingInsertionsAndRetrievals {

  /**
   * Run an ehcache3 based client, against the Terracotta Server
   *
   */
  public static void main(String[] args) throws IOException {

    String terracottaServerUrl = System.getenv("TERRACOTTA_SERVER_URL");

    if(terracottaServerUrl == null || terracottaServerUrl.trim().equals("")) {
      System.out.println("The environment variable TERRACOTTA_SERVER_URL was not set; using terracotta:9510 as the cluster url.");
      terracottaServerUrl = "terracotta:9510";
    }

    URI clusterUri = URI.create("terracotta://" + terracottaServerUrl + "/my-application");
    System.out.println("**** Programmatically configure an instance, configured to connect to : " + clusterUri + " ****");

    // from http://www.ehcache.org/documentation/3.3/clustered-cache.html
    final CacheManagerBuilder<PersistentCacheManager> clusteredCacheManagerBuilder =
        CacheManagerBuilder.newCacheManagerBuilder()
            .with(ClusteringServiceConfigurationBuilder.cluster(clusterUri).autoCreate()
                .defaultServerResource("primary-server-resource")
                .resourcePool("resource-pool-a", 28, MemoryUnit.MB, "secondary-server-resource")
                .resourcePool("resource-pool-b", 32, MemoryUnit.MB))
            .withCache("clustered-cache", CacheConfigurationBuilder.newCacheConfigurationBuilder(Integer.class, String.class,
                ResourcePoolsBuilder.newResourcePoolsBuilder()
                    .with(ClusteredResourcePoolBuilder.clusteredDedicated("primary-server-resource", 32, MemoryUnit.MB))))
            .withCache("shared-cache-1", CacheConfigurationBuilder.newCacheConfigurationBuilder(Integer.class, String.class,
                ResourcePoolsBuilder.newResourcePoolsBuilder()
                    .with(ClusteredResourcePoolBuilder.clusteredShared("resource-pool-a"))))
            .withCache("shared-cache-2", CacheConfigurationBuilder.newCacheConfigurationBuilder(Integer.class, String.class,
                ResourcePoolsBuilder.newResourcePoolsBuilder()
                    .with(ClusteredResourcePoolBuilder.clusteredShared("resource-pool-a"))));
    final PersistentCacheManager cacheManager = clusteredCacheManagerBuilder.build(true);
    try {
      Cache<Integer, String> clusteredCache = cacheManager.getCache("clustered-cache", Integer.class, String.class);
      Cache<Integer, String> sharedCache1 = cacheManager.getCache("shared-cache-1", Integer.class, String.class);
      Cache<Integer, String> sharedCache2 = cacheManager.getCache("shared-cache-2", Integer.class, String.class);


      Random random = new Random();
      System.out.println("**** Starting inserting / getting elements **** ");
      while (!Thread.currentThread().isInterrupted() && cacheManager.getStatus() == Status.AVAILABLE) {
        // indexes spread between 0 and 999
        int index = random.nextInt(1000);
        if (random.nextInt(10) < 3) {
          // put
          String value = new BigInteger(1024 * 128 * (1 + random.nextInt(10)), random).toString(16);
          System.out.println("Inserting at key  " + index + " String of size : " + value.length() + " bytes");
          clusteredCache.put(index, value); // construct a big string of 256k data
          sharedCache1.put(index, value);
          sharedCache2.put(index, value);
        } else {
          // get
          String elementFromClusteredCache = clusteredCache.get(index);
          sharedCache1.get(index);
          sharedCache2.get(index);

          System.out.println("Getting key  " + index + (elementFromClusteredCache == null ? ", that was a miss" : ", THAT WAS A HIT !"));
        }
        Thread.sleep(100);

      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      if (cacheManager != null) {
        cacheManager.close();
      }
    }
  }

}
