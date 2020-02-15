package ru.job4j.parser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Class for parsing an Internet site www.sql.ru.
 *
 * @author Seregin Vladimir (SereginSun@yandex.ru)
 * @version $Id$
 * @since 15.02.2020
 */
public class ParserSQL implements Parser {
    private static final Logger LOG = LogManager.getLogger(ParserSQL.class.getName());

    private List<Vacancy> vacancies = new ArrayList<>();
    private boolean pageLimit = true;
    private Pattern pattern = Pattern.compile(".*\\bjava\\b(?!script| script).*", Pattern.CASE_INSENSITIVE);
    private VacansiesDB dateBase;

    public ParserSQL() {
        this.dateBase = new VacansiesDB(new Config());
    }

    private boolean checkJobTitle(String jobTitle) {
        return pattern.matcher(jobTitle).find();
    }

    @Override
    public List<Vacancy> parser() {
        LocalDateTime dateTime = getStartDate();
        int n = 1;
        LOG.info("Start Parsing Jobs");
        while (pageLimit) {
            String url = "https://www.sql.ru/forum/job-offers/" + n++;
            Document page;
            try {
                page = Jsoup.parse(new URL(url), 5000);
                Element forumTable = page.selectFirst("table.forumTable");
                Elements rows = forumTable.select("tr").next();
                for (int i = 4; i < rows.size(); i++) {
                    Element row = rows.get(i);
                    Element element = row.select("td.postslisttopic > a").first();
                    Element topicDate = row.select("td.altCol").last();
                    String jobTitle = element.text();
                    String date = topicDate.text();
                    if (checkJobTitle(jobTitle)) {
                        if (convertDate(date).isAfter(dateTime)) {
                            String link = row.select("a").attr("href");
                            Document vacancy = Jsoup.parse(new URL(link), 5000);
                            Element vacanciesTable = vacancy.selectFirst("table.msgTable");
                            Element desc = vacanciesTable.select("td.msgBody").last();
                            String description = desc.text();
                            vacancies.add(new Vacancy(jobTitle, description, link, convertDate(date)));
                        } else {
                            pageLimit = false;
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                LOG.error("URL connection error: ", e.fillInStackTrace());
            }
        }
        LOG.info("Job parsing completed");
        return vacancies;
    }

    public LocalDateTime getStartDate() {
        LocalDateTime lastStartDate = dateBase.getLastDate();
        if (lastStartDate == null) {
            lastStartDate = LocalDateTime.of(2020, 1, 1, 0, 0);
        }
        return lastStartDate;
    }

    @Override
    public LocalDateTime convertDate(String date) {
        Map<Long, String> map = new HashMap<>();
        map.put(1L, "янв");
        map.put(2L, "фев");
        map.put(3L, "мар");
        map.put(4L, "апр");
        map.put(5L, "май");
        map.put(6L, "июн");
        map.put(7L, "июл");
        map.put(8L, "авг");
        map.put(9L, "сен");
        map.put(10L, "окт");
        map.put(11L, "ноя");
        map.put(12L, "дек");

        LocalDateTime result;
        TemporalField tf = ChronoField.MONTH_OF_YEAR;
        DateTimeFormatter formatterFull = new DateTimeFormatterBuilder()
                .appendPattern("d ")
                .appendText(tf, map)
                .appendPattern(" yy, HH:mm")
                .toFormatter();
        DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("HH:mm");
        String[] arrSplit = date.split(", ");
        LocalDate datePart;
        LocalTime timePart = LocalTime.parse(arrSplit[1], formatterTime);
        if (date.contains("сегодня")) {
            datePart = LocalDate.now();
            result = LocalDateTime.of(datePart, timePart);
        } else
        if (date.contains("вчера")) {
            datePart = LocalDate.now().minusDays(1);
            result = LocalDateTime.of(datePart, timePart);
        } else {
            result = LocalDateTime.parse(date, formatterFull);
        }
        return result;
    }
}