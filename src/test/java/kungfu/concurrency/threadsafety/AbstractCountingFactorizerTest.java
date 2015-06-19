/*
 * Copyright (C) 2015 - present, Laszlo Csontos
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package kungfu.concurrency.threadsafety;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author László Csontos
 */
public abstract class AbstractCountingFactorizerTest {

  @Before
  public void setUp() {
    countDownLatch = new CountDownLatch(NUM_REQUESTS);
    factorizer = getFactorizer();
    executor = Executors.newFixedThreadPool(NUM_THREADS);
  }

  @After
  public void tearDown() {
    executor.shutdown();
  }

  @Test
  public void testFactor() throws Exception {
    for (int index = 0; index < NUM_REQUESTS; index++) {
      final int number = index;

      executor.submit(new Runnable() {

        @Override
        public void run() {
          try {
            factorizer.factor(number);
          } finally {
            countDownLatch.countDown();
          }
        }

      });
    }

    countDownLatch.await();

    long count = factorizer.getCount();

    Assert.assertEquals(NUM_REQUESTS, count);
  }

  protected abstract CountingFactorizer getFactorizer();

  private static int NUM_REQUESTS = 1000;
  private static int NUM_THREADS = Runtime.getRuntime().availableProcessors();

  private CountDownLatch countDownLatch;
  private CountingFactorizer factorizer;
  private ExecutorService executor;

}
