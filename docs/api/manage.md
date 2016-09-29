# Data Resources - Internal Use

## Base URL

We have some base URL as listed below.

Server | URL
------ | ----
Production | [https://saferstreets.org/rs/manage]()
Test | [https://test.secq.me/crimereport/rs/manage]()


## Update Crime DayTime

> Request Body Example

```json
{

}
```

> Respond Body Example

```json
{
	"Total Updated" : 1234
}
```

Update Crime DayTime based on crimeDate in crimeData

<aside class="notice">
	Internal Use
</aside>

### HTTP Request

Method | EndPoint
------ | --------
***POST*** | '/updatecrimedaytime'

### Request Parameters Notes

Body Parameter | Type | Value
-------------- | ---- | ------


### Response Parameters Notes

Body Parameter | Type | Value
-------------- | ---- | ------
`Total Updated` | Integer | Total number of crimeData being updated with crimeDayTime

---

## Check Time Zone

> Request Body Example

```json
{
	"timeZone":"America/Chicago"
}
```

> Respond Body Example

```json
{
	"DayTime" : "MORNING"
}
```

Check Time Zone

<aside class="notice">
	Internal Use
</aside>


### HTTP Request

Method | EndPoint
------ | --------
***POST*** | '/checktimezone'

### Request Parameters Notes

Body Parameter | Type | Value
-------------- | ---- | ------
`timeZone` | String | Name Time Zone

### Response Parameters Notes

Body Parameter | Type | Value
-------------- | ---- | ------
`DayTime` | String | Return enum of MORNING, DAYTIME, NIGHT
