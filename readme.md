# MoneyTransferAPI
Spring boot application which provide RESTful API for money transfer

### Prerequisite
- Gradle
- JDK 1.8+

### build with test
```
gradlew build
```
### build without test
```
gradlew build -x test
```
### Running
```
java -jar challenge-0.0.1-SNAPSHOT
```

## Feature
This application is for demo only. It provides APIs for following 2 features
- Create Account and Retrieve Account
- Create Transaction (money Transfer)
- Send Notification to Account holder(not implemented)
### Basic API Information
| Method | Path | Usage |
| --- | --- | --- |
| POST | /v1/accounts | Create account  |
| GET  | /v1/accounts/{accountId}  | Retrieve transaction |
| POST | /v1/transactions/transfer | create transaction   |
### Swagger-UI
API Specification is provided in the [swagger-ui page](http://localhost:18080/swagger-ui.html) after spring boot application start.
```
http://localhost:18080/swagger-ui.html
```

### Library used
| Library | Usage |
| --- | --- |
| spring boot | for spring boot application |
| springfox swagger | generate swagger API specification from code |
| springfox swagger ui | generate swagger ui page for specification |
| lombok | plugin for getter setter and log support |