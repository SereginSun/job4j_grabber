[![Build Status](https://travis-ci.org/SereginSun/job4j_grabber.svg?branch=master)](https://travis-ci.org/SereginSun/job4j_grabber)
[![codecov](https://codecov.io/gh/SereginSun/job4j_grabber/branch/master/graph/badge.svg)](https://codecov.io/gh/SereginSun/job4j_grabber)
# job4j_grabber

Parser vacancies site sql.ru

The parser application should go to the sql.ru website in the work section and collect Java vacancies,
excluding Java Script programmers from the sql.ru website from the beginning of the current year to
the current date and save them in the database.

 ## Used technologies:
    - Site parsing - jsoup
    - Database - JDBC and postgres
    - Logging - slf4j
    - Testing - Junit