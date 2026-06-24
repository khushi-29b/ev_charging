# ---------- Stage 1: Compile the Java sources and build the WAR ----------
FROM eclipse-temurin:21-jdk AS build

WORKDIR /build
COPY src ./src
COPY WebContent ./WebContent

# Compile servlets/DAOs/models against the libs already bundled in WEB-INF/lib
# Download the Jakarta Servlet API needed only for compiling (Tomcat provides it at runtime)
ADD https://repo1.maven.org/maven2/jakarta/servlet/jakarta.servlet-api/6.0.0/jakarta.servlet-api-6.0.0.jar /tmp/servlet-api.jar

RUN mkdir -p WebContent/WEB-INF/classes && \
    find src -name "*.java" > sources.txt && \
    javac -cp "WebContent/WEB-INF/lib/*:/tmp/servlet-api.jar" -d WebContent/WEB-INF/classes @sources.txt

# Package everything in WebContent into a fresh WAR (overwrites the old prebuilt one)
WORKDIR /build/WebContent
RUN jar -cf /build/ev-charging.war .

# ---------- Stage 2: Run it on Tomcat ----------
FROM tomcat:10.1-jdk21

# Remove default Tomcat sample apps (not needed, smaller image)
RUN rm -rf /usr/local/tomcat/webapps/*

# Deploy under the "ev_charging" context path, matching the path the
# frontend's app.js already calls (/ev_charging/api/...).
COPY --from=build /build/ev-charging.war /usr/local/tomcat/webapps/ev_charging.war

# Tiny redirect page at "/" so visiting the bare Render URL also works
RUN mkdir -p /usr/local/tomcat/webapps/ROOT && \
    printf '<meta http-equiv="refresh" content="0; url=/ev_charging/">' \
    > /usr/local/tomcat/webapps/ROOT/index.html

EXPOSE 8080
CMD ["catalina.sh", "run"]
