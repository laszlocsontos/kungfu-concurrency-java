/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package kungfu.concurrency.threaddump;

import com.google.common.base.Strings;
import com.google.common.io.ByteStreams;
import com.google.common.primitives.Ints;

import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Date;
import java.util.Map;

import kungfu.concurrency.util.CharPool;
import kungfu.concurrency.util.StringPool;

/**
 * @author Tina Tian
 * @author Shuyang Zhou
 */
public class ThreadUtil {

  public static String threadDump() {
    String threadDump = _getThreadDumpFromJstack();

    if (Strings.isNullOrEmpty(threadDump)) {
      threadDump = _getThreadDumpFromStackTrace();
    }

    return "\n\n".concat(threadDump);
  }

  private static String _getThreadDumpFromJstack() {
    try {
      String vendorURL = System.getProperty("java.vendor.url");

      if (!vendorURL.equals("http://java.oracle.com/") &&
        !vendorURL.equals("http://java.sun.com/")) {

        return StringPool.BLANK;
      }

      RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();

      String name = runtimeMXBean.getName();

      if (Strings.isNullOrEmpty(name)) {
        return StringPool.BLANK;
      }

      int pos = name.indexOf(CharPool.AT);

      if (pos == -1) {
        return StringPool.BLANK;
      }

      String pidString = name.substring(0, pos);

      Integer pid = Ints.tryParse(pidString);

      if (pid == null) {
        return StringPool.BLANK;
      }

      Runtime runtime = Runtime.getRuntime();

      String[] cmd = new String[] {"jstack", pidString};

      Process process = runtime.exec(cmd);

      InputStream inputStream = process.getInputStream();

      byte[] bytes = ByteStreams.toByteArray(inputStream);

      return new String(bytes);
    }
    catch (Exception e) {
    }

    return StringPool.BLANK;
  }

  private static String _getThreadDumpFromStackTrace() {
    String jvm =
      System.getProperty("java.vm.name") + " " +
        System.getProperty("java.vm.version");

    StringBuilder sb = new StringBuilder(
      "Full thread dump of " + jvm + " on " + String.valueOf(new Date()) +
        "\n\n");

    Map<Thread, StackTraceElement[]> stackTraces =
      Thread.getAllStackTraces();

    for (Map.Entry<Thread, StackTraceElement[]> entry :
        stackTraces.entrySet()) {

      Thread thread = entry.getKey();
      StackTraceElement[] elements = entry.getValue();

      sb.append(StringPool.QUOTE);
      sb.append(thread.getName());
      sb.append(StringPool.QUOTE);

      if (thread.getThreadGroup() != null) {
        sb.append(StringPool.SPACE);
        sb.append(StringPool.OPEN_PARENTHESIS);
        sb.append(thread.getThreadGroup().getName());
        sb.append(StringPool.CLOSE_PARENTHESIS);
      }

      sb.append(", priority=");
      sb.append(thread.getPriority());
      sb.append(", id=");
      sb.append(thread.getId());
      sb.append(", state=");
      sb.append(thread.getState());
      sb.append("\n");

      for (int i = 0; i < elements.length; i++) {
        sb.append("\t");
        sb.append(elements[i]);
        sb.append("\n");
      }

      sb.append("\n");
    }

    return sb.toString();
  }

}