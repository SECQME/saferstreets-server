# SaferStreets API Server

## Tech Stack
* Apache Tomcat 7.0
* Java EE 1.7
    * Eclipse Java Persistence 2.0
    * Spring 2.5
    * Redis Cache
* Postgresql
---
## Structure of Documentation

```
┌──────┐
| docs |
└──────┘
    |    ┌───────┐
    +--> |  API  |
    |    └───────┘
    |        |    ┌────────────────────┐
    |        +--> |     data.md        | * Data Resources
    |        |    └────────────────────┘
    |        |    ┌────────────────────┐
    |        +--> |     manage.md      | * Manage Resources
    |             └────────────────────┘
    |    ┌───────┐
    +--> | Error |
    |    └───────┘
    |        |    ┌────────────────────┐
    |        +--> | core_exceptions.md | * CoreExceptions - Error Messages
    |             └────────────────────┘
    |    ┌───────┐
    +--> | Setup |
    |    └───────┘
    |        |    ┌────────────────────┐
    |        +--> |  initial_setup.md  | * Instructions to Setup Environment
                  └────────────────────┘
```

* Read `initial_setup.md` in Setup Folder to setup SaferStreets Environment

---
## High Level View of SaferStreets
```
                                                         ┌──────────────────┐
                                ┌──────────────┐    +--> |  Data Resources  |
┌────────────────────┐         ┌──────────────┐|    |    └──────────────────┘
| Redis Cache Server | <------ |   Managers   |┘ <--+        (REST APIs)
└────────────────────┘  Read   └──────────────┘     |    ┌──────────────────┐
                                 (Controller)       +--> | Manage Resources |
                                                         └──────────────────┘
```
1. When Mobile Phone call the APIs, the manager will read on the Redis Cache Server
2. CrimeReport based on grid is retrieved from the Redis Cache Server
3. Then it will get to convert to JSON format and return in JSON String

---
## Structure of SaferStreets

```
src
 +-> com.secqme
        |
        +--> crimedata
                 |
                 +--> domain
                        +--> converter
                             * PostgisConverter : Convert coordinates
                        +--> dao
                              +--> jpa
                                    * Models JPA Data Acces Object (JPADAO)
                              * Models Data Access Object (DAO)
                        +--> model
                             * Models Value Object (VO)
                        +--> serializer
                             * GeometrySerializer   : Serialize geometry of coordinates
                             * GeometryDeserializer : Deserialize geometry of coordinates
                 +--> filter
                        * RollbarFilter : Log request and catch exception. Send the exception to Rollbar.
                 +--> manager
                        * CrimeManager  : Handle request of object from API which related to crime data
                        * DataManager   : Handle update of Daytime and Night Report  
                 +--> rs (API)
                       +--> provider
                             * CoreExceptionsMapper
                             * NotFoundExceptionMapper
                             * ThrowableExceptionMapper
                       +--> v2
                             * DataResources   (Version 2)
                       * DataResources   (Version 2)
                       * ManageResources (Version 2)
```
