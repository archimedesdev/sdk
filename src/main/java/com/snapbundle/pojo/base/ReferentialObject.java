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

package com.snapbundle.pojo.base;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.snapbundle.model.base.EntityReferenceType;
import com.snapbundle.model.base.IReferentialObject;
import com.snapbundle.model.context.IAccount;
import com.snapbundle.pojo.context.Account;
import com.snapbundle.util.JsonGenerationView;

public abstract class ReferentialObject<T> extends DomainResource<T> implements IReferentialObject
{
    @JsonView(JsonGenerationView.Full.class)
    @JsonDeserialize(as = Account.class)
    protected IAccount account;

    @JsonView(JsonGenerationView.Minimum.class)
    protected EntityReferenceType entityReferenceType;

    @JsonView(JsonGenerationView.Minimum.class)
    protected String referenceURN;

    @Override
    public String getReferenceUrn()
    {
        return referenceURN;
    }

    @Override
    public void setReferenceUrn(String urn)
    {
        this.referenceURN = urn;
    }

    @Override
    public IAccount getAccount()
    {
        return account;
    }

    @Override
    public void setAccount(IAccount account)
    {
        this.account = account;
    }

    @Override
    public EntityReferenceType getEntityReferenceType()
    {
        return entityReferenceType;
    }

    @Override
    public void setEntityReferenceType(EntityReferenceType entityReferenceType)
    {
        this.entityReferenceType = entityReferenceType;
    }

}
