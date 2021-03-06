# Extension Framework
SnapBundle™ Extensions provide an open means for writing value-add services atop of the SnapBundle™ platform. An Extension could be authored by a third-party, or by the account owner. The SnapBundle™ Extension mechanism is built around a RESTful interface using JavaScript Object Notation (JSON) messages, which means that virtually any programming language or operating system with network access can be used.

The SnapBundle™ Extension security model uses the open standard OAuth 2.0 specification. For a detailed explanation of the SnapBundle™ OAuth security model, please refer to the SnapBundle™ OAuth 2.0 Guide _once you've finished setting up your Extension, as defined here._

## Extension
The SnapBundle™ Extension is a declaration of an external actor that may be granted access to one or more SnapBundle™ Accounts where access is restricted to a finite set of permissions.

## Extension Fields

Field | Data Type | Required | Can Update | Serialization Level | Default Value
------------ | ------------- | ------------ | ------------ | ------------ | ------------
uniqueId | long  | true | false | Restricted | Generated
urn | String  | true | false | Minimum | Generated
lastModifiedTimestamp | long   | true | false | Standard | Generated
moniker | String  | false | true | Standard | null
account | IAccount  | true | fase | Full | Generated
version | integer  | true | true | Minimum | 
name | String  | true | true | Minimum | 
description | String  | false | true | Standard | 
activeFlag | Boolean  | true | false | Standard  | 
encodedPublicKey | String  | true | false | Standard | Generated
encodedPrivateKey | String  | true  | false | Restricted | Generated 
topicArn | String  | false | true | Restricted | 
subscriptionArn | String  | false | true | Restricted | 
integrationEndpointUrl | String  | false | true | Full | 
pendingConfirmation | Boolean  | false | true | Full | 
supportEmail | String | false | true | Published |
webSiteUrl | String | false | true | Published |
clientId | String | true | false | Full | Generated |
clientSecret | String | true | false | Full | Generated |
redirectUrl | String | true | true | Full |
appCatalogUrl | String | true | false | Published | Generated
shortDescription | String | true | true | Published | 
longDescription | String | false | true | Full |
extensionType | ExtensionType | true | true | Published |
registrationTimestamp | long | true | false | Restricted | Generated

The `webSiteUrl` is the URL where an Administrator User will be directed if they want to subscribe to your Extension point. Generally, this page serves as the gateway for the User to initiate an OAuth 2 authentication process authorizing your Extension access to their data. If your Extensions requires a paid subscription, etc., then you should use this URL to solicit all required information from the User before initiating the OAuth 2 authentication process.

In contrast, the `redirectUrl` is used explicitly during the OAuth 2 authentication process. Unlike the `webSiteUrl` which is intended to be User facing, the `redirectUrl` is expected to be an endpoint capable of processing the bearer token, token expiration timestamp, and the refresh token _in addition to confirming with the User that registration has completed successfully._

The `clientId` and the `clientSecret` conceptually represent your Extension's username and password when performing an OAuth 2 handshake.
 
## Extension Endpoints

Endpoint | Supported HTTP Methods | Events Generated
------------ | ------------- | ------------
/admin/extensions | PUT, POST  | ExtensionActivated, ExtensionUpdated, ExtensionDeactivate
/extensions/{urn} | GET, DELETE | TokenRevocation, ExtensionDeleted 

## Extension Permission
The SnapBundle™ platform's Extension Permission design operates against a snapshot metaphor. The permission set your Extension publishes as required at the time the User authorizes your Extension access to their data represents the only permissions authorized for use by the Extension on that account. Conceptually, it is a model that mirrors the way Android's app security policy works. When an app changes its security permissions, the User must phyiscally review and authorize the changes; they don't instantly take effect.

Field | Data Type | Required | Can Update | Serialization Level | Default Value
------------ | ------------- | ------------ | ------------ | ------------ | ------------
uniqueId | long  | true | false | Restricted | Generated
urn | String  | true | false | Minimum | Generated
lastModifiedTimestamp | long   | true | false | Standard | Generated
moniker | String  | false | true | Standard | null
extension | IExtension | true | false | Standard | 
permissionType | PermissionType | true | false | Standard |

Each Extension must explicitly declare the security permission(s) that the extension requires. Permissions are very granular and include a concrete separation between a _read-only_ and a _write_ permission. For example, an extension may be granted `UserRead` authorization, but may not be given `UserWrite` permission. This would allow the SnapBundle™ Extension to query an Account's User listing, but not generate a new User.

Permissions are enforced _at the time an account authorizes an Extension's access request._ Stated differently, an Extension is locked into the permissions that it it requested from the user _at the time of authorization_. Any future modifications to the Extension's list of permissions is **not** reflected in any existing Account authorization grants. This prevents an Extension from requesting read-only permission at the time of authorization, and then claiming it requires write permission after the fact. If an Extension requires new permissions, it must self-revoke its current access token and request new authorization from the User's account with the new set of required permissions.

The general expectation is that most SnapBundle™ Extensions will request access to an account's Event Stream. The Event Stream is a literal stream of read-only events that collectively represent the overall activity on the account. The Event Stream includes both administrative event notifications as well as operational notifications across 50+ different event types, including the seminal MarkerDefined, MarkerInteraction, and VFSObjectUploaded events. By monitoring the Event Stream, an Extension is able to receive near real-time notification of marker interactions and respond accordingly. For example, the Event Stream could be used to monitor marker interactions for real-time billing calculations, perform deep analytical operations, etc.



## Sample Script for Extension Definnition

#### Define a New Extension
**curl** https://snapbundle.tagdynamics.net/v1/admin/extensions -u jason.weiss@therustedroof.com:tahoe65 -d '{"name" : "Event Stream Extension","redirectUrl" : "https://snapbundle.tagdynamics.net/extension/registration/complete","shortDescription" : "short descr","extensionType" : "Reporting","permissions" :[{"permission" : "EventStream"}]}'  -H "Content-Type:application/json" -X PUT

```
{
   "urn" : "urn:uuid:bc834586-4778-4c39-89f8-55e031dc6043",
   "clientId" : "973d6f32bcd2422a813b237e171d3941",
   "clientSecret" : "a46d69bcdc3c48d5a723ab7d5f754a84",
   "redirectUri" : "https://snapbundle.tagdynamics.net/extension/registration/complete"
}

```
#### Activate the Developer License on the Account for Testing
**curl** https://snapbundle.tagdynamics.net/v1/admin/account/configure/developer/true -u jason.weiss@therustedroof.com:tahoe65 -H "Content-Type:application/json" -X POST


#### Setup an HTTPS Endpoint for the Event Sink
Consider using the **stream.war** sample  web application for this step until you are comfortable with the architecture of an event sink.


#### Define the Extension Integration Endpoint for Event Stream Delivery
**curl** https://snapbundle.tagdynamics.net/v1/admin/extensions/urn:uuid:bc834586-4778-4c39-89f8-55e031dc6043/integration/enroll -u jason.weiss@therustedroof.com:tahoe65 -d '{"integrationEndpointUrl" : "https://snapbundle.tagdynamics.net/stream/event/sink"}'  -H "Content-Type:application/json" -X PUT


```
{
   "result" : 1,
   "message" : "Ensure you confirm the subscription with the registration token"
}

```
Simultaneously, the **AWS Simple Notification Service** will POST an SubscriptionConfirmation message to your endpoint. You _**MUST**_ take the `Token` from that POST and submit it through the SnapBundle™ platform. Here is a sample from the web log you can expect to see if you are using the SnapBundle™ sample **stream.war** web application:

````
Oct 27, 2013 6:57:28 PM com.snapbundle.demo.event.EventSinkResource processSubscriptionEvent
INFO: ASW subscription confirmation token:

2336412f37fb687f5d51e6e241d164b05333005609dc4f12b6c9501c15c59b3db7e63a3323efbe3a08bb40e30751a77eaeacffcd709f71b74b0215211870d9ae0cb3a4afecefd6c1e3c97e066deefae7c837fc514a67ca5d802a92d64e3be19825a5cc81a6d4bfffe661cab85e01111623705fbbf348e1340f19265ded9aec60f2cc1389c443212c28504a07656b5220


Oct 27, 2013 6:57:28 PM org.restlet.engine.log.LogFilter afterHandle
INFO: 2013-10-27	18:57:28	72.21.217.96	-	10.224.117.153	443	POST	/stream/event/sink	-	204	0	1723	127	https://snapbundle.tagdynamics.net	Amazon Simple Notification Service Agent	-
````

Using the AWS subscription confirmation token echoed above, confirm the subscription _**throught**_ SnapBundle™:

**curl** https://snapbundle.tagdynamics.net/v1/admin/extensions/urn:uuid:bc834586-4778-4c39-89f8-55e031dc6043/integration/confirm/2336412f37fb687f5d51e6e241d164b05333005609dc4f12b6c9501c15c59b3db7e63a3323efbe3a08bb40e30751a77eaeacffcd709f71b74b0215211870d9ae0cb3a4afecefd6c1e3c97e066deefae7c837fc514a67ca5d802a92d64e3be19825a5cc81a6d4bfffe661cab85e01111623705fbbf348e1340f19265ded9aec60f2cc1389c443212c28504a07656b5220 -u jason.weiss@therustedroof.com:tahoe65 -X POST


```
{
   "message" : "Integration endpoint URL has been confirmed",
   "result" : 1
}

```

At this point, the Extension is fully configured for Event Stream notification, if it is a permission requested by your extension. Regardless of whether the Extension requests the `EventStream` Permission Type or not, _**all of these steps must be completed**_.

#### Activate the Extension
The final step is to _activate_ your Extension, which has the effect of publishing your extension, announcing to all that your Extension is ready for business.  

