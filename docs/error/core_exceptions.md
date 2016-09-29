# CoreExceptions in SaferStreets

Error code return from saferstreets when calling APIs from Saferstreets

## CoreExceptions Error for APIs
Example of error message return from server
```json
{
    "errorMessage": "Out of coverage for city. Please check request coordinates",
    "errorCode": "city.out.of.bound"
}
```


ErrorType | Message
----------- | --------------
`PATH_NOT_FOUND` | Resource not found.
`INTERNAL_SERVER_ERROR` | Oops, looks like something went wrong! We track these errors automatically, but if the problem persists feel free to contact us. In the meantime, try refreshing.
`CRIME_CITY_NOT_FOUND_EXCEPTION` | We don't have any crime data for {0}, please send us email support@watchovermeapp.com, if you wants us to support this city
`PARAMETER_NOT_FOUND_EXCEPTION` | Parameters are incorrect
`PARAMETER_USERID_NOT_FOUND_EXCEPTION` | Parameter "userid" is not found in request body
`PARAMETER_USER_EMAIL_NOT_FOUND_EXCEPTION` | Parameter "email" is not found in request body
`PARAMETER_COORDINATE_OUT_OF_BOUND` | Parameter Coordinate is out of bound
`USER_REQUESTED_SAFERSTREETS_SUPPORT` | User had requested for saferstreets support.
`CITY_PARAMETER_NOT_FOUND` | Parameter "city", "state", or "country" are not found in request body
`CITY_REQUESTED_NOT_FOUND` | User's current city is not supported by saferstreets.
`CITY_OUT_OF_BOUND` | Out of coverage for city. Please check request coordinates
`CRIME_DATA_NOT_FOUND` | There're no crime data in database
