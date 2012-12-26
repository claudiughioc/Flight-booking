#!/bin/bash
TOMCAT_PATH="/home/claudiu/Desktop/sem1/sprc/5lab.web/apache-tomcat-6.0.36";

#Init resources
cp resources/context.xml    $TOMCAT_PATH/conf/;
cp resources/web.xml        $TOMCAT_PATH/webapps/axis/WEB-INF/;
cp resources/mysql-connector-java-5.1.22-bin.jar $TOMCAT_PATH/lib;

#Create database structure and test data
php scripts/createDatabase.php
