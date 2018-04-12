package org.mskcc.igo.pi.cmopatientconverter.crdb;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mskcc.igo.pi.cmopatientconverter.convert.CRDBPatientIdRetriever;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

@Component
public class RestCRDBPatientIdRetriever implements CRDBPatientIdRetriever {
    private static final Logger LOGGER = LogManager.getLogger(RestCRDBPatientIdRetriever.class);

    @Value("${crdb.service.url}")
    private String crdbServiceUrl;

    @Value("${crdb.mrn.cmo.endpoint}")
    private String endpoint;

    @Value("${crdb.sid}")
    private String sid;

    @Autowired
    @Qualifier("crdbRestTemplate")
    private RestTemplate restTemplate;

    @Override
    public PatientInfo resolve(String mrn) {
        PatientInfo patientInfo = restTemplate.getForObject(getUrl(mrn), PatientInfo.class);

        String patientId = patientInfo.getPatientId();

        if (StringUtils.isEmpty(patientId)) {
            throw new CmoPatientIdRetrievalException(String.format("CRDB patient id could not be retrieved from " +
                    "service: %s%s for mrn: %s. Cause: %s", crdbServiceUrl, endpoint, mrn, patientInfo
                    .getErrorMessage()));
        }

        LOGGER.info(String.format("Retrieved CRDB Patient id: %s", patientId));

        return patientInfo;
    }

    private String getUrl(String mrn) {
        String url = String.format("%s/%s?mrn=%s&sid=%s", crdbServiceUrl, endpoint, mrn, sid);

        LOGGER.info(String.format("Retrieving CRDB Patient id from service: %s for endpoint: %s", crdbServiceUrl,
                endpoint));

        return url;
    }

    public class CmoPatientIdRetrievalException extends RuntimeException {
        public CmoPatientIdRetrievalException(String message) {
            super(message);
        }
    }
}
