package ru.job4j.parser;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Class for QuartzAPI.
 *
 * @author Seregin Vladimir (SereginSun@yandex.ru)
 * @version $Id$
 * @since 15.02.2020
 */
public class SchedulerJob implements Job {
    private static final Logger LOG = LoggerFactory.getLogger(SchedulerJob.class.getName());
    private final Config config;

    public SchedulerJob() {
        this.config = new Config();
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        LOG.info("Start worker...");
        try (VacanciesDB dataBase = new VacanciesDB(this.config)) {
            ParserSQL parserSQL = new ParserSQL(dataBase.getLastDate());
            LOG.info("Start of parsing");
            dataBase.addVacancies(parserSQL.parser());
        } catch (Exception e) {
            LOG.error("Error: ", e.fillInStackTrace());
        }
    }

    public void startScheduler() {
        try {
            SchedulerFactory schedulerFactory = new StdSchedulerFactory();
            Scheduler scheduler = schedulerFactory.getScheduler();
            JobDetail job = newJob(SchedulerJob.class).build();
            CronTrigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(cronSchedule(this.config.get("cron.time")))
                    .build();
            scheduler.start();
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            LOG.error("Scheduler error: ", e.fillInStackTrace());
        }
    }
}
