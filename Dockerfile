FROM openjdk:11
ADD target/event-teammates-search.jar event-teammates-search.jar
ENTRYPOINT ["java","-jar","event-teammates-search.jar"]