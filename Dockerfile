FROM adoptopenjdk/openjdk11:jdk-11.0.6_10-alpine

COPY ./target/tracking-*.jar tracking.jar
COPY kafkaCerts kafkaCerts

ENV spring_profiles_active=local

EXPOSE 8080

ENTRYPOINT ["/bin/ash", "-c", "java $JAVA_OPTS -jar tracking.jar"]
