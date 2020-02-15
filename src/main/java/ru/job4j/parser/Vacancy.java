package ru.job4j.parser;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Class Vacancy.
 *
 * @author Seregin Vladimir (SereginSun@yandex.ru)
 * @version $Id$
 * @since 15.02.2020
 */
public class Vacancy {
    private String name;
    private String description;
    private String link;
    private LocalDateTime date;

    public Vacancy(String name, String description, String link, LocalDateTime date) {
        this.name = name;
        this.description = description;
        this.link = link;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getLink() {
        return link;
    }

    public LocalDateTime getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Vacancy{"
                + "name='" + name + '\''
                + ", description='" + description
                + '\'' + ", link='" + link + '\''
                + ", date=" + date + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Vacancy vacancy = (Vacancy) o;
        return Objects.equals(name, vacancy.name)
                && Objects.equals(description, vacancy.description)
                && Objects.equals(link, vacancy.link)
                && Objects.equals(date, vacancy.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, link, date);
    }
}
