# Chat Server / Client
Simple Chat-Server / Chat-Client system written in Java.

## Build
````
cd src
javac -d ../bin chat/ChatServer.java
javac -d ../bin chat/ChatClient.java
````

## Run
````
cd bin
java chat.ChatServer
````
````
java chat.ChatClient <ipaddress> <username>
java chat.ChatClient localhost user1
java chat.ChatClient localhost user2
...
````
