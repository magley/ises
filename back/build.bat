call ./model/mvnw.cmd clean install -f ./model/pom.xml
call ./kjar/mvnw.cmd clean install -f ./kjar/pom.xml
call ./service/mvnw.cmd clean install -DskipTests -f ./service/pom.xml