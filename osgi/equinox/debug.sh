#!/bin/sh

java -agentlib:jdwp=transport=dt_socket,address=8100,server=y,suspend=y \
     -jar org.eclipse.osgi_3.7.1.R37x_v20110808-1106.jar
