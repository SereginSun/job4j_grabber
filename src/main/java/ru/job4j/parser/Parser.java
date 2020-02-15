package ru.job4j.parser;

import java.time.LocalDateTime;
import java.util.List;

public interface Parser {

    List<Vacancy> parser();

    LocalDateTime convertDate(String date);
}
