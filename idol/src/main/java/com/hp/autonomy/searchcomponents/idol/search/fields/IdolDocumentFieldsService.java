/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search.fields;

import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.searchcomponents.core.config.FieldInfo;
import com.hp.autonomy.searchcomponents.core.config.FieldType;
import com.hp.autonomy.searchcomponents.core.config.HavenSearchCapable;
import com.hp.autonomy.searchcomponents.core.search.fields.AbstractDocumentFieldsService;
import com.hp.autonomy.searchcomponents.core.search.fields.DocumentFieldsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;

@Component
public class IdolDocumentFieldsService extends AbstractDocumentFieldsService implements DocumentFieldsService {
    public static final String QMS_ID_FIELD = "qmsid";
    public static final String INJECTED_PROMOTION_FIELD = "injectedpromotion";
    public static final FieldInfo<String> QMS_ID_FIELD_INFO = new FieldInfo<>(QMS_ID_FIELD, FieldType.STRING);
    public static final FieldInfo<Boolean> INJECTED_PROMOTION_FIELD_INFO = new FieldInfo<>(INJECTED_PROMOTION_FIELD, FieldType.BOOLEAN);

    @SuppressWarnings("TypeMayBeWeakened")
    @Autowired
    public IdolDocumentFieldsService(final ConfigService<? extends HavenSearchCapable> configService) {
        super(configService);
    }

    @SuppressWarnings("CastToConcreteClass")
    @Override
    public Collection<FieldInfo<?>> getHardCodedFields() {
        return Arrays.asList((FieldInfo<?>) QMS_ID_FIELD_INFO, INJECTED_PROMOTION_FIELD_INFO);
    }
}
