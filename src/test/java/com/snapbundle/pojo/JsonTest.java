/*
 * SnapBundle™ SDK
 * (C) Copyright 2013 Tag Dynamics, LLC (http://tagdynamics.com/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.snapbundle.pojo;

import com.snapbundle.model.base.EntityReferenceType;
import com.snapbundle.model.context.IAccount;
import com.snapbundle.model.context.IMetadata;
import com.snapbundle.model.context.MetadataDataType;
import com.snapbundle.pojo.context.Account;
import com.snapbundle.pojo.context.Metadata;
import com.snapbundle.pojo.context.TypeSafeMetadata;
import com.snapbundle.util.JsonGenerationView;
import junit.framework.Assert;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertTrue;


public class JsonTest
{
    @Test
    public void testAccount() throws IOException
    {
        String urn = "com.tagdynamics";
        String name = "Test Account";
        String description = "My test Description";

        IAccount testAccount = new Account();
        testAccount.setUrn(urn);
        testAccount.setName(name);
        testAccount.setDescription(description);
        testAccount.setActive(true);

        String json = testAccount.toJson(JsonGenerationView.Full.class);
        assertTrue(json != null);

        System.out.println(json);

        IAccount jsonAccount = Account.fromJson(json);

        assertTrue(jsonAccount.getName().equals(name));
        assertTrue(jsonAccount.getDescription().equals(description));
        assertTrue(jsonAccount.getUrn().equals(urn));
    }

    @Test
    public void testMetadataObject() throws IOException
    {
        final String MESSAGE = "alphabet soup";

        IMetadata metadataObject = new Metadata.MetadataObjectBuilder(MetadataDataType.StringType)
                .setEntityReferenceType(EntityReferenceType.Device)
                .setReferenceUrn("urn:uuid:12345")
                .setKey("foo")
                .setStringValue(MESSAGE)
                .build();

        String json = metadataObject.toJson(JsonGenerationView.Minimum.class);
        System.out.println(json);

        IMetadata reconstituted = Metadata.fromJson(json);

        TypeSafeMetadata<String> m = new TypeSafeMetadata<>(reconstituted);

        Assert.assertEquals(MESSAGE, m.getValue());
    }

    @Test
    public void testJsonMetadataObject() throws IOException, JSONException
    {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("foo", "bar");
        jsonObject.put("now", true);

        IMetadata metadataObject = new Metadata.MetadataObjectBuilder(MetadataDataType.JSONType)
                .setEntityReferenceType(EntityReferenceType.Device)
                .setReferenceUrn("urn:uuid:12345")
                .setKey("foo")
                .setJsonValue(jsonObject)
                .build();

        String json = metadataObject.toJson(JsonGenerationView.Minimum.class);
        System.out.println(json);

        IMetadata reconstituted = Metadata.fromJson(json);

        TypeSafeMetadata<JSONObject> m = new TypeSafeMetadata<>(reconstituted);

        Assert.assertEquals(jsonObject.get("foo"), m.getValue().get("foo"));
    }

}
