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

package kungfu.concurrency.threaddump;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author László Csontos
 */
public class IdleThreadPoolTest {

  @Test
  public void dumpIdleThreadPool() throws Exception {
    semaphore.acquire();

    String dumpFile = ThreadUtil.writeThreadDump("idle");

    System.out.println(dumpFile);
  }

  @Before
  public void setUp() throws InterruptedException {
    semaphore = new Semaphore(1);

    semaphore.acquire();

    executorService = Executors.newCachedThreadPool();

    executorService.submit(new Runnable() {

      @Override
      public void run() {
        semaphore.release();
      }

    });
  }

  @After
  public void tearDown() {
    executorService.shutdown();
  }

  private ExecutorService executorService;
  private Semaphore semaphore;

}
