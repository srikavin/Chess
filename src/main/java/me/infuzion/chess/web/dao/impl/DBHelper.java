/*
 * Copyright 2020 Srikavin Ramkumar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.infuzion.chess.web.dao.impl;

import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@FunctionalInterface
interface PreparedStatementHandler<T> {
    T call(PreparedStatement preparedStatement) throws SQLException;
}

@FunctionalInterface
interface VoidPreparedStatementHandler {
    void call(PreparedStatement preparedStatement) throws SQLException;
}

@FunctionalInterface
interface VoidStatementHandler {
    void call(Statement statement) throws SQLException;
}

@FunctionalInterface
interface ResultSetMapper<T> {
    T map(ResultSet set) throws SQLException;
}

@FunctionalInterface
interface TransactionHandler<T> {
    T call(Connection connection) throws SQLException;
}

@FunctionalInterface
interface VoidTransactionHandler {
    void call(Connection connection) throws SQLException;
}


public class DBHelper {
    public static void statement(DataSource source, VoidStatementHandler callable) {
        try (Connection connection = source.getConnection()) {
            statement(connection, callable);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void statement(Connection connection, VoidStatementHandler callable) throws SQLException {
        try (Statement preparedStatement = connection.createStatement()) {
            callable.call(preparedStatement);
        }
    }

    public static void transaction(DataSource source, VoidTransactionHandler handler) {
        try (Connection connection = source.getConnection()) {
            try {
                connection.setAutoCommit(false);
                handler.call(connection);
                connection.commit();
            } catch (Exception e) {
                connection.rollback();
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T transaction(DataSource source, TransactionHandler<T> handler) {
        try (Connection connection = source.getConnection()) {
            try {
                connection.setAutoCommit(false);
                T ret = handler.call(connection);
                connection.commit();
                return ret;
            } catch (Exception e) {
                connection.rollback();
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T prepareStatement(DataSource source, @Language("sql") String sql, PreparedStatementHandler<T> callable) {
        try (Connection connection = source.getConnection()) {
            return prepareStatement(connection, sql, callable);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T prepareStatement(Connection connection, @Language("sql") String sql, PreparedStatementHandler<T> callable) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            return callable.call(preparedStatement);
        }
    }

    public static void executeStatement(DataSource source, @Language("sql") String sql) {
        try (Connection connection = source.getConnection()) {
            executeStatement(connection, sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void executeStatement(Connection connection, @Language("sql") String sql) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    public static void prepareStatement(DataSource source, String sql, VoidPreparedStatementHandler callable) {
        try (Connection connection = source.getConnection()) {
            prepareStatement(connection, sql, callable);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void prepareStatement(Connection connection, String sql, VoidPreparedStatementHandler callable) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            callable.call(preparedStatement);
        }
    }

    @Nullable
    public static <T> T mapFirstElement(ResultSet set, ResultSetMapper<T> mapper) {
        try (set) {
            if (!set.next()) {
                return null;
            }

            return mapper.map(set);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public static <T> List<@Nullable T> mapElements(ResultSet set, ResultSetMapper<T> mapper) {
        try (set) {
            List<T> list = new ArrayList<>();

            while (set.next()) {
                list.add(mapper.map(set));
            }

            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
