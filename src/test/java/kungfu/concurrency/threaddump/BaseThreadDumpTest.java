package kungfu.concurrency.threaddump;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.After;
import org.junit.Before;

public class BaseThreadDumpTest {

  @Before
  public void setUp() throws InterruptedException {
    executorService = Executors.newCachedThreadPool();
  }

  @After
  public void tearDown() {
    executorService.shutdown();
  }

  protected ExecutorService executorService;

}