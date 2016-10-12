#!/bin/bash

#find ../*/libs -name "*limelib-dev.jar" | xargs rm
rm build/libs/limelib-dev.jar
./gradlew build
mv -f build/libs/*dev.jar build/libs/limelib-dev.jar
echo ../*/libs | xargs -n 1 cp build/libs/limelib-dev.jar
#find build/libs -name "*limelib-dev.jar" | xargs rm
