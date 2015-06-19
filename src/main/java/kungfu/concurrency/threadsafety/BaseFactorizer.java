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

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Based on {@link http://javarevisited.blogspot.hu/2014/05/how-to-find-prime-factors-of-integer-number-java.html}
 * 
 * @author Javin Paul
 * @author László Csontos
 *
 */
public class BaseFactorizer implements Factorizer {

  @Override
  public SortedSet<Integer> factor(int number) {
    SortedSet<Integer> primefactors = new TreeSet<>();

    for (int index = 2; index <= number; index++) {
      if (number % index == 0) {
        primefactors.add(index);
        number /= index;
        index--;
      }
    }

    return primefactors;
  }

}
