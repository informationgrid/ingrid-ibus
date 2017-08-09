FROM openjdk:8-jdk-alpine
ENV INGRID_USER=root
ENV RUN_DIRECTLY=true
ENV IBUS_ID=/ingrid-group:ibus
ENV SERVER_PORT=80

ADD ingrid-ibus-*-installer.jar /

RUN unzip ingrid-ibus-*-installer.jar -d /tmp \
    && mkdir -p /opt/ingrid/ingrid-ibus \
    && cp -R /tmp/ingrid-ibus-*/* /opt/ingrid/ingrid-ibus/ \
    && rm -Rf /tmp/* \
    && rm ingrid-*.jar

WORKDIR /opt/ingrid/ingrid-ibus
EXPOSE 9900
EXPOSE 9901

CMD /bin/sh start.sh start