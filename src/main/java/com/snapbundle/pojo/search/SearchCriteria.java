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

package com.snapbundle.pojo.search;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.snapbundle.model.base.EntityReferenceType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchCriteria
{
    private static List<EntityReferenceType> supportedReferenceTypes = new ArrayList<>();

    static
    {
        // TODO: Add support for Tag, File, and Metadata as applicable
        supportedReferenceTypes.add(EntityReferenceType.Device);
        supportedReferenceTypes.add(EntityReferenceType.Object);
        supportedReferenceTypes.add(EntityReferenceType.ObjectInteraction);
        supportedReferenceTypes.add(EntityReferenceType.ObjectInteractionSession);
        supportedReferenceTypes.add(EntityReferenceType.User);
    }

    protected Map<EntityReferenceType, SearchClause> criteriaMap = new HashMap<>();

    protected int limit;

    public static SearchCriteria newInstance()
    {
        return new SearchCriteria();
    }

    public static SearchCriteria fromJson(String json) throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        return mapper.readValue(json, SearchCriteria.class);
    }

    public Map<EntityReferenceType, SearchClause> getCriteriaMap()
    {
        return criteriaMap;
    }

    public int getLimit()
    {
        return limit;
    }

    public SearchCriteria setLimit(int limit)
    {
        this.limit = limit;
        return this;
    }

    public SearchCriteria addCriteria(EntityReferenceType entityReferenceType, SearchClause searchClause)
    {
        Preconditions.checkArgument(!criteriaMap.containsKey(entityReferenceType), "Search Criteria already contains a search clause for " + entityReferenceType.name());
        Preconditions.checkArgument(supportedReferenceTypes.contains(entityReferenceType), "Search Criteria doesn't support entity reference type " + entityReferenceType);

        criteriaMap.put(entityReferenceType, searchClause);
        return this;
    }

    public String toJson() throws JsonProcessingException
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        return mapper.writeValueAsString(this);
    }
}
