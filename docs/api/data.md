# Crime Report APIs

## Base URL

We have some base URL as listed below.

Server | URL
--------- | -----------
Production V1 | [https://saferstreets.org/rs/data]()
Production V2 | [https://saferstreets.org/rs/data/v2]()
Test V1 | [https://test.secq.me/crimereport/rs/data]()
Test V2 | [https://test.secq.me/crimereport/rs/data/v2]()

All APIs return in JSON String format

## API: (V2) 9 Grids Crime Report ( 3 x 3 Array )
### Description
Return crime reports in total of 9 grids

Get Safety Report Based on user's current location.

Removed `-` for crime report list and return all crime type without filter

### Base Url
`Use V2 Base URL`

### HTTP Request

Method | EndPoint
------ | ---------
**POST** | '/ssgridreport'

### Request Parameters Notes

Body Parameter | Type | Value
-------------- | ---- | -----
`city` | String | *Required*. Name of the city in string. Supported city will be strored inside cityInfo table.
`latitude` | Double | *Required*. User's current location, latitude is in ***Decimal Units***
`longitude` | Double | *Required*. User's current location, longitude is in ***Decimal Units***

### Response Parameters Notes

Body Parameter | Type | Value
-------------- | ---- | ------
`centerPointLat` | Double | Latitude of center point of the grid in ***Decimal Units***
`centerPointLng` | Double | Longitude of center point of the grid in ***Decimal Units***
`topRightLat` | Double | Latitude of top right of the grid in ***Decimal Units***
`topRightLng` | Double | Longitude of top right of the grid in ***Decimal Units***
`bottomLeftLat` | Double | Latitude of bottom left of the grid in ***Decimal Units***
`bottomLeftLng` | Double | Longitude of bottom left of the grid in ***Decimal Units***
`grid` | JSONObject | Holds `gridId` and `currentGrid`
`gridId` | String | Id generated based on grid report's row index and column index
`currentGrid` | Boolean | Return true when the user is inside the grid
`crimeReport` | JSONObject |  Holds `startDate`, `endDate` and `crimeTrend`
`startDate` | String | Date where the crime report starts from (2 weeks interval)
`endDate` | String | Date where the crime report ends (2 weeks interval)
`crimeTrend` | JSONArray | Holds `crimeCount` and `crimeType`
`crimeCount` | Integer | The number of count for the same crime type happened in the grid
`crimeType` | String | Crime type name that happened in the grid

> Request Body Example

```json
{
  "city":"Chicago",
  "latitude":41.882032,
  "longitude":-87.631757
}
```

> Response Example (`200 OK`)

```json
[
  {
    "safetyRatingValue": 1,
    "safetyRating": "MODERATELY SAFE",
    "bottomLeftLat": 41.883337,
    "endDate": "2016-04-27",
    "centerPointLat": 41.8855855,
    "centerPointLng": -87.635073,
    "userStreetRating": {
      "rating": 0
    },
    "topRightLat": 41.8878338,
    "topRightLng": -87.6320533,
    "grid": {
      "gridId": "chicagoCrimeGrid3050"
    },
    "bottomLeftLng": -87.638093,
    "crimeTrend": [],
    "startDate": "2016-04-14"
  },
  {
    "safetyRatingValue": -1,
    "safetyRating": "LOW SAFETY",
    "bottomLeftLat": 41.8833208,
    "endDate": "2016-04-27",
    "centerPointLat": 41.8855693,
    "centerPointLng": -87.6290329,
    "userStreetRating": {
      "rating": 0
    },
    "topRightLat": 41.8878176,
    "topRightLng": -87.6260132,
    "grid": {
      "gridId": "chicagoCrimeGrid3051"
    },
    "bottomLeftLng": -87.6320529,
    "crimeTrend": [
      {
        "crimeCount": 1,
        "crimeType": "assault"
      },
      {
        "crimeCount": 6,
        "crimeType": "battery"
      },
      {
        "crimeCount": 2,
        "crimeType": "criminal damage"
      },
      {
        "crimeCount": 1,
        "crimeType": "robbery"
      },
      {
        "crimeCount": 2,
        "crimeType": "vehicle theft"
      }
    ],
    "startDate": "2016-04-14"
  },
  {
    "safetyRatingValue": 1,
    "safetyRating": "MODERATELY SAFE",
    "bottomLeftLat": 41.8833044,
    "endDate": "2016-04-27",
    "centerPointLat": 41.8855529,
    "centerPointLng": -87.6229928,
    "userStreetRating": {
      "rating": 0
    },
    "topRightLat": 41.8878012,
    "topRightLng": -87.6199731,
    "grid": {
      "gridId": "chicagoCrimeGrid3052"
    },
    "bottomLeftLng": -87.6260128,
    "crimeTrend": [],
    "startDate": "2016-04-14"
  },
  {
    "safetyRatingValue": -1,
    "safetyRating": "LOW SAFETY",
    "bottomLeftLat": 41.8788404,
    "endDate": "2016-04-27",
    "centerPointLat": 41.8810889,
    "centerPointLng": -87.6350945,
    "userStreetRating": {
      "rating": 0
    },
    "topRightLat": 41.8833372,
    "topRightLng": -87.632075,
    "grid": {
      "gridId": "chicagoCrimeGrid3150"
    },
    "bottomLeftLng": -87.6381143,
    "crimeTrend": [
      {
        "crimeCount": 2,
        "crimeType": "criminal trespass"
      },
      {
        "crimeCount": 2,
        "crimeType": "vehicle theft"
      }
    ],
    "startDate": "2016-04-14"
  },
  {
    "safetyRatingValue": -1,
    "safetyRating": "LOW SAFETY",
    "bottomLeftLat": 41.8788243,
    "endDate": "2016-04-27",
    "centerPointLat": 41.8810728,
    "centerPointLng": -87.6290548,
    "userStreetRating": {
      "rating": 0
    },
    "topRightLat": 41.8833211,
    "topRightLng": -87.6260353,
    "grid": {
      "gridId": "chicagoCrimeGrid3151",
      "currentGrid": true
    },
    "bottomLeftLng": -87.6320746,
    "crimeTrend": [
      {
        "crimeCount": 1,
        "crimeType": "robbery"
      }
    ],
    "startDate": "2016-04-14"
  },
  {
    "safetyRatingValue": -1,
    "safetyRating": "LOW SAFETY",
    "bottomLeftLat": 41.8788078,
    "endDate": "2016-04-27",
    "centerPointLat": 41.8810563,
    "centerPointLng": -87.6230151,
    "userStreetRating": {
      "rating": 0
    },
    "topRightLat": 41.8833046,
    "topRightLng": -87.6199956,
    "grid": {
      "gridId": "chicagoCrimeGrid3152"
    },
    "bottomLeftLng": -87.6260349,
    "crimeTrend": [
      {
        "crimeCount": 13,
        "crimeType": "theft"
      }
    ],
    "startDate": "2016-04-14"
  },
  {
    "safetyRatingValue": 1,
    "safetyRating": "MODERATELY SAFE",
    "bottomLeftLat": 41.8743439,
    "endDate": "2016-04-27",
    "centerPointLat": 41.8765924,
    "centerPointLng": -87.635116,
    "userStreetRating": {
      "rating": 0
    },
    "topRightLat": 41.8788407,
    "topRightLng": -87.6320966,
    "grid": {
      "gridId": "chicagoCrimeGrid3250"
    },
    "bottomLeftLng": -87.6381355,
    "crimeTrend": [],
    "startDate": "2016-04-14"
  },
  {
    "safetyRatingValue": -1,
    "safetyRating": "LOW SAFETY",
    "bottomLeftLat": 41.8743278,
    "endDate": "2016-04-27",
    "centerPointLat": 41.8765763,
    "centerPointLng": -87.6290767,
    "userStreetRating": {
      "rating": 0
    },
    "topRightLat": 41.8788246,
    "topRightLng": -87.6260573,
    "grid": {
      "gridId": "chicagoCrimeGrid3251"
    },
    "bottomLeftLng": -87.6320962,
    "crimeTrend": [
      {
        "crimeCount": 6,
        "crimeType": "battery"
      },
      {
        "crimeCount": 5,
        "crimeType": "robbery"
      }
    ],
    "startDate": "2016-04-14"
  },
  {
    "safetyRatingValue": 0,
    "safetyRating": "MODERATE",
    "bottomLeftLat": 41.8743113,
    "endDate": "2016-04-27",
    "centerPointLat": 41.8765598,
    "centerPointLng": -87.6230375,
    "userStreetRating": {
      "rating": 0
    },
    "topRightLat": 41.8788081,
    "topRightLng": -87.6200181,
    "grid": {
      "gridId": "chicagoCrimeGrid3252"
    },
    "bottomLeftLng": -87.626057,
    "crimeTrend": [
      {
        "crimeCount": 1,
        "crimeType": "battery"
      },
      {
        "crimeCount": 1,
        "crimeType": "criminal damage"
      }
    ],
    "startDate": "2016-04-14"
  }
]
```

---

## API: (V1) 9 Grids Crime Report ( 3 x 3 Array )

### Descriptions
Return crime reports in total of 9 grids

Get Safety Report Based on user's current location.

### Base url
`use V1 Base URL`

### HTTP Request

Method | EndPoint
------ | ---------
**POST** | '/saferstreetscrimerating'

### Request Parameters Notes

Body Parameter | Type | Value
-------------- | ---- | -----
`city` | String | *Required*. Name of the city in string. Supported city will be strored inside cityInfo table.
`latitude` | Double | *Required*. User's current location, latitude is in ***Decimal Units***
`longitude` | Double | *Required*. User's current location, longitude is in ***Decimal Units***

### Response Parameters Notes

Body Parameter | Type | Value
-------------- | ---- | ------
`centerPointLat` | Double | Latittude of center point of the grid in ***Decimal Units***
`centerPointLng` | Double | Longitude of center point of the grid in ***Decimal Units***
`topRightLat` | Double | Latitude of top right of the grid in ***Decimal Units***
`topRightLng` | Double | Longitude of top right of the grid in ***Decimal Units***
`bottomLeftLat` | Double | Latitude of bottom left of the grid in ***Decimal Units***
`bottomLeftLng` | Double | Longitude of bottom left of the grid in ***Decimal Units***
`grid` | JSONObject | Holds `gridId` and `currentGrid`
`gridId` | String | Id generated based on grid report's row index and column index
`currentGrid` | Boolean | Return true when the user is inside the grid
`crimeReport` | JSONObject |  Holds `startDate`, `endDate` and `crimeTrend`
`startDate` | String | Date where the crime report starts from (2 weeks interval)
`endDate` | String | Date where the crime report ends (2 weeks interval)
`crimeTrend` | JSONArray | Holds `crimeCount` and `crimeType`
`crimeCount` | Integer | The number of count for the same crime type happened in the grid
`crimeType` | String | Crime type name that happened in the grid


> Request Body Example

```json
{
  "city":"Chicago",
  "latitude":41.882032,
  "longitude":-87.631757
}
```

> Response Example (`200 OK`)

```json
{
  "gridCrimeReportList": [
    {
      "topRightLat": 41.8894966,
      "safetyRating": "LOW SAFETY",
      "bottomLeftLat": 41.8849998,
      "topRightLng": -87.6330489,
      "centerPointLat": 41.8872483,
      "grid": {
        "gridId": "ChicagoCrimeGrid3051"
      },
      "centerPointLng": -87.6360688,
      "bottomLeftLng": -87.6390888,
      "crimeReport": {
        "endDate": "2015-05-05",
        "crimeTrend": [
          {
            "crimeCount": 1,
            "crimeType": "Theft"
          },
          {
            "crimeCount": " - ",
            "crimeType": " - "
          },
          {
            "crimeCount": " - ",
            "crimeType": " - "
          },
          {
            "crimeCount": " - ",
            "crimeType": " - "
          }
        ],
        "startDate": "2015-04-21"
      },
      "userStreetRating": {
        "rating": 0
      }
    },
    {
      "topRightLat": 41.8894801,
      "safetyRating": "LOW SAFETY",
      "bottomLeftLat": 41.8849833,
      "topRightLng": -87.6270086,
      "centerPointLat": 41.8872318,
      "grid": {
        "gridId": "ChicagoCrimeGrid3052"
      },
      "centerPointLng": -87.6300285,
      "bottomLeftLng": -87.6330485,
      "crimeReport": {
        "endDate": "2015-05-05",
        "crimeTrend": [
          {
            "crimeCount": 2,
            "crimeType": "Theft"
          },
          {
            "crimeCount": " - ",
            "crimeType": " - "
          },
          {
            "crimeCount": " - ",
            "crimeType": " - "
          },
          {
            "crimeCount": " - ",
            "crimeType": " - "
          }
        ],
        "startDate": "2015-04-21"
      },
      "userStreetRating": {
        "rating": 0
      }
    },
    {
      "topRightLat": 41.8894634,
      "safetyRating": "LOW SAFETY",
      "bottomLeftLat": 41.8849666,
      "topRightLng": -87.6209684,
      "centerPointLat": 41.8872151,
      "grid": {
        "gridId": "ChicagoCrimeGrid3053"
      },
      "centerPointLng": -87.6239883,
      "bottomLeftLng": -87.6270083,
      "crimeReport": {
        "endDate": "2015-05-05",
        "crimeTrend": [
          {
            "crimeCount": 1,
            "crimeType": "Theft"
          },
          {
            "crimeCount": " - ",
            "crimeType": " - "
          },
          {
            "crimeCount": " - ",
            "crimeType": " - "
          },
          {
            "crimeCount": " - ",
            "crimeType": " - "
          }
        ],
        "startDate": "2015-04-21"
      },
      "userStreetRating": {
        "rating": 0
      }
    },
    {
      "topRightLat": 41.8850001,
      "safetyRating": "LOW SAFETY",
      "bottomLeftLat": 41.8805033,
      "topRightLng": -87.633071,
      "centerPointLat": 41.8827518,
      "grid": {
        "gridId": "ChicagoCrimeGrid3151"
      },
      "centerPointLng": -87.6360907,
      "bottomLeftLng": -87.6391105,
      "crimeReport": {
        "endDate": "2015-05-05",
        "crimeTrend": [
          {
            "crimeCount": 1,
            "crimeType": "Criminal Damage"
          },
          {
            "crimeCount": " - ",
            "crimeType": " - "
          },
          {
            "crimeCount": " - ",
            "crimeType": " - "
          },
          {
            "crimeCount": " - ",
            "crimeType": " - "
          }
        ],
        "startDate": "2015-04-21"
      },
      "userStreetRating": {
        "rating": 0
      }
    },
    {
      "topRightLat": 41.8849836,
      "safetyRating": "LOW SAFETY",
      "bottomLeftLat": 41.8804868,
      "topRightLng": -87.6270311,
      "centerPointLat": 41.8827353,
      "grid": {
        "gridId": "ChicagoCrimeGrid3152",
        "currentGrid": true
      },
      "centerPointLng": -87.6300508,
      "bottomLeftLng": -87.6330706,
      "crimeReport": {
        "endDate": "2015-05-05",
        "crimeTrend": [
          {
            "crimeCount": 3,
            "crimeType": "Assault"
          },
          {
            "crimeCount": 1,
            "crimeType": "Theft"
          },
          {
            "crimeCount": " - ",
            "crimeType": " - "
          },
          {
            "crimeCount": " - ",
            "crimeType": " - "
          }
        ],
        "startDate": "2015-04-21"
      },
      "userStreetRating": {
        "rating": 0
      }
    },
    {
      "topRightLat": 41.8849668,
      "safetyRating": "LOW SAFETY",
      "bottomLeftLat": 41.88047,
      "topRightLng": -87.6209913,
      "centerPointLat": 41.8827185,
      "grid": {
        "gridId": "ChicagoCrimeGrid3153"
      },
      "centerPointLng": -87.624011,
      "bottomLeftLng": -87.6270308,
      "crimeReport": {
        "endDate": "2015-05-05",
        "crimeTrend": [
          {
            "crimeCount": 1,
            "crimeType": "Battery"
          },
          {
            "crimeCount": " - ",
            "crimeType": " - "
          },
          {
            "crimeCount": " - ",
            "crimeType": " - "
          },
          {
            "crimeCount": " - ",
            "crimeType": " - "
          }
        ],
        "startDate": "2015-04-21"
      },
      "userStreetRating": {
        "rating": 0
      }
    },
    {
      "topRightLat": 41.8805035,
      "safetyRating": "LOW SAFETY",
      "bottomLeftLat": 41.8760067,
      "topRightLng": -87.6330932,
      "centerPointLat": 41.8782552,
      "grid": {
        "gridId": "ChicagoCrimeGrid3251"
      },
      "centerPointLng": -87.6361126,
      "bottomLeftLng": -87.6391322,
      "crimeReport": {
        "endDate": "2015-05-05",
        "crimeTrend": [
          {
            "crimeCount": 2,
            "crimeType": "Theft"
          },
          {
            "crimeCount": " - ",
            "crimeType": " - "
          },
          {
            "crimeCount": " - ",
            "crimeType": " - "
          },
          {
            "crimeCount": " - ",
            "crimeType": " - "
          }
        ],
        "startDate": "2015-04-21"
      },
      "userStreetRating": {
        "rating": 0
      }
    },
    {
      "topRightLat": 41.8804871,
      "safetyRating": "LOW SAFETY",
      "bottomLeftLat": 41.8759903,
      "topRightLng": -87.6270537,
      "centerPointLat": 41.8782388,
      "grid": {
        "gridId": "ChicagoCrimeGrid3252"
      },
      "centerPointLng": -87.6300731,
      "bottomLeftLng": -87.6330927,
      "crimeReport": {
        "endDate": "2015-05-05",
        "crimeTrend": [
          {
            "crimeCount": 3,
            "crimeType": "Theft"
          },
          {
            "crimeCount": 1,
            "crimeType": "Crim Sexual Assault"
          },
          {
            "crimeCount": " - ",
            "crimeType": " - "
          },
          {
            "crimeCount": " - ",
            "crimeType": " - "
          }
        ],
        "startDate": "2015-04-21"
      },
      "userStreetRating": {
        "rating": 0
      }
    },
    {
      "topRightLat": 41.8804703,
      "safetyRating": "LOW SAFETY",
      "bottomLeftLat": 41.8759735,
      "topRightLng": -87.6210143,
      "centerPointLat": 41.878222,
      "grid": {
        "gridId": "ChicagoCrimeGrid3253"
      },
      "centerPointLng": -87.6240337,
      "bottomLeftLng": -87.6270533,
      "crimeReport": {
        "endDate": "2015-05-05",
        "crimeTrend": [
          {
            "crimeCount": 2,
            "crimeType": "Criminal Damage"
          },
          {
            "crimeCount": 1,
            "crimeType": "Sex Offense"
          },
          {
            "crimeCount": 1,
            "crimeType": "Theft"
          },
          {
            "crimeCount": " - ",
            "crimeType": " - "
          }
        ],
        "startDate": "2015-04-21"
      },
      "userStreetRating": {
        "rating": 0
      }
    }
  ]
}
```

---

## API: Current Grid Report (One Report)

### Descriptions
Return single grid report based on user's location

### HTTP Request

Method | Version | Base Url | EndPoint
------ | ------- | -------- | --------
***POST*** | V1 | V1 | '/currentgridsafetyreport'
***POST*** | V2 | v2 | '/gridcrimereport'

### Request Parameters Notes

Body Parameter | Type | Value
-------------- | ---- | -----
`city` | String | *Required*. Name of the city in string. Supported city will be strored inside cityInfo table.
`latitude` | Double | *Required*. User's current location, latitude is in ***Decimal Units***
`longitude` | Double | *Required*. User's current location, longitude is in ***Decimal Units***

### Respond Parameters Notes

Body Parameter | Type | Value
-------------- | ---- | -----
`safetyRatingValue` | Integer | Return 1 (as Moderately Safe), 0 (as Moderate) and -1 (as Low Safety)
`centerPointLat` | Double | Latittude of center point of the grid in ***Decimal Units***
`centerPointLng` | Double | Longitude of center point of the grid in ***Decimal Units***
`topRightLat` | Double | Latitude of top right of the grid in ***Decimal Units***
`topRightLng` | Double | Longitude of top right of the grid in ***Decimal Units***
`bottomLeftLat` | Double | Latitude of bottom left of the grid in ***Decimal Units***
`bottomLeftLng` | Double | Longitude of bottom left of the grid in ***Decimal Units***
`startDate` | String | Date where the crime report starts from (2 weeks interval)
`endDate` | String | Date where the crime report ends (2 weeks interval)
`crimeTrend` | JSONArray | Holds `crimeCount` and `crimeType`
`crimeCount` | Integer | The number of count for the same crime type happened in the grid
`crimeType` | String | Crime type name that happened in the grid
`latitude` | Double | Latitude of the crime happened
`longtiude` | Double | Longitude of the crime happened
`crimeDate` | String | Date that crime happened

> Request Body Example

```json
{
  "city":"Chicago",
  "latitude":41.882032,
  "longitude":-87.631757
}
```

> Respond Body Example

```json
{
  "safetyRatingValue": -1,
  "topRightLat": 41.8849836,
  "safetyRating": "LOW SAFETY",
  "bottomLeftLat": 41.8804868,
  "topRightLng": -87.6270311,
  "endDate": "2015-05-05",
  "centerPointLat": 41.8827353,
  "centerPointLng": -87.6300508,
  "bottomLeftLng": -87.6330706,
  "crimeTrend": [
    {
      "crimeCount": 25,
      "crimeType": "Theft"
    },
    {
      "crimeCount": 6,
      "crimeType": "Assault"
    },
    {
      "crimeCount": 3,
      "crimeType": "Criminal Trespass"
    },
    {
      "crimeCount": 2,
      "crimeType": "Battery"
    }
  ],
  "startDate": "2015-04-21",
  "crimeList": [
    {
      "latitude": 41.88563789,
      "crimeDate": "2015-05-05",
      "crimeType": "Theft",
      "longitude": -87.629208298
    },
    {
      "latitude": 41.883480076,
      "crimeDate": "2015-05-05",
      "crimeType": "Theft",
      "longitude": -87.627730028
    },
    {
      "latitude": 41.883989989,
      "crimeDate": "2015-05-05",
      "crimeType": "Robbery",
      "longitude": -87.632575539
    },
    {
      "latitude": 41.883480076,
      "crimeDate": "2015-05-05",
      "crimeType": "Theft",
      "longitude": -87.627730028
    },
    {
      "latitude": 41.883480076,
      "crimeDate": "2015-05-04",
      "crimeType": "Theft",
      "longitude": -87.627730028
    },
    {
      "latitude": 41.88328152,
      "crimeDate": "2015-05-04",
      "crimeType": "Theft",
      "longitude": -87.628018649
    },
    {
      "latitude": 41.883480076,
      "crimeDate": "2015-05-03",
      "crimeType": "Theft",
      "longitude": -87.627730028
    },
    {
      "latitude": 41.884556039,
      "crimeDate": "2015-05-03",
      "crimeType": "Criminal Trespass",
      "longitude": -87.627765929
    },
    {
      "latitude": 41.884552356,
      "crimeDate": "2015-05-02",
      "crimeType": "Theft",
      "longitude": -87.628059757
    },
    {
      "latitude": 41.884556039,
      "crimeDate": "2015-05-02",
      "crimeType": "Battery",
      "longitude": -87.627765929
    },
    {
      "latitude": 41.883480076,
      "crimeDate": "2015-04-30",
      "crimeType": "Theft",
      "longitude": -87.627730028
    },
    {
      "latitude": 41.883480076,
      "crimeDate": "2015-04-30",
      "crimeType": "Theft",
      "longitude": -87.627730028
    },
    {
      "latitude": 41.883480076,
      "crimeDate": "2015-04-30",
      "crimeType": "Theft",
      "longitude": -87.627730028
    },
    {
      "latitude": 41.883480076,
      "crimeDate": "2015-04-30",
      "crimeType": "Theft",
      "longitude": -87.627730028
    },
    {
      "latitude": 41.884556039,
      "crimeDate": "2015-04-29",
      "crimeType": "Theft",
      "longitude": -87.627765929
    },
    {
      "latitude": 41.883480076,
      "crimeDate": "2015-04-29",
      "crimeType": "Theft",
      "longitude": -87.627730028
    },
    {
      "latitude": 41.883480076,
      "crimeDate": "2015-04-29",
      "crimeType": "Theft",
      "longitude": -87.627730028
    },
    {
      "latitude": 41.885632799,
      "crimeDate": "2015-04-29",
      "crimeType": "Criminal Trespass",
      "longitude": -87.631514623
    },
    {
      "latitude": 41.886350095,
      "crimeDate": "2015-04-29",
      "crimeType": "Assault",
      "longitude": -87.628102416
    },
    {
      "latitude": 41.886707264,
      "crimeDate": "2015-04-29",
      "crimeType": "Theft",
      "longitude": -87.630408469
    },
    {
      "latitude": 41.885411545,
      "crimeDate": "2015-04-28",
      "crimeType": "Assault",
      "longitude": -87.632582039
    },
    {
      "latitude": 41.884592135,
      "crimeDate": "2015-04-27",
      "crimeType": "Criminal Trespass",
      "longitude": -87.629175712
    },
    {
      "latitude": 41.883480076,
      "crimeDate": "2015-04-26",
      "crimeType": "Assault",
      "longitude": -87.627730028
    },
    {
      "latitude": 41.883480076,
      "crimeDate": "2015-04-25",
      "crimeType": "Theft",
      "longitude": -87.627730028
    },
    {
      "latitude": 41.884601798,
      "crimeDate": "2015-04-25",
      "crimeType": "Motor Vehicle Theft",
      "longitude": -87.627174178
    },
    {
      "latitude": 41.884426107,
      "crimeDate": "2015-04-24",
      "crimeType": "Battery",
      "longitude": -87.628057473
    },
    {
      "latitude": 41.885437062,
      "crimeDate": "2015-04-24",
      "crimeType": "Robbery",
      "longitude": -87.627785614
    },
    {
      "latitude": 41.885850791,
      "crimeDate": "2015-04-24",
      "crimeType": "Theft",
      "longitude": -87.631710548
    },
    {
      "latitude": 41.884591822,
      "crimeDate": "2015-04-24",
      "crimeType": "Theft",
      "longitude": -87.630020355
    },
    {
      "latitude": 41.88561391,
      "crimeDate": "2015-04-24",
      "crimeType": "Theft",
      "longitude": -87.631118211
    },
    {
      "latitude": 41.886707265,
      "crimeDate": "2015-04-23",
      "crimeType": "Narcotics",
      "longitude": -87.629960428
    },
    {
      "latitude": 41.886708258,
      "crimeDate": "2015-04-23",
      "crimeType": "Theft",
      "longitude": -87.627885474
    },
    {
      "latitude": 41.885850791,
      "crimeDate": "2015-04-22",
      "crimeType": "Theft",
      "longitude": -87.631710548
    },
    {
      "latitude": 41.883480076,
      "crimeDate": "2015-04-22",
      "crimeType": "Theft",
      "longitude": -87.627730028
    }
  ]
}
```


---

## API: User Street Rating

### Descriptions
Return streets rating that created by users

### HTTP Request

Method | Version | Base Url | EndPoint
------ | ------- | -------- | --------
**POST** | V1 | V1 | '/userstreetsrating'
**POST** | V2 | V2 | '/newuserssrating'

### Request Parameters Notes

Body Parameter | Type | Value
-------------- | ---- | ------
`authToken` | String |  Authentication token from Watch Over Me user
`location` | JSONObject | *Required*. Holds `latitude`, `longitude`, and `accuracy`
`latitude` | Double | *Required*. Latitude of the current user in ***Decimal Units***
`longitude` | Double | *Required*. Longitude of the currnet user in ***Decimal Units***
`address` | JSONObject | *Required*. Holds `streetName`, `city`, `state`, `country` and `postcode`
`streetName` | String | *Required*. Name of the street that user is currently in
`city` | String | *Required*. City name
`country` | String | *Required*. Two-digits country code. See [ISO 3166-1 alpha-2](http://en.wikipedia.org/wiki/ISO_3166-1_alpha-2#Officially_assigned_code_elements).
`postcode` | String | *Required*. Postcode
`userRating` | Integer | *Required*. Streets rating from the user
`userid` | String | Watch Over Me user's phone number will be taken as unique id


### Respond Parameters Notes

Body Parameter | Type | Value
-------------- | ---- | ------
`status` | String | Okay


> Request Body Example

```json
{  
  "authToken":"b1d08a7d-f7b4-4944-984f-55930af23d0f",
  "location":{  
    "latitude":41.882814,
    "longitude":-87.6293277,
    "accuracy":"100"
  },
  "address":{  
    "streetName":"Dearborn St",
    "city":"Chicago",
    "state":"IL",
    "country":"US",
    "postcode":60602
  },
  "userRating":3,
  "userid":"6012-4689026"
}
```

> Response Body Example

```json
{
  "status":"OK"
}
```

---

## API: New User Request for SaferStreets Support

### Descriptions
User request for saferstreets support based on their location (city)

### HTTP Request
Method | Version | Base Url | EndPoint
------ | ------- | -------- | --------
***POST*** | V1 | V1 | `/saferstreetsrequest`
***POST*** | V2 | V2 | `/newssrequest`

### Request Parameter Notes

Body Parameter | Type | Value
-------------- | ---- | ------
`authToken` | String | Unique Id passed from Watch Over Me user
`userid` | String | *Required*. Phone number of Watch Over Me
`email` | String | *Required*. Request from SaferStreets Website
`name` | String | *Required*. User's name that request from SaferStreets Website
`city` | String | *Required*. Name of the city
`state` | String | *Required*. State of the country
`country` | String  | *Required*. Two-digits country code. See [ISO 3166-1 alpha-2](http://en.wikipedia.org/wiki/ISO_3166-1_alpha-2#Officially_assigned_code_elements).
`postcode` | String | Postcode

### Respond Parameter Notes
Body Parameter | Type | Value
-------------- | ---- | -----
`userRequested` | Boolean | Return true when user had requested
`requestCount` | Integer | Total number of requested for SaferStreets Support


> Request Body Example 1

```json
{  
  "authToken":"b1d08a7d-f7b4-4944-984f-55930af23d0f",
  "userid":"60-14920902",
  "city":"Chicago",
  "state":"Illinois",
  "country":"US",  /* ISO 3166-1 alpha-2, do not use long name */
  "postcode":60604 /* Optional */
}
```

> Request Body Example 2

```json
{  
  "email":"john.doe@example.com",
  "name":"John Doe",
  "city":"Chicago",
  "state":"Illinois",
  "country":"US",  /* ISO 3166-1 alpha-2, do not use long name */
  "postcode":60604 /* Optional */
}
```

> Respond Body Example

```json
{
  "userRequested" : true,
  "requestCount" : 2
}
```

---

## API: Check User Request for SaferStreets Support

### Descriptions
Check that there's any requested record in the database

### HTTP Request
Method | Version | Base Url | EndPoint
------ | ------- | --------- | --------
***POST*** | V1 | V1 | '/checksupportrequest'
***POST*** | V2 | V2 | '/checkssrequest'

### Request Parameter Notes

Body Parameter | Type | Value
-------------- | ---- | ------
`authToken` | String | Unique Id passed from Watch Over Me user
`userid` | String | *Required*. Phone number of Watch Over Me
`email` | String | *Required*. Request from SaferStreets Website
`name` | String | *Required*. User's name that request from SaferStreets Website
`city` | String | *Required*. Name of the city
`state` | String | *Required*. State of the country
`country` | String  | *Required*. Two-digits country code. See [ISO 3166-1 alpha-2](http://en.wikipedia.org/wiki/ISO_3166-1_alpha-2#Officially_assigned_code_elements).
`postcode` | String | Postcode

### Respond Parameter Notes
Body Parameter | Type | Value
-------------- | ---- | -----
`userRequested` | Boolean | Return true when user had requested
`requestCount` | Integer | Total number of requested for SaferStreets Support


> Request Body Example 1

```json
{  
  "authToken":"b1d08a7d-f7b4-4944-984f-55930af23d0f",
  "userid":"60-14920902",
  "city":"Chicago",
  "state":"Illinois", /* Optional, but recommended */
  "country":"US",     /* Optional, but recommended, ISO 3166-1 alpha-2 */
}
```

> Request Body Example 2

```json
{  
  "email":"john.doe@example.com",
  "city":"Chicago",
  "name":"Edmund",
  "state":"Illinois", /* Optional, but recommended */
  "country":"US",     /* Optional, but recommended, ISO 3166-1 alpha-2 */
}
```

> Respond Body Example

```json
{
  "userRequested" : true,
  "requestCount" : 2
}
```

---

## API: Check Support City in SaferStreets

### Descriptions
Return true when city is supported in SaferStreets

### HTTP Request

|Method | Version | Base Url | EndPoint
------ | ------- | -------- | --------
***POST*** | V1 | V1 |  '/checksupportcity'
***POST*** | V2 | V2 | '/checksupportcity'

### Request Parameter Notes

Body Parameter | Type | Value
-------------- | ---- | -----
`city` | String | *Required*. Name of the city
`state` | String | *Required*. State of the country
`country` | String  | *Required*. Two-digits country code. See [ISO 3166-1 alpha-2](http://en.wikipedia.org/wiki/ISO_3166-1_alpha-2#Officially_assigned_code_elements).
`postcode` | String | Postcode
`latitude` | Double | *Required*. Latitude of the current user in ***Decimal Units***
`longitude` | Double | *Required*. Longitude of the currnet user in ***Decimal Units***

### Respond Parameter Notes

Body Parameter | Type | Value
-------------- | ---- | -----
`citySupport` | Boolean | Return true if city is supported


> Request Body Example

```json            
{
  "city":"San Francisco",
  "state":"California",
  "country":"US",
  "latitude":37.780361,
  "longitude":-122.418031
}
```

> Respond Body Example
```json

> Respond Body Example

```json
{
  "citySupport":true
}
```

---
