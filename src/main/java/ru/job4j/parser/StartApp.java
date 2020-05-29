package ru.job4j.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class StartApp.
 *
 * @author Seregin Vladimir (SereginSun@yandex.ru)
 * @version $Id$
 * @since 15.02.2020
 */
public class StartApp {
    private static final Logger LOG = LoggerFactory.getLogger(StartApp.class.getName());

    public static void main(String[] args) {
        LOG.info("Program start!");
        SchedulerJob job = new SchedulerJob();
        job.startScheduler();
    }
}
