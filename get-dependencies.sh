#!/bin/sh

rm -f lib/*
mvn clean package -f ../servicestage-java-client/pom.xml -DskipTests -DfinalName=servicestage-client
cp ../servicestage-java-client/target/servicestage-client.jar ./lib/
