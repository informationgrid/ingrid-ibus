FROM alpine AS builder

ADD ingrid-ibus-*-installer.jar wait-for-elasticsearch.sh /
RUN unzip -q ingrid-ibus-*-installer.jar -d /tmp \
    && export INSTALL_DIR=`ls -d1 /tmp/ingrid-ibus-*` \
    && mkdir -p $INSTALL_DIR/logs \
    && sed -i -r 's/(<AppenderRef.*\/>)/\1<AppenderRef ref="Console" \/>/g' $INSTALL_DIR/conf/log4j2.xml \
    && cp wait-for-elasticsearch.sh $INSTALL_DIR/


FROM docker-registry.wemove.com/ingrid-java:jre-17
ENV INGRID_USER=ingrid
ENV RUN_DIRECTLY=true
ENV IBUS_ID=/ingrid-group:ibus
ENV SERVER_PORT=8080

# user, group ingrid (1000:1000) is created in base image
COPY --chown=1000:1000 --from=builder /tmp/ingrid-ibus-* /opt/ingrid/ingrid-ibus/

USER ingrid
    
WORKDIR /opt/ingrid/ingrid-ibus
EXPOSE 8080
EXPOSE 9900

CMD /bin/sh start.sh start
