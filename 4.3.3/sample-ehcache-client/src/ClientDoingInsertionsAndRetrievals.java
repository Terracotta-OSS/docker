import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.Status;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.MemoryUnit;
import net.sf.ehcache.config.TerracottaClientConfiguration;
import net.sf.ehcache.config.TerracottaConfiguration;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;

public class ClientDoingInsertionsAndRetrievals {

  /**
   * Run an ehcache based client, against the Terracotta Server
   *
   */
  public static void main(String[] args) throws IOException {

    String terracottaServerUrl = System.getenv("TERRACOTTA_SERVER_URL");

    if(terracottaServerUrl == null || terracottaServerUrl.trim().equals("")) {
      System.out.println("The environment variable TERRACOTTA_SERVER_URL was not set; using terracotta:9510 as the cluster url.");
      terracottaServerUrl = "terracotta:9510";
    }

    System.out.println("**** Programmatically configure an instance, configured to connect to : " + terracottaServerUrl + " ****");

    Configuration managerConfiguration = new Configuration()
        .name("myCacheManager")
        .terracotta(new TerracottaClientConfiguration().url(terracottaServerUrl))
        .cache(new CacheConfiguration()
            .name("myCache")
            .maxEntriesLocalHeap(50)
            .copyOnRead(true)
            .eternal(true)
            .terracotta(new TerracottaConfiguration())
        );

    CacheManager manager = CacheManager.create(managerConfiguration);
    try {
      Cache myCache = manager.getCache("myCache");
      //myCache is now ready.

      Random random = new Random();
      if (myCache.getSize() > 0) {
        System.out.println("**** We found some data in the cache ! I guess some other client inserted data in BigMemory ! **** ");
      }
      System.out.println("**** Starting inserting / getting elements **** ");
      while (!Thread.currentThread().isInterrupted() && manager.getStatus() == Status.STATUS_ALIVE) {
        // indexes spread between 0 and 999
        int index = random.nextInt(1000);
        if (random.nextInt(10) < 3 && myCache.getSize() < 1000) {
          // put
          String value = new BigInteger(1024 * 128 * (1 + random.nextInt(10)), random).toString(16);
          System.out.println("Inserting at key  " + index + " String of size : " + value.length() + " bytes");
          myCache.put(new Element(index, value)); // construct a big string of 256k data
        } else {
          // get
          Element element = myCache.get(index);
          System.out.println("Getting key  " + index + (element == null ? ", that was a miss" : ", THAT WAS A HIT !"));
        }
        Thread.sleep(100);

      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      if (manager != null) {
        manager.shutdown();
      }
    }
  }

}
