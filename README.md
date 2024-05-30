# ises

![Static Badge](https://img.shields.io/badge/spring_boot-2.7.9-green)
![Static Badge](https://img.shields.io/badge/react-remix-red)
![Static Badge](https://img.shields.io/badge/Drools-7.49.0-yellow)


Information security expert system. Powered by Drools, 

# Getting started

For instructions on how to run the backend server, consult the `README` in `back/`.

For instructions on how to run the web app, consult the `README` in `app/`.

# Using the application

TODO: Add screenshots (or just remove this section)

# Development notes

All the rules for the rule engine are manually loaded
(`DroolsBeans::getKieSessionWithCreatedTemplates()`) which includes both regular
rules and templates. Regular rules are scraped automatically if found in
`rules/**/*.drl`. Template files have to be specified manually. Template
parameters are loaded from a corresponding `.xls` files (if any).

The server boots up with some intialized data. Check the `DevServerInitializer`
class to learn more. This initializes both the SQL database and the Drools
knowledge base.

# Penetration testing

In an attempt to make the rules as "real" as possible, it's inevitable that some
of the rules cannot be tested during normal interaction with the web app. For
this reason, some of the functionalities can be tested using "penetration test"
scripts written in python. Check the `pentesting/` directory for more info.