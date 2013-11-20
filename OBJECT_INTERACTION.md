# ObjectInteraction
The SnapBundle™ platform Object Interaction is the major metaphor that the entire platform is designed around. An Object Interaction captures context; _this_ user utilized _this_ device and interacted with _this_ object at _this_ time at _this_ geo-rectified location and associated _this_ set of files along with _this_ set of metadata to capture the moment.

> Every Object Interaction is read-only once submitted! Neither field values can be updated nor can records be deleted once the interaction is persisted.


Object Interactions are inherently _bi-temporal_ in nature. There is the time of interaction as capture on the Device, and there is the time of record receipt as captured by the SnapBundle™ server in the cloud. These two times could have a difference of minutes, hours, or perhaps days in those instances where the Device lacked an active Internet connection.


Geo-rectification may be course, fine, or omitted altogether depending on the environment the User is in and the documentation needs of the Object Interaction. The SnapBundle™ platform does not mandate require geo-rectification.

**NOTE:** In the case of an Enterprise license, Object Interactions may be batched together under the concept of an Object Interaction Session. See the Object Interaction Session documentation for more details.

## ObjectInteraction Fields

Field | Data Type | Required | Can Update | Serialization Level | Default Value
------------ | ------------- | ------------ | ------------ | ------------ | ------------
uniqueId | long  | true | false | Restricted | Generated
urn | String  | true | false | Minimum | Generated
lastModifiedTimestamp | long   | true | false | Standard | Generated
moniker | String  | false | true | Standard | null
account | IAccount  | true | fase | Full | Generated |
user | IUser | true | false | Minimum | Generated |
object | IObject | true | false | Minimum |
device | IDevice | true | false | Minimum |
data | String | false | false | Standard | null
recordedTimestamp | long | true | false | Standard |
receivedTimestamp | long | true | false | Full | Generated
hasGeoLocation | Boolean | false | false | Standard |
lat | double | false | false | Standard |
lon | double | false | false | Standard |
alt | double | false | false | Standard |
hasSessionMembership | Boolean | false | false | Minimum |
objectInteractionSession | IObjectInteractionSession | false | false | Standard | null


## ObjectInteraction Endpoints

Endpoint | Supported HTTP Methods | Events Generated
------------ | ------------- | ------------
/app/interaction | PUT, GET, POST  | ObjectInteraction
/app/interaction/{objectInteractionUrn} | GET |
