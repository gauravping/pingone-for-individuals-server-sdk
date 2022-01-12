# PingOne JAVA SDK - Sample server code

1. Clone this repository
1. The simplest way to get this server running is to run
```
./gradlew build && ./gradlew run
```
1. Open this folder in IntelliJ. The IntelliJ IDE will automatically recognize this as a gradle project.
1. Once open IntelliJ will take a few minutes to run the first gradle build.
1. The class ServerMain is the starting point. You can run the ServerMain.main() function to start the server
1. The demo project uses SparkJava, a micro framework for creating Java web applications
1. The ServerMain.main() method will initialize the sparkjava library which will in-turn run a embedded Jetty server listening on port 4567
1. Once successfully started you can go to **http://localhost:4567**
1. On the root and only page of this web application you will see a QR code waiting to be scanned
1. Use the iOS client demo or the Android client demo to scan the QR code and share the scanned data with the site
1. Once the data is received by the server it will be verified by the SDK instance on the server
1. The processed data is available to the NotificationHandler's handleShare callback method (https://gitlab.corp.pingidentity.com/shocard/pingone-for-individuals-server-sdk/-/blob/master/sample-code/src/main/java/com/pingidentity/shocard/demo/DIDMessageHandler.java#L44)

## Customizing the code to work with PingOne Credentials

1. Request the cards that were created in PingOne Credentials
    - Change [this file](https://gitlab.corp.pingidentity.com/shocard/pingone-for-individuals-server-sdk/-/blob/master/sample-code/src/main/java/com/pingidentity/shocard/demo/ReceiveShareHandler.java#L37) to request for the card by "Card Title". For example if the card title is "Course Completion" as displayed in the image, then the code in `ReceiveShareHandler` should be as follows
    ```
    requestData.put("requested_keys", ImmutableList.of(
        "Course Completion->"
    ));
    ```
    - If you want to request only a few fields from the "Course Completion" card the code will need to be modified as follows
    ```
    requestData.put("requested_keys", ImmutableList.of(
        "Course Completion->Course Name",
        "Course Completion->Completion Date",
    ));
    ```
    - If you want to request fields from multiple cards the code will need to be modified as follows
    ```
    requestData.put("requested_keys", ImmutableList.of(
        "Course Completion->Course Name",
        "Course Completion->Completion Date",
        "U.S. Driver License->First Name",
    ));
    ```
1. In the `DIDMessageHandler` you can explore and store the requested fields in the session manager [DIDMessageHandler](https://gitlab.corp.pingidentity.com/shocard/pingone-for-individuals-server-sdk/-/blob/master/sample-code/src/main/java/com/pingidentity/shocard/demo/DIDMessageHandler.java#L44)
1. Finally, to make the fields appear in a desired order you will need to modify the [Javascript code](https://gitlab.corp.pingidentity.com/shocard/pingone-for-individuals-server-sdk/-/blob/master/sample-code/src/main/resources/public/js/shocardjs.js#L18)