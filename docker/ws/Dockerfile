ARG TAG
FROM elementalcomputing/elements-jetty-ws:${TAG}

USER root:root
RUN mkdir -p /var/log/elements && \
    chown -R elements:elements /var/log/elements

# Workaround: slf4j-api missing from 3.7.x images
ARG SLF4J_VERSION=1.7.36
ADD --chown=elements:elements \
    https://repo1.maven.org/maven2/org/slf4j/slf4j-api/${SLF4J_VERSION}/slf4j-api-${SLF4J_VERSION}.jar \
    /opt/elements/lib/slf4j-api-${SLF4J_VERSION}.jar

USER elements:elements

ENTRYPOINT [ \
    "dockerize", "-timeout", "15m", \
    "-wait", "tcp://mongo:27017", \
    "java", "jettyws" \
]
