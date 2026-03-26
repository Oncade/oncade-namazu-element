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

# Workaround: Guice on the server formats errors with com.google.common.base.CaseFormat; Guava is not on
# the root Jetty classpath in some 3.7.x images, so logging a CreationException throws NoClassDefFoundError.
ARG GUAVA_VERSION=33.1.0
ARG FAILURE_ACCESS_VERSION=1.0.2
ADD --chown=elements:elements \
    https://repo1.maven.org/maven2/com/google/guava/failureaccess/${FAILURE_ACCESS_VERSION}/failureaccess-${FAILURE_ACCESS_VERSION}.jar \
    /opt/elements/lib/failureaccess-${FAILURE_ACCESS_VERSION}.jar
ADD --chown=elements:elements \
    https://repo1.maven.org/maven2/com/google/guava/guava/${GUAVA_VERSION}-jre/guava-${GUAVA_VERSION}-jre.jar \
    /opt/elements/lib/guava-${GUAVA_VERSION}-jre.jar

USER elements:elements

ENTRYPOINT [ \
    "dockerize", "-timeout", "15m", \
    "-wait", "tcp://mongo:27017", \
    "java", "jettyws" \
]
