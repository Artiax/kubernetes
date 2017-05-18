FROM openjdk:8-jdk

#######################################################################
# Installing additional packages
#######################################################################

RUN apt-get update && \
    apt-get install -y jq && \
    apt-get install -y iptables apt-transport-https ca-certificates curl software-properties-common

#######################################################################
# Configuring package repositories
#######################################################################

RUN curl -fsSL https://download.docker.com/linux/debian/gpg | apt-key add -
RUN add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/debian $(lsb_release -cs) stable"

#######################################################################
# Installing docker
#######################################################################

RUN apt-get update && \
    apt-get install -y docker-ce && \
    rm -rf /var/lib/apt/lists/*

#######################################################################
# Configure jenkins directory
#######################################################################

ENV JENKINS_HOME=/var/jenkins_home

RUN mkdir -p ${JENKINS_HOME}/.jenkins

#######################################################################
# Copying and laying down the files
#######################################################################

COPY mappedFiles /tmp/mappedFiles
COPY fileMappings.json /tmp/

RUN mv /tmp/mappedFiles/bin/fileMapper.sh /usr/local/bin/ && \
    chmod +x /usr/local/bin/fileMapper.sh && \
    fileMapper.sh /tmp/fileMappings.json && \
    chmod -R +x /usr/local/bin/ && \
    rm -rf /tmp/mappedFiles

#######################################################################
# Miscellaneous configuration
#######################################################################

ENTRYPOINT ["/usr/local/bin/entrypoint.sh"]
