# ises Back

Backend server for ises.

- Java 11
- Spring Boot 2.7.9
- MariaDB

## Building

You can build everything using `build.bat`. Be careful as this does a `clean
install` on all three projects, which is an overkill for most cases.

## Running

You may run the server using maven in the `service` directory:

```bash
mvn mvn spring-boot:run
```

or through an IDE of your choice.

## Database

MariaDB 11.3.2 is used for storing SQL data.
Check `service/.../application.properties` for the parameters.

Instructions (Windows):

1. Download [mariadb-11.3.2-winx64.msi](https://mariadb.org/download/?t=mariadb&p=mariadb&r=11.3.2&os=windows&cpu=x86_64&pkg=msi&mirror=bme)
2. Install anywhere (recommended to install as a service)
3. Create a database named `sbnz_db` (using HeidiSQL which comes bundled with MariaDB or through the CLI)
4. Username and password should be the default values: `root` and (empty) respsectively (see `application.properties` for more info)