package org.mskcc.igo.pi.cmopatientconverter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mskcc.domain.patient.CRDBPatientInfo;
import org.mskcc.igo.pi.cmopatientconverter.convert.CRDBPatientIdRetriever;
import org.mskcc.igo.pi.cmopatientconverter.convert.CRDBToCmoConverter;
import org.mskcc.igo.pi.cmopatientconverter.crdb.PatientInfo;
import org.mskcc.igo.pi.cmopatientconverter.crdb.RestCRDBPatientIdRetriever;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CmoPatientIdController {
    private static final Logger LOGGER = LogManager.getLogger(CmoPatientIdController.class);
    @Autowired
    private CRDBPatientIdRetriever crdbPatientIdRetriever;

    @Autowired
    private CRDBToCmoConverter crdbToCmoConverter;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<String> getCmoPatientId(@PathVariable String patientId) {
        try {
            PatientInfo patientInfo = crdbPatientIdRetriever.resolve(patientId);

            return ResponseEntity.ok().body(crdbToCmoConverter.convert(patientInfo.getPatientId()));
        } catch (RestCRDBPatientIdRetriever.CmoPatientIdRetrievalException e) {
            LOGGER.error(String.format("Error while retrieving CMO Patient id for patientId: %s. Cause: %s",
                    patientId, e
                    .getMessage()));

            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            LOGGER.error(String.format("Error while retrieving CMO Patient id for patientId: %s. Cause: %s",
                    patientId, e
                    .getMessage()));

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/patientInfo/{patientId}")
    public ResponseEntity<CRDBPatientInfo> getCmoPatientInfo(@PathVariable String patientId) {
        PatientInfo patientInfo = new PatientInfo();

        try {
            patientInfo = crdbPatientIdRetriever.resolve(patientId);
            CRDBPatientInfo crdbPatientInfo = convert(patientInfo);

            return ResponseEntity.ok().body(crdbPatientInfo);
        } catch (RestCRDBPatientIdRetriever.CmoPatientIdRetrievalException e) {
            LOGGER.error(String.format("Error while retrieving CMO Patient id for patientId: %s", patientId), e);

            throw new RuntimeException(patientInfo.getErrorMessage() + e.getMessage());
        } catch (Exception e) {
            LOGGER.error(String.format("Error while retrieving CMO Patient id for patientId: %s", patientId), e);

            throw new RuntimeException(patientInfo.getErrorMessage() + e.getMessage());
        }
    }

    private CRDBPatientInfo convert(PatientInfo patientInfo) {
        String cmoId = crdbToCmoConverter.convert(patientInfo.getPatientId());

        CRDBPatientInfo crdbPatientInfo = new CRDBPatientInfo();
        crdbPatientInfo.setErrorMessage(patientInfo.getErrorMessage());
        crdbPatientInfo.setJobStatus(patientInfo.getJobStatus());
        crdbPatientInfo.setGender(patientInfo.getGender());
        crdbPatientInfo.setPatientId(cmoId);

        return crdbPatientInfo;
    }
}
