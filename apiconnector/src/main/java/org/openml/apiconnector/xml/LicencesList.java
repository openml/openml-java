package org.openml.apiconnector.xml;

import org.openml.apiconnector.settings.Constants;

/**
 * User: Kuba
 * Date: 1.3.2015
 * Time: 18:52
 */
public class LicencesList {
    private final String oml = Constants.OPENML_XMLNS;
    public String getOml() {
        return oml;
    }

    private Licences licences;
    public Licences getLicences()
    {
        return licences;
    }

    public static class Licences
    {
        private String[] licences;
        public String[] getLicences() {
            return licences;
        }
    }
}
