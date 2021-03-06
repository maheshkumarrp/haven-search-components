/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("FieldMayBeFinal")
@Data
@NoArgsConstructor
public class FieldInfo<T> implements Serializable {
    private static final long serialVersionUID = -5649457890413743332L;
    private String id;
    private FieldType type = FieldType.STRING;
    @SuppressWarnings({"MismatchedQueryAndUpdateOfCollection", "NonSerializableFieldInSerializableClass", "TypeMayBeWeakened"})
    private final List<String> names = new ArrayList<>();
    @SuppressWarnings({"MismatchedQueryAndUpdateOfCollection", "NonSerializableFieldInSerializableClass", "TypeMayBeWeakened"})
    private final List<T> values = new ArrayList<>();

    public FieldInfo(final String names, final FieldType type) {
        this(null, Collections.singletonList(names), type, Collections.<T>emptyList());
    }

    public FieldInfo(final String id, final Collection<String> names, final FieldType type) {
        this(id, names, type, Collections.<T>emptyList());
    }

    public FieldInfo(final String id, final Collection<String> names, final FieldType type, final T value) {
        this(id, names, type, Collections.singletonList(value));
    }

    public FieldInfo(final String id, final Collection<String> names, final FieldType type, final Collection<T> values) {
        this.id = id;
        this.names.addAll(names);
        this.type = type;
        this.values.addAll(values);
    }

    @JsonProperty("type")
    public void setType(final String type) {
        this.type = type == null ? FieldType.STRING : FieldType.valueOf(type.toUpperCase());
    }
}
