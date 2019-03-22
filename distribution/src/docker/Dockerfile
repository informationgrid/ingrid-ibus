FROM openjdk:8-jdk-alpine
ENV INGRID_USER=root
ENV RUN_DIRECTLY=true
ENV IBUS_ID=/ingrid-group:ibus
ENV SERVER_PORT=80

ADD ingrid-ibus-*-installer.jar wait-for-elasticsearch.sh /

RUN unzip ingrid-ibus-*-installer.jar -d /tmp \
    && mkdir -p /opt/ingrid/ingrid-ibus \
    && mkdir -p /opt/ingrid/ingrid-ibus/logs \
    && cp -R /tmp/ingrid-ibus-*/* /opt/ingrid/ingrid-ibus/ \
    && rm -Rf /tmp/* \
    && rm ingrid-*.jar
    
RUN ln -sf /dev/stdout /opt/ingrid/ingrid-ibus/logs/log.log

WORKDIR /opt/ingrid/ingrid-ibus
EXPOSE 80
EXPOSE 9901

CMD /bin/sh start.sh start