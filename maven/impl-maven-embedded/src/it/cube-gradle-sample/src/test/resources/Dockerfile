FROM skorochkin/java-gradle

RUN cd /root \
&& git clone http://github.com/MatousJobanek/resolver \
&& cd /root/resolver \

&& git fetch origin \
&& git checkout origin/maven_embedded_gradle \
&& cd /root/resolver/maven/impl-maven-embedded/src/it/cube-gradle-sample/gradle-sample/

CMD ["/opt/gradle/bin/gradle","-b","/root/resolver/maven/impl-maven-embedded/src/it/cube-gradle-sample/gradle-sample/build.gradle","test","-i"]
