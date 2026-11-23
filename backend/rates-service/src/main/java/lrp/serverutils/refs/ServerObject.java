package lrp.serverutils.refs;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Spring-backed stub of Booking {@code lrp.serverutils.refs.ServerObject}.
 * <p>
 * {@code CommonSurchargeBean} opens JDBC via {@code new ServerObject().getConnection()}.
 * In ECOMAPI there is no AppServer JNDI pool, so datasources are registered from Spring
 * ({@code commercialDataSource} by default).
 */
public class ServerObject {

    private static final Map<String, DataSource> DATA_SOURCES = new ConcurrentHashMap<String, DataSource>();
    private static volatile DataSource defaultDataSource;

    private final String name;

    public ServerObject() {
        this.name = null;
    }

    public ServerObject(String name) {
        this.name = name;
    }

    /**
     * Registers the default datasource used by the no-arg constructor.
     */
    public static void setDefaultDataSource(DataSource dataSource) {
        defaultDataSource = dataSource;
    }

    /**
     * Registers a named datasource (e.g. {@code ECOMDB}, {@code Commercial}).
     */
    public static void registerDataSource(String name, DataSource dataSource) {
        if (name == null || dataSource == null) {
            return;
        }
        DATA_SOURCES.put(name.trim().toUpperCase(), dataSource);
    }

    public Connection getConnection() throws SQLException {
        DataSource ds = resolveDataSource();
        if (ds == null) {
            throw new SQLException(
                    "No DataSource registered for ServerObject"
                            + (name != null ? " name=" + name : " (default)")
                            + ". Ensure SurchargeServerObjectConfig has started.");
        }
        return ds.getConnection();
    }

    private DataSource resolveDataSource() {
        if (name != null && name.trim().length() > 0) {
            DataSource named = DATA_SOURCES.get(name.trim().toUpperCase());
            if (named != null) {
                return named;
            }
        }
        return defaultDataSource;
    }
}
