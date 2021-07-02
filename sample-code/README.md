# PingOne JAVA SDK - Sample server code

1. Clone this repository
1. The simplest way to get this server running is to run 
```
./gradlew build && ./gradlew run
```
1. Open this folder in IntelliJ. The IntelliJ IDE will automatically recognize this as a gradle project.
1. Once open IntelliJ will take a few minutes to run the first gradle build.
1. The class com.shocard.demo.ServerMain is the starting point. You can run the ServerMain.main() function to start the server
1. The demo project uses SparkJava, a micro framework for creating Java web applications
1. The ServerMain.main() method will initialize the sparkjava library which will in-turn run a embedded Jetty server listening on port 4567
1. Once successfully started you can go to **http://localhost:4567**
1. On the root and only page of this web application you will see a QR code waiting to be scanned
1. Use the iOS client demo or the Android client demo to scan the QR code and share the scanned data with the site
1. Once the data is received by the server it will be verified by the SDK instance on the server
1. The processed data is available to the NotificationHandler's shareReceived method (https://git.shocard.io/ShoCard/ShoCard_Docs/wikis/home#notificationhandler-delegate)
1. This data is then presented on the web page with a certify button.
1. When you press the certify button on the web page, the server will call the certifyData SDK method (https://git.shocard.io/ShoCard/ShoCard_Docs/wikis/home#certify-data) to create a certification of the shared data.
1. The app will receive a notification to indicate that  it received a Certification from the server.      

