IDK GAME SERVER
========

IDK is a Game Server written in Java. It includes and uses the libraries log4j, BoneCP, JSON Simple, Netty and Google's Guava Project.

## Building
To build you will need:

* [JDK 1.7+](http://www.oracle.com/technetwork/java/javase/downloads)

To build everything using Gradle (the command below will download Gradle automatically, you do not need to download it first).

    ./gradlew clean dist

Or under Windows:
	
	gradlew clean dist

The distribution ready file can then be found under `build/distributions`. The other JAR files can be found under `build/libs`.

## License

IDK is licensed under the terms of the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).