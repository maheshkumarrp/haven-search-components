/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.parametricvalues;

import com.hp.autonomy.hod.client.api.resource.ResourceName;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.searchcomponents.core.parametricvalues.AbstractParametricValuesServiceIT;
import com.hp.autonomy.searchcomponents.core.parametricvalues.ParametricValuesService;
import com.hp.autonomy.searchcomponents.hod.beanconfiguration.HavenSearchHodConfiguration;
import com.hp.autonomy.searchcomponents.hod.fields.HodFieldsRequest;
import com.hp.autonomy.searchcomponents.hod.fields.HodFieldsRequestBuilder;
import com.hp.autonomy.searchcomponents.hod.search.HodQueryRestrictions;
import com.hp.autonomy.searchcomponents.hod.search.HodQueryRestrictionsBuilder;
import com.hp.autonomy.types.requests.idol.actions.tags.TagName;
import org.apache.commons.lang3.NotImplementedException;
import org.junit.Test;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = HavenSearchHodConfiguration.class)
public class HodParametricValuesServiceIT extends AbstractParametricValuesServiceIT<HodParametricRequest, HodFieldsRequest, HodFieldsRequestBuilder, HodQueryRestrictions, ResourceName, HodErrorException> {
    @Autowired
    private ObjectFactory<HodQueryRestrictionsBuilder> queryRestrictionsBuilderFactory;

    @Override
    protected HodFieldsRequestBuilder fieldsRequestParams(final HodFieldsRequestBuilder fieldsRequestBuilder) {
        return fieldsRequestBuilder.databases(testUtils.buildQueryRestrictions().getDatabases());
    }

    @Override
    protected TagName determinePaginatableField() {
        // Can't use value details for non-numeric fields on HOD, but we know the tests use wiki_eng
        return tagNameFactory.buildTagName("WIKIPEDIA_CATEGORY");
    }

    @Override
    protected HodParametricRequest noResultsParametricRequest() {
        final HodQueryRestrictions queryRestrictions = queryRestrictionsBuilderFactory.getObject()
                // No documents will match this text (probably)
                .queryText("sfoiewsfoseinf")
                .database(ResourceName.WIKI_ENG)
                .build();

        return parametricRequestBuilderFactory.getObject()
                .queryRestrictions(queryRestrictions)
                .fieldName(tagNameFactory.buildTagName(ParametricValuesService.AUTN_DATE_FIELD))
                .build();
    }

    @SuppressWarnings("RedundantMethodOverride")
    @Override
    @Test(expected = NotImplementedException.class)
    public void getDependentParametricValues() throws HodErrorException {
        super.getDependentParametricValues();
    }
}
