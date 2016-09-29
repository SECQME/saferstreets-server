package com.secqme.crimedata.jsf.util;

import javax.faces.context.FacesContext;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * User: James Khoo
 * Date: 10/3/14
 * Time: 4:47 PM
 */
public class FacesUtils {

    public static String getMessageResourceString(
            FacesContext context,
            String key,
            Object params[]) {

        String text = null;
        String bundleName = context.getApplication().getMessageBundle();
        Locale locale = context.getViewRoot().getLocale();

        ResourceBundle bundle =
                ResourceBundle.getBundle(bundleName, locale);

        try {
            text = bundle.getString(key);
        } catch (MissingResourceException e) {
            text = "?? key " + key + " not found ??";
        }

        if (params != null) {
            MessageFormat mf = new MessageFormat(text, locale);
            text = mf.format(params, new StringBuffer(), null).toString();
        }
        System.out.println("bundle->" + bundle + ", locale:" + locale + ", key->" + key + ", text->" + text);
        return text;
    }
}
