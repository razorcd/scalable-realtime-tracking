sdk use java openjdk-11

mvn spring-boot:run -Dspring-boot.run.profiles='local' -Dspring-boot.run.arguments="--server.port=9091"

mvn clean package
docker build --tag=docker.xxxxxx/tracking-test:test-01 .
docker push docker.xxxxxx/tracking-test:test-01
docker run -ti --rm --name tracking-test01 --env spring_profiles_active=stage docker.xxxxxx/tracking-test:test-01


~/applications/kafka/kafka-2.0.0-src/bin/kafka-console-consumer.sh --bootstrap-server xxxxxxxxx.com:27479 --topic tracker-dummy --consumer.config consumer-stage.properties --property print.key=true --group local-02 --from-beginning