package org.mskcc.igo.pi.cmopatientconverter.convert;

import org.mskcc.igo.pi.cmopatientconverter.crdb.PatientInfo;

public interface CRDBPatientIdRetriever {
    PatientInfo resolve(String mrn);
}