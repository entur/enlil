FROM bellsoft/liberica-openjdk-alpine:21.0.1-12

RUN apk update && apk upgrade && apk add --no-cache \
    tini=0.19.0

WORKDIR /deployments
COPY target/enlil-*-SNAPSHOT.jar deneir.jar
RUN addgroup appuser && adduser --disabled-password appuser --ingroup appuser
USER appuser

CMD [ "/sbin/tini", "--", "java", "-jar", "enlil.jar" ]
