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

import static org.mskcc.igo.pi.cmopatientconverter.utils.Utils.getRedactedPatientId;

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
    public PatientInfo resolve(String patientId) {
        PatientInfo patientInfo = restTemplate.getForObject(getUrl(patientId), PatientInfo.class);

        String cmoPatientId = patientInfo.getPatientId();

        if (StringUtils.isEmpty(cmoPatientId)) {
            throw new CmoPatientIdRetrievalException(String.format("CRDB patient id could not be retrieved from " +
                    "service: %s%s for patient id: %s. Cause: %s", crdbServiceUrl, endpoint, getRedactedPatientId
                    (patientId), patientInfo.getErrorMessage()));
        }

        LOGGER.info(String.format("Retrieved CRDB Patient id: %s", cmoPatientId));

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
