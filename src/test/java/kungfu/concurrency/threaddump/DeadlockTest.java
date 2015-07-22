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

import java.util.concurrent.CyclicBarrier;

import org.junit.Before;
import org.junit.Test;

/**
 * @author László Csontos
 */
public class DeadlockTest extends BaseThreadDumpTest {

  @Test
  public void dumpDeadlock() throws Exception {
    cyclicBarrier.await();

    String dumpFile = ThreadUtil.writeThreadDump("deadlock");

    System.out.println(dumpFile);
  }

  @Before
  public void setUp() throws Exception {
    super.setUp();

    executorService.submit(new Task1());
    executorService.submit(new Task2());
  }

  private final Object lock1 = new Object();
  private final Object lock2 = new Object();

  private final CyclicBarrier cyclicBarrier = new CyclicBarrier(3);

  private class Task1 implements Runnable {

    @Override
    public void run() {
      synchronized (lock1) {
        try {
          cyclicBarrier.await();
        }
        catch (Exception e) {
          e.printStackTrace();
        }

        synchronized (lock2) { }
      }
    }

  }

  private class Task2 implements Runnable {

    @Override
    public void run() {
      synchronized (lock2) {
        try {
          cyclicBarrier.await();
        }
        catch (Exception e) {
          e.printStackTrace();
        }

        synchronized (lock1) { }
      }
    }

  }

}
