FROM gcr.io/distroless/java21-debian12

USER root
COPY build/libs/application.jar application.jar
EXPOSE 8080

USER nonroot
ENTRYPOINT ["java","-jar","application.jar"]
