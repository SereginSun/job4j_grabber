package ru.job4j.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

/**
 * Class for configuration settings connecting to the database.
 *
 * @author Seregin Vladimir (SereginSun@yandex.ru)
 * @version $Id$
 * @since 15.02.2020
 */
public class Config {
    private static final Logger LOG = LoggerFactory.getLogger(Config.class.getName());
    private final Properties values = new Properties();

    public Config() {
        this.init();
    }

    public void init() {
        try (InputStream in = Config.class.getClassLoader().getResourceAsStream("app.properties")) {
            values.load(in);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public String get(String key) {
        return this.values.getProperty(key);
    }
}
