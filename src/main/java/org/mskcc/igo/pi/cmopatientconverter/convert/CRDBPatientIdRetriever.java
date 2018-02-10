package org.mskcc.igo.pi.cmopatientconverter.convert;

public interface CRDBPatientIdRetriever {
    String resolve(String mrn);
}