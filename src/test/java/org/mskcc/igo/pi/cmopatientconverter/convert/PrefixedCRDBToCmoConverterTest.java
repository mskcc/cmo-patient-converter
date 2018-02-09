package org.mskcc.igo.pi.cmopatientconverter.convert;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PrefixedCRDBToCmoConverterTest {
    private PrefixedCRDBToCmoConverter prefixedCRDBToCmoConverter;

    @Before
    public void setUp() throws Exception {
        prefixedCRDBToCmoConverter = new PrefixedCRDBToCmoConverter();
    }

    @Test
    public void whenCrdbIdIsConvertedToCmoPatientId_shouldReturnPrefixedCmoId() throws Exception {
        assertCmoPatientId("123456", "C-123456");
        assertCmoPatientId("66666", "C-66666");
        assertCmoPatientId("987654", "C-987654");
        assertCmoPatientId("2745368", "C-2745368");
    }

    private void assertCmoPatientId(String crdbPatientId, String expected) {
        String cmoPatientId = prefixedCRDBToCmoConverter.convert(crdbPatientId);
        assertThat(cmoPatientId, is(expected));
    }
}