FROM adoptopenjdk/openjdk11 as BUILD
WORKDIR /app

ADD project /app/project
ADD build.sbt /app/build.sbt
ADD src /app/src

ADD sbt /usr/local/bin/
RUN chmod a+x /usr/local/bin/sbt
RUN sbt -no-colors clean assembly

FROM adoptopenjdk/openjdk11

COPY --from=BUILD /app/target/scala-2.13/order-status-recommender-api*.jar /app/order-status-recommender-api.jar

EXPOSE 8080

CMD [ "java", \
      "-jar", \
      "/app/order-status-recommender-api.jar" \
    ]
