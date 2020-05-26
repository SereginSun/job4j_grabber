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

//        try (VacanciesDB dataBase = new VacanciesDB(new Config())) {
//            LOG.info("Start program...");
//            ParserSQL parserSQL = new ParserSQL(dataBase.getLastDate());
//            ParserHH parserHH = new ParserHH(dataBase.getLastDate());
//            LOG.info("Start of parsing");
//            dataBase.addVacancies(parserSQL.parser());
////            dataBase.addVacancies(parserHH.parser());
//        } catch (Exception jee) {
//            LOG.error("Error: ", jee.fillInStackTrace(), jee);
//        }
    }
}
