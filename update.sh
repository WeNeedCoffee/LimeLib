#!/bin/bash

./gradlew build
echo ../*/libs | xargs -n 1 cp build/libs/limelib-1.10.2-1.0.0-dev.jar

