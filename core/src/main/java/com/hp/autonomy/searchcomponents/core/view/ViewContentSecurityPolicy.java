/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.view;

import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * Utility for adding a content security policy for securing viewed documents.
 */
public class ViewContentSecurityPolicy {
    private static final String CONTENT_SECURITY_POLICY = StringUtils.join(Arrays.asList(
            // Unless another directive applies, prevent loading content
            "default-src 'none'",

            // Allow CSS, fonts, images and media (video, audio etc) to come from any domain or inline
            "font-src * 'unsafe-inline'",
            "img-src * 'unsafe-inline'",
            "style-src * 'unsafe-inline'",
            "media-src * 'unsafe-inline'",

            // Temporarily allow scripts
            "sandbox allow-scripts"
    ), "; ");

    private ViewContentSecurityPolicy() {}

    /**
     * Add content security policy headers to an HTTP response. These control what child content can be loaded from the
     * proxied document, reducing the risk of allowing users to serve arbitrary documents from the application domain.
     * @param response The HTTP response
     */
    public static void addContentSecurityPolicy(final HttpServletResponse response) {
        // We need both headers to support all browsers
//        response.addHeader("Content-Security-Policy", CONTENT_SECURITY_POLICY);
//        response.addHeader("X-Content-Security-Policy", CONTENT_SECURITY_POLICY);
    }
}
