package ru.job4j.parser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Class adding selected vacancies to the database.
 *
 * @author Seregin Vladimir (SereginSun@yandex.ru)
 * @version $Id$
 * @since 15.02.2020
 */
public class VacansiesDB implements AutoCloseable {
    private static final Logger LOG = LogManager.getLogger(VacansiesDB.class.getName());

    private final Config config;
    private Connection conn;

    public VacansiesDB(Config config) {
        this.config = config;
        this.init();
    }

    private void init() {
        String driver = config.get("driver");
        String url = config.get("url");
        String username = config.get("username");
        String password = config.get("password");
        String create = "CREATE TABLE IF NOT EXISTS vacancy (id SERIAL PRIMARY KEY, name VARCHAR(1000) UNIQUE,text TEXT, link TEXT, date TIMESTAMP)";
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, username, password);
            LOG.info("Database connection established successfully!");
            Statement st = conn.createStatement();
            st.executeQuery(create);
            st.close();
        } catch (SQLException | ClassNotFoundException e) {
            LOG.error("Database access error", e.fillInStackTrace());
        }
    }

    public void addVacansies(List<Vacancy> vacansies) {
        String insert = "INSERT INTO vacancy (name, text, link, date) VALUES (?, ?, ?, ?) ON CONFLICT (name) DO NOTHING";
        try (PreparedStatement ps = conn.prepareStatement(insert)) {
            for (Vacancy vacancy : vacansies) {
                ps.setString(1, vacancy.getName());
                ps.setString(2, vacancy.getDescription());
                ps.setString(3, vacancy.getLink());
                ps.setTimestamp(4, Timestamp.valueOf(vacancy.getDate()));
                ps.addBatch();
            }
            ps.executeBatch();
            LOG.info("Vacancies added to the database.", vacansies.size());
        } catch (SQLException e) {
            LOG.error("Database access error", e.fillInStackTrace());
        }
    }

    public LocalDateTime getLastDate() {
        LocalDateTime lastDate = null;
        String getDate = "SELECT MAX(date)  FROM vacancy LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(getDate)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lastDate = rs.getTimestamp(0).toLocalDateTime();
            }
        } catch (SQLException e) {
            LOG.error("Database access error", e.fillInStackTrace());
        }
        return lastDate;
    }

    @Override
    public void close() throws Exception {
        if (conn != null) {
            conn.close();
        }
    }
}
