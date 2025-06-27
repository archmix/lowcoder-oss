package lowcoder.api.interfaces;

import io.vertx.jdbcclient.JDBCPool;

import java.util.function.Consumer;

public interface JDBCPoolConsumer extends Consumer<JDBCPool> {}