**curl** https://snapbundle.tagdynamics.net/v1/admin/extensions -u jason.weiss@therustedroof.com:tahoe65 -d '{"urn" : "urn:uuid:bc834586-4778-4c39-89f8-55e031dc6043", "activeFlag" : true}' -H "Content-Type:application/json" -X POST

```   
{
   "urn" : "urn:uuid:bc834586-4778-4c39-89f8-55e031dc6043",
   "lastModifiedTimestamp" : 0,
   "moniker" : null,
   "name" : "Event Stream Extension",
   "description" : null,
   "activeFlag" : true,
   "encodedPublicKey" : "308201b83082012c06072a8648ce3804013082011f02818100fd7f53811d75122952df4a9c2eece4e7f611b7523cef4400c31e3f80b6512669455d402251fb593d8d58fabfc5f5ba30f6cb9b556cd7813b801d346ff26660b76b9950a5a49f9fe8047b1022c24fbba9d7feb7c61bf83b57e7c6a8a6150f04fb83f6d3c51ec3023554135a169132f675f3ae2b61d72aeff22203199dd14801c70215009760508f15230bccb292b982a2eb840bf0581cf502818100f7e1a085d69b3ddecbbcab5c36b857b97994afbbfa3aea82f9574c0b3d0782675159578ebad4594fe67107108180b449167123e84c281613b7cf09328cc8a6e13c167a8b547c8d28e0a3ae1e2bb3a675916ea37f0bfa213562f1fb627a01243bcca4f1bea8519089a883dfe15ae59f06928b665e807b552564014c3bfecf492a038185000281810086d0d4f41a5559257e4a016f78a70f0f4d82cedaefb0ff185d178221cdfea4db5ad5da0147a5eeb0266c23fa6b99dbd19802cbd81c24a97f69acd4cc7f6c98068582a9c75efe010f5bbfac100094999b2a8c7f5c31e781337c9ef6ddcf8c7b45fd243bb4c8807bb3719b29e97bc0121e7f7ca21ea210bfd1acf1e04f28534684",
   "integrationEndpointUrl" : "https://snapbundle.tagdynamics.net/stream/event/sink",
   "pendingConfirmation" : false,
   "supportEmail" : "jason.weiss@therustedroof.com",
   "webSiteUrl" : null,
   "clientId" : "973d6f32bcd2422a813b237e171d3941",
   "clientSecret" : "a46d69bcdc3c48d5a723ab7d5f754a84",
   "redirectUrl" : "https://snapbundle.tagdynamics.net/extension/registration/complete",
   "appCatalogUrl" : "public/extensions/urn:uuid:bc834586-4778-4c39-89f8-55e031dc6043",
   "shortDescription" : "short descr",
   "longDescription" : null,
   "extensionType" : "Reporting",
   "version" : 91
}

```

#### [Published Extension Catalog](id:published)
You may choose to verify that your Extension is published by using the **/public** space to query all published Extension objects.

**curl** https://snapbundle.tagdynamics.net/v1/public/extensions

```
[
   {
      "activeFlag" : true,
      "appCatalogUrl" : "public/extensions/urn:uuid:bc834586-4778-4c39-89f8-55e031dc6043",
      "description" : null,
      "encodedPublicKey" : "308201b83082012c06072a8648ce3804013082011f02818100fd7f53811d75122952df4a9c2eece4e7f611b7523cef4400c31e3f80b6512669455d402251fb593d8d58fabfc5f5ba30f6cb9b556cd7813b801d346ff26660b76b9950a5a49f9fe8047b1022c24fbba9d7feb7c61bf83b57e7c6a8a6150f04fb83f6d3c51ec3023554135a169132f675f3ae2b61d72aeff22203199dd14801c70215009760508f15230bccb292b982a2eb840bf0581cf502818100f7e1a085d69b3ddecbbcab5c36b857b97994afbbfa3aea82f9574c0b3d0782675159578ebad4594fe67107108180b449167123e84c281613b7cf09328cc8a6e13c167a8b547c8d28e0a3ae1e2bb3a675916ea37f0bfa213562f1fb627a01243bcca4f1bea8519089a883dfe15ae59f06928b665e807b552564014c3bfecf492a038185000281810086d0d4f41a5559257e4a016f78a70f0f4d82cedaefb0ff185d178221cdfea4db5ad5da0147a5eeb0266c23fa6b99dbd19802cbd81c24a97f69acd4cc7f6c98068582a9c75efe010f5bbfac100094999b2a8c7f5c31e781337c9ef6ddcf8c7b45fd243bb4c8807bb3719b29e97bc0121e7f7ca21ea210bfd1acf1e04f28534684",
      "extensionType" : "Reporting",
      "lastModifiedTimestamp" : 0,
      "name" : "Event Stream Extension",
      "shortDescription" : "short descr",
      "supportEmail" : "jason.weiss@therustedroof.com",
      "urn" : "urn:uuid:bc834586-4778-4c39-89f8-55e031dc6043",
      "webSiteUrl" : null
   }
]

```
 
