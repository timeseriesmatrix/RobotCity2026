/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class Messages {
    private static final String BUNDLE_NAME = "messages";
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("messages", new ResourceControl());

    private Messages() {
    }

    public static String getString(String key) {
        try {
            String string = RESOURCE_BUNDLE.getString(key);
            return string;
        }
        catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }

    private static class ResourceControl
    extends ResourceBundle.Control {
        private ResourceControl() {
        }

        @Override
        public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IllegalAccessException, InstantiationException, IOException {
            String bundlename = this.toBundleName(baseName, locale);
            String resName = this.toResourceName(bundlename, "properties");
            InputStream stream = loader.getResourceAsStream(resName);
            return new PropertyResourceBundle(new InputStreamReader(stream, "UTF-8"));
        }
    }
}

