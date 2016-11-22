# @dockerfile.Template@
FROM dr.rbkmoney.com/rbkmoney/service-java:@dockerfile.base.service.tag@
MAINTAINER Poluektov Yevgeniy <e.poluektov@rbkmoney.com>

COPY GeoLite2-city.mmdb /maxmind.mmdb
COPY @artifactId@-@version@.jar /opt/@artifactId@/@artifactId@.jar

ENTRYPOINT ["java", "-Xmx256m", "-jar","/opt/@artifactId@/@artifactId@.jar"]

EXPOSE @server.port@

LABEL com.rbkmoney.@artifactId@.parent=service-java \
    com.rbkmoney.@artifactId@.parent_tag=@dockerfile.base.service.tag@ \
    com.rbkmoney.@artifactId@.commit_id=@git.revision@ \
    com.rbkmoney.@artifactId@.commit_number=@git.commitsCount@ \
    com.rbkmoney.@artifactId@.branch=@git.branch@ \
    com.rbkmoney.@artifactId@.mvn.java=@mvn.java.version@
WORKDIR /opt/@artifactId@
