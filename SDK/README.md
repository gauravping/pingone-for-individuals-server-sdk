# PingOne for Individuals SDK

## [SDK documentation](https://apidocs.pingidentity.com/pingone/native-sdks/v1/api/#pingone-shocard-server-sdk)

## Setup

Please include the following dependencies in your build script.

### Maven
```xml
<dependencies>
  <dependency>
    <groupId>com.squareup.retrofit2</groupId>
    <artifactId>retrofit</artifactId>
    <version>2.7.2</version>
  </dependency>
  <dependency>
    <groupId>com.squareup.retrofit2</groupId>
    <artifactId>converter-moshi</artifactId>
    <version>2.7.2</version>
  </dependency>
  <dependency>
    <groupId>com.squareup.moshi</groupId>
    <artifactId>moshi</artifactId>
    <version>1.9.3</version>
  </dependency>
  <dependency>
    <groupId>org.bitbucket.b_c</groupId>
    <artifactId>jose4j</artifactId>
    <version>0.7.4</version>
  </dependency>
  <dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
    <version>1.7.30</version>
  </dependency>
  <dependency>
    <groupId>com.squareup.okio</groupId>
    <artifactId>okio</artifactId>
    <version>1.16.0</version>
  </dependency>
  <dependency>
    <groupId>com.squareup.okhttp3</groupId>
    <artifactId>okhttp</artifactId>
    <version>3.14.7</version>
  </dependency>
</dependencies>
```

### Gradle
```groovy
dependencies {
    implementation 'com.squareup.retrofit2:converter-moshi:2.7.2'
    implementation 'com.squareup.retrofit2:retrofit:2.7.2'
    implementation 'com.squareup.moshi:moshi:1.9.3'
    implementation 'org.bitbucket.b_c:jose4j:0.7.4'
    implementation 'org.slf4j:slf4j-api:1.7.30'
    implementation 'com.squareup.okio:okio:1.16.0'
    implementation 'com.squareup.okhttp3:okhttp:3.14.7'
}
```
