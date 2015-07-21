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

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author László Csontos
 */
public class WaitForReentrantLockTest extends BaseWaitingTest {

  protected BlockedRunnable getBlockedRunnable() throws InterruptedException {
    return new BlockedRunnable() {

      @Override
      public void run() {
        super.run();

        lock.lock();
      }

    };
  }

  protected BlockerRunnable getBlockerRunnable() throws InterruptedException {
    return new BlockerRunnable() {

      @Override
      public void run() {
        lock.lock();

        super.run();
      }

    };
  }

  private final Lock lock = new ReentrantLock();

}
