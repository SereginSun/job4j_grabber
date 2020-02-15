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
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class ParserHH implements Parser {
    private static final Logger LOG = LogManager.getLogger(ParserHH.class.getName());

    private List<Vacancy> vacancies = new ArrayList<>();
    private boolean pageLimit = true;
    private Pattern pattern = Pattern.compile(".*\\bjava\\b(?!script| script).*", Pattern.CASE_INSENSITIVE);
    private VacansiesDB dateBase;

    public ParserHH() {
        this.dateBase = new VacansiesDB(new Config());
    }

    private boolean checkJobTitle(String jobTitle) {
        return pattern.matcher(jobTitle).find();
    }

    @Override
    public List<Vacancy> parser() {
        LocalDateTime dateTime = getStartDate();
        int n = 0;
        LOG.info("Start Parsing Jobs");
        while (pageLimit) {
            String url = "https://voronezh.hh.ru/search/vacancy?L_is_autosearch=false&area=26&clusters=true" +
                    "&enable_snippets=true&specialization=1&page=" + n++;

            Document page;
            try {
                page = Jsoup.parse(new URL(url), 5000);
                Elements rows = page.select("div.vacancy-serp-item");
                for (Element element : rows) {
                    Elements titles = element
                            .getElementsByAttributeValueContaining("data-qa", "vacancy-title");
                    String jobTitle = titles.text();
                    Elements dates = element
                            .getElementsByAttributeValueContaining("data-qa", "vacancy-date");
                    String date = dates.text();
                    if (checkJobTitle(jobTitle)) {
                        if (convertDate(date).isAfter(dateTime)) {
                            String link = element
                                    .getElementsByAttributeValueContaining("data-qa", "vacancy-title")
                                    .attr("href");
                            String responsibility = element
                                    .getElementsByAttributeValueContaining("data-qa", "responsibility")
                                    .text();
                            String requirement = element
                                    .getElementsByAttributeValueContaining("data-qa", "requirement")
                                    .text();
                            String description = responsibility.concat(requirement);
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
            lastStartDate = LocalDateTime.now().minusMonths(1).plusDays(1);
        }
        return lastStartDate;
    }

    @Override
    public LocalDateTime convertDate(String date) {
        Map<Long, String> map = new HashMap<>();
        map.put(1L, "января");
        map.put(2L, "февраля");
        map.put(3L, "марта");
        map.put(4L, "апреля");
        map.put(5L, "мая");
        map.put(6L, "июня");
        map.put(7L, "июля");
        map.put(8L, "августа");
        map.put(9L, "сентября");
        map.put(10L, "октября");
        map.put(11L, "ноября");
        map.put(12L, "декабря");

        LocalDate present = LocalDate.now();
        LocalDate last = present.minusMonths(1);
        String dateTemp;
        if (last.getMonthValue() == 12 && date.contains("декабря")) {
            dateTemp = date.concat(String.valueOf(last.getYear()));
        } else {
            dateTemp = date.concat(String.valueOf(present.getYear()));
        }

        LocalDate result;
        TemporalField tf = ChronoField.MONTH_OF_YEAR;
        DateTimeFormatter formatterFull = new DateTimeFormatterBuilder()
                .appendPattern("d ")
                .appendText(tf, map)
                .appendPattern("yyyy")
                .toFormatter();
        result = LocalDate.parse(dateTemp, formatterFull);

        return result.atStartOfDay();
    }
}
