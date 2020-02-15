package ru.job4j.parser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

/**
 * Class for QuartzAPI.
 *
 * @author Seregin Vladimir (SereginSun@yandex.ru)
 * @version $Id$
 * @since 15.02.2020
 */
public class SchedulerJob implements Job {
    private static final Logger LOG = LogManager.getLogger(SchedulerJob.class.getName());
    private final Config config;

    public SchedulerJob() {
        this.config = new Config();
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        try (VacansiesDB dataBase = new VacansiesDB(new Config())) {
            ParserSQL parserSQL = new ParserSQL();
            ParserHH parserHH = new ParserHH();
            LOG.info("Start of parsing");
            dataBase.addVacansies(parserSQL.parser());
            dataBase.addVacansies(parserHH.parser());
        } catch (Exception e) {
            LOG.error("Error: ", e.fillInStackTrace());
        }
    }

    public void startScheduler() {
        try {
            JobDetail jobDetail = JobBuilder.newJob(SchedulerJob.class)
                    .withIdentity("job1", "group1")
                    .build();
            CronTrigger trigger = TriggerBuilder
                    .newTrigger()
                    .withIdentity("trigger1", "group1")
                    .withSchedule(CronScheduleBuilder.cronSchedule(this.config.get("cron.time")))
                    .build();
            Scheduler scheduler = new StdSchedulerFactory().getScheduler();
            scheduler.start();
            LOG.info("The scheduler is running.");
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            LOG.error("Scheduler error: ", e.fillInStackTrace());
        }
    }
}
