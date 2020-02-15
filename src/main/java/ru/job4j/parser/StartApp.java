package ru.job4j.parser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * Class StartApp.
 *
 * @author Seregin Vladimir (SereginSun@yandex.ru)
 * @version $Id$
 * @since 15.02.2020
 */
public class StartApp {
    private static final Logger LOG = LogManager.getLogger(StartApp.class.getName());

    public static void main(String[] args) throws IOException {
        SchedulerJob job = new SchedulerJob();
        job.startScheduler();
    }
}
