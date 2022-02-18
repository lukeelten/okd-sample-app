FROM registry.access.redhat.com/ubi8/openjdk-17:1.11 as builder

USER root
RUN mkdir -p /app
WORKDIR /app

COPY . .

RUN mvn compile

RUN mvn test

RUN mvn package




FROM registry.access.redhat.com/ubi8/openjdk-17-runtime:1.11

ENV LANG='en_US.UTF-8' LANGUAGE='en_US:en'

# We make four distinct layers so if there are application changes the library layers can be re-used
COPY --chown=185 --from=builder /app/target/quarkus-app/lib/ /deployments/lib/
COPY --chown=185 --from=builder /app/target/quarkus-app/*.jar /deployments/
COPY --chown=185 --from=builder /app/target/quarkus-app/app/ /deployments/app/
COPY --chown=185 --from=builder /app/target/quarkus-app/quarkus/ /deployments/quarkus/

EXPOSE 8080
USER 185

ENV AB_JOLOKIA_OFF=""
ENV JAVA_OPTS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
ENV JAVA_APP_JAR="/deployments/quarkus-run.jar"

ENTRYPOINT [ "java" ]
CMD [ "-jar", "/deployments/quarkus-run.jar" ]
