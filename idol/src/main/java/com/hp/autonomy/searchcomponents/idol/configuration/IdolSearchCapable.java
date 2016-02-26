/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.configuration;

import com.autonomy.aci.client.transport.AciServerDetails;
import com.hp.autonomy.searchcomponents.core.config.HavenSearchCapable;
import com.hp.autonomy.searchcomponents.idol.category.configuration.CategoryCapable;
import com.hp.autonomy.searchcomponents.idol.view.configuration.ViewCapable;

/**
 * Configuration required for any application performing Haven Search
 */
public interface IdolSearchCapable extends HavenSearchCapable, ViewCapable, CategoryCapable {
    AciServerDetails getContentAciServerDetails();

    QueryManipulation getQueryManipulation();
}
