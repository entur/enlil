FROM gcr.io/distroless/java25-debian13:nonroot AS builder
WORKDIR /home/nonroot
COPY target/enlil-*-SNAPSHOT.jar application.jar
RUN [ "/usr/bin/java", "-Djarmode=tools", "-jar", "application.jar", "extract", "--layers", "--destination", "extracted" ]

FROM gcr.io/distroless/java25-debian13:nonroot
WORKDIR /deployments
COPY --from=builder /home/nonroot/extracted/dependencies/ ./
COPY --from=builder /home/nonroot/extracted/spring-boot-loader/ ./
COPY --from=builder /home/nonroot/extracted/snapshot-dependencies/ ./
COPY --from=builder /home/nonroot/extracted/application/ ./
ENTRYPOINT [ "/usr/bin/java", "-jar", "application.jar" ]
