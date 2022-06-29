FROM --platform=linux/amd64 ubuntu:22.04

RUN apt-get update && apt-get -y install openjdk-8-jdk

RUN mkdir /home/display-args-app

COPY /target/* /home/display-args-app

CMD java -cp /home/display-args-app/display-args-app-1.0-SNAPSHOT.jar com.cuonggm.App Param1 Param2 Param3