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

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

import org.h2.jdbcx.JdbcDataSource;
import org.h2.tools.Server;
import org.junit.Before;
import org.junit.Test;

/**
 * @author László Csontos
 */
public class DatabaseLockTest extends BaseThreadDumpTest {

  @Test
  public void dumpDatabaseLock() throws Exception {
    CyclicBarrier cyclicBarrier = new CyclicBarrier(3);

    final BlockedSQLExecutor sqlExecutor1 = new BlockedSQLExecutor(cyclicBarrier);
    final BlockedSQLExecutor sqlExecutor2 = new BlockedSQLExecutor(cyclicBarrier);

    executorService.submit(new Runnable() {

      @Override
      public void run() {
        try {
          sqlExecutor1.runSQL("SELECT * FROM test FOR UPDATE");
        } catch (SQLException sqle) {
          sqle.printStackTrace();
        }
      }

    });

    executorService.submit(new Runnable() {

      @Override
      public void run() {
        try {
          sqlExecutor2.runSQL("SELECT * FROM test FOR UPDATE");
        } catch (SQLException sqle) {
          sqle.printStackTrace();
        }
      }

    });

    cyclicBarrier.await();

    String dumpFile = ThreadUtil.writeThreadDump("dblock");

    System.out.println(dumpFile);

    sqlExecutor1.release();
    sqlExecutor2.release();
  }

  @Before
  public void setUp() throws Exception {
    super.setUp();

    server = Server.createTcpServer();
    server.start();

    databaseDir = Files.createTempDirectory("testdb");

    System.out.println(databaseDir);

    dataSource = new JdbcDataSource();

    dataSource.setUrl("jdbc:h2:tcp://localhost/" + databaseDir.toString());
    dataSource.setUser("sa");
    dataSource.setPassword("sa");

    runSQL("CREATE TABLE test(col INTEGER)");
    runSQL("INSERT INTO test VALUES(1)");
  }

  @Override
  public void tearDown() throws Exception {
    super.tearDown();

    server.shutdown();

    Files.deleteIfExists(databaseDir);
  }

  protected void runSQL(String sql) throws SQLException {
    new SimpleSQLExecutor().runSQL(sql);
  }

  private Path databaseDir;
  private JdbcDataSource dataSource;
  private Server server;

  private abstract class SQLExecutor {

    abstract void processResultSet(ResultSet resultSet) throws SQLException;

    void runSQL(String sql) throws SQLException {
      Connection connection = null;
      Statement statement = null;
      ResultSet resultSet = null;

      try {
        connection = dataSource.getConnection();

        connection.setAutoCommit(false);

        statement = connection.createStatement();

        if (statement.execute(sql)) {
          resultSet = statement.getResultSet();
        }

        processResultSet(resultSet);
      } catch (SQLException sqle) {
        if (connection != null) {
          connection.rollback();
        }

        sqle.printStackTrace();
      } finally {
        if (resultSet != null) {
          resultSet.close();
        }

        if (statement != null) {
          statement.close();
        }

        if (connection != null) {
          connection.commit();
          connection.close();
        }
      }
    }

  }

  private class BlockedSQLExecutor extends SQLExecutor {

    public BlockedSQLExecutor(CyclicBarrier cyclicBarrier) {
      this.cyclicBarrier = cyclicBarrier;

      acquire();
    }

    @Override
    void processResultSet(ResultSet resultSet) {
      acquire();
    }

    @Override
    void runSQL(String sql) throws SQLException {
      try {
        cyclicBarrier.await();
      } catch (Exception e) {
        e.printStackTrace();
      }

      super.runSQL(sql);
    }

    void acquire() {
      try {
        semaphore.acquire();
      } catch (InterruptedException ie) {
        ie.printStackTrace();
      }
    }

    void release() {
      semaphore.release();
    }

    final CyclicBarrier cyclicBarrier;
    final Semaphore semaphore = new Semaphore(1);

  }

  private class SimpleSQLExecutor extends SQLExecutor {

    @Override
    void processResultSet(ResultSet resultSet) {}

  }

}
