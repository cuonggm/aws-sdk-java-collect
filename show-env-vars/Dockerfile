FROM --platform=linux/amd64 ubuntu:22.04

RUN apt-get update && apt-get -y install openjdk-8-jdk

RUN mkdir /home/myapp

COPY /target/* /home/myapp

CMD java -cp /home/myapp/show-env-vars-1.0.jar com.cuonggm.App