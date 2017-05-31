FROM openjdk:8-jdk-alpine
ENV INGRID_USER=root
ENV ADMIN_PASSWORD=admin
ENV BUS_URL=/ingrid-group:ibus

ADD ingrid-ibus-*-installer.jar /

RUN unzip ingrid-ibus-*-installer.jar -d /tmp \
    && mkdir -p /opt/ingrid/ingrid-ibus \
    && cp -R /tmp/ingrid-ibus-*/* /opt/ingrid/ingrid-ibus/ \
    && sed -i 's/@ADMIN_PASSWORD@/$ADMIN_PASSWORD/' /opt/ingrid/ingrid-ibus/start.sh \
    && sed -i 's/@ADMIN_PORT@/9901/' /opt/ingrid/ingrid-ibus/start.sh \
    && sed -i 's/@BUS_URL@/$BUS_URL/' /opt/ingrid/ingrid-ibus/start.sh \
    && sed -i 's/@serverport@/9900/' /opt/ingrid/ingrid-ibus/conf/communication.xml \
    && rm -Rf /tmp/* \
    && rm ingrid-*.jar

WORKDIR /opt/ingrid/ingrid-ibus
EXPOSE 9900
EXPOSE 9901

CMD /bin/sh start.sh start && tail -f /dev/null