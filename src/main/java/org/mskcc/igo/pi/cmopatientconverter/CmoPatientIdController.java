package org.mskcc.igo.pi.cmopatientconverter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mskcc.igo.pi.cmopatientconverter.convert.CRDBPatientIdRetriever;
import org.mskcc.igo.pi.cmopatientconverter.convert.CRDBToCmoConverter;
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
            String crdbPatientId = crdbPatientIdRetriever.resolve(patientId);

            return ResponseEntity.ok().body(crdbToCmoConverter.convert(crdbPatientId));
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

}
