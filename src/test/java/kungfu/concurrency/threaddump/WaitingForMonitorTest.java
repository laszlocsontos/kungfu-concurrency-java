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

import java.util.concurrent.Semaphore;

import org.junit.Before;
import org.junit.Test;

/**
 * @author László Csontos
 */
public class WaitingForMonitorTest extends BaseThreadDumpTest {

  @Test
  public void dumpWaitingForMonitor() throws Exception {
    readySemaphore.acquire();

    executorService.submit(new Runnable() {

      @Override
      public void run() {
        try {
          readySemaphore.acquire();
        }
        catch (InterruptedException ie) {
          ie.printStackTrace();
        }

        synchronized (lock) { }
      }

    });

    readySemaphore.release();

    String dumpFile = ThreadUtil.writeThreadDump("monitor");

    System.out.println(dumpFile);
  }

  @Before
  public void setUp() throws InterruptedException {
    super.setUp();

    blockingSemaphore.acquire();
    readySemaphore.acquire();

    executorService.submit(new Runnable() {

      @Override
      public void run() {
        synchronized (lock) {
          try {
            readySemaphore.release();
            blockingSemaphore.acquire();
          }
          catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }

    });
  }


  private final Object lock = new Object();

  private final Semaphore blockingSemaphore = new Semaphore(1);
  private final Semaphore readySemaphore = new Semaphore(1);

}
