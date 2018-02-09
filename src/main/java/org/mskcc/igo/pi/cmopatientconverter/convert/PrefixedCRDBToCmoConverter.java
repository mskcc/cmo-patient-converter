package org.mskcc.igo.pi.cmopatientconverter.convert;

import org.springframework.stereotype.Component;

@Component
public class PrefixedCRDBToCmoConverter implements CRDBToCmoConverter {
    private String CMO_PREFIX = "C-";

    @Override
    public String convert(String crdbPatientId) {
        return String.format("%s%s", CMO_PREFIX, crdbPatientId);
    }
}
