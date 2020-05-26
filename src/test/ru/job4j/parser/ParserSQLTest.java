package ru.job4j.parser;

import org.junit.Test;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class ParserSQLTest {

    public Connection init() {
        try (InputStream in = VacanciesDB.class.getClassLoader().getResourceAsStream("app.properties")) {
            Properties config = new Properties();
            config.load(in);
            Class.forName(config.getProperty("driver"));
            return DriverManager.getConnection(
                    config.getProperty("url"),
                    config.getProperty("username"),
                    config.getProperty("password")
            );
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Test
    public void whenInsertDataThenSizeIs1() throws SQLException {
        VacanciesDB testDataBase = new VacanciesDB(ConnectionRollback.create(this.init()));
        ParserSQL parser = new ParserSQL(testDataBase.getLastDate());
        LocalDateTime vacanciesDate = parser.convertDate("вчера, 19:08");
        Vacancy testVacancy = new Vacancy("title Java Developer", "desc", "url", vacanciesDate);
        List<Vacancy> in = new ArrayList<>();
        in.add(testVacancy);
        testDataBase.addVacancies(in);
        int size = in.size();
        assertThat(size, is(1));
    }

    @Test
    public void whenDateToFormatThenReturnLocalDateTime() throws SQLException {
        VacanciesDB testDataBase = new VacanciesDB(ConnectionRollback.create(this.init()));
        ParserSQL parser = new ParserSQL(testDataBase.getLastDate());
        LocalDateTime result = parser.convertDate("27 мар 20, 17:32");
        LocalDateTime expected = LocalDateTime.of(2020, 3, 27, 17, 32);
        assertThat(result, is(expected));
    }
}