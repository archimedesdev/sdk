# Metadata
The SnapBundle™ platform provides a powerful type-safe key-value pair mechanism to capture additional data beyond the platform's core data model. Similar to the [File](FILE.md "File"), a collection of metadata can be associated with any of the platform's [primary data types](DATA_TYPES.md "Data Types"), literally. There is no physical limit on the number of key-value pair associations.

## Metadata Fields

Field | Data Type | Required | Can Update | Serialization Level | Default Value
------------ | ------------- | ------------ | ------------ | ------------ | ------------
uniqueId | long  | true | false | Restricted | Generated
urn | String  | true | false | Minimum | Generated
lastModifiedTimestamp | long   | true | false | Standard | Generated
moniker | String  | false | true | Standard | null
account | IAccount  | true | fase | Full | Generated
entityReferenceType | EntityReferenceType | true | false | Minimum |
referenceURN | String | true | false | Minimum |
dataType | MetadataDataType | true | false | Minimum |
key | String | true | false | Minimum |
rawValue | byte[] | true | true | Minimum |

Generally speaking, if the `rawValue` is going to be over several kilobytes in size, developers are **strongly** encouraged to store the data in a more efficient manner: as a [File](FILE.MD "File").

The `rawValue` is persisted as a byte[] (stored as a blob). Developers must either use the MetadataObject.MetadataObjectBuilder class, which follows the Builder design pattern and ensures a canonical encoding of type-safe data into the byte[], or use one of the publicly accessible mapping endpoints described below that accept any of the given metadata datatypes, responding back with a `text/plain` String that is suitable for submission with the MetadataObject record.

> The `rawValue` should never be generated by hand unless the MetaDataType is specified as `Custom`. 

The `Custom` MetaDataType places the onus entirely on the developer to provide a meaningful encoding/decoding capability! One common use of `Custom` is when developers are relying on a serialization framework like Google's Protocol Buffers or Hessian binary web service protocol.

### `rawValue` Encoding Endpoints

Endpoint | Supported HTTP Methods | Events Generated
------------ | ------------- | ------------
/public/metadata/mapper/encode/{metadataDataType} | POST  | 
/public/metadata/mapper/decode/{metadataDataType} | POST  | 

The _encode_ endpoint is expecting the body of the POST to be `text/plain`, and the response back will be JSON object with a single field, `rawValue` that represents the encoded byte[] that the platform knows how to successfully decode based on the declared metadataDataType.

The _decode_ endpoint is expecting the body of the POST to be `text/plain`, and the response back will be a JSON object with a single field, `value`, and the value will either be represented in JSON as the appropriate data type (e.g. int, long, Boolean, float, double, String) or in the case of Custom, JSONType, and XML, a String data type.

> If you are building with the SnapBundle™ Java SDK, then you should just use one of the Mapper classes found in the `com.snapbundle.util.mapper` package.

#### How to use the Encoding Endpoint Response
The submission of a Metadata record is always done using JSON. The following JSON array shows how the encoded `rawValue` returned from the encoding endpoint can be dropped into the JSON array that is being to be submited to the platform:

````
[
    {
        "key": "alpha",
        "type": "JSONType",
        "rawValue": "ewogICAiZm9vIjogImJhciIsCiAgICJub3ciOiB0cnVlCn0="
    },
    {
        "key": "bravo",
        "type": "JSONType",
        "rawValue": "ewogICAiZm9vIjogImJhciIsCiAgICJub3ciOiB0cnVlCn0="
    }
]
````
In effect, the encoding endpoint response is just placed into the Metadata JSON that is going to be sent up to the platform, ensuring that the `type` matches the `metadataDataType` used to encode the data. When the encoded data reaches the platform, it knows how to properly decode the data back into a byte[] for proper storage.


## Metadata Endpoints

Endpoint | Supported HTTP Methods | Events Generated
------------ | ------------- | ------------
/app/metadata/{entityReferenceType}/{referenceUrn} | PUT  | MetadataUpserted
/app/metadata/{entityReferenceType}/{referenceUrn}/{key} | DELETE | MetadataDeleted
/app/metadata/query/{entityReferenceType}/{referenceUrn} | GET |
/app/metadata/query/{entityReferenceType}/{referenceUrn}/{key} | GET |

The HTTP PUT endpoint uses an **upsert** algorithm, inserting the metadata record(s) if the named key doesn't already exist, or updating the value (and only the value) of the metadata record if the key already exists.

The HTTP PUT operation expects the submission of a JSON array. The design allows for the rapid definition of multiple metadata key-value pairs in a single operation.

The HTTP DELETE operation deletes the specific named key, if it exists, under the referenced object. If the key doesn't exist, the operation fails silently, returning a **200** status.

The two HTTP GET query operations return all the metadata key-values for the specified reference object in a JSON array, or a single key-value pair for the specific key as a JSON object.

