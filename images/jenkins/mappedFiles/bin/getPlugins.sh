#!/bin/bash

find ${JENKINS_HOME}/plugins -maxdepth 1 -type d -exec cat {}/META-INF/MANIFEST.MF \; | grep -E 'Short-Name|Plugin-Version' | awk '{ print $2 }' | tr -d ' \r' | paste -d ':' - - | sort
