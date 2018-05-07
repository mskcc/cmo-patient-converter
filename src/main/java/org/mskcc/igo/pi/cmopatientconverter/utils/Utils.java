package org.mskcc.igo.pi.cmopatientconverter.utils;

import org.springframework.web.bind.annotation.PathVariable;

import java.util.regex.Pattern;

public class Utils {
    private static final Pattern MRN_PATTERN = Pattern.compile("[0-9]{8}");
    private static final String MRN_REDACTED = "MRN_REDACTED";

    public static String getRedactedPatientId(@PathVariable String patientId) {
        if (MRN_PATTERN.matcher(patientId).matches())
            return MRN_REDACTED;
        return patientId;
    }
}
