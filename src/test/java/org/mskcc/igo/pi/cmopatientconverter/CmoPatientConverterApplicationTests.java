package org.mskcc.igo.pi.cmopatientconverter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mskcc.igo.pi.cmopatientconverter.convert.CRDBPatientIdRetriever;
import org.mskcc.igo.pi.cmopatientconverter.crdb.RestCRDBPatientIdRetriever;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
@ContextConfiguration()
public class CmoPatientConverterApplicationTests {
    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @MockBean
    private CRDBPatientIdRetriever crdbPatientIdRetriever;

    @Before
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void whenNoCredentialsArePassed_shouldNotAuthenticateUser() throws Exception {
        mvc
                .perform(MockMvcRequestBuilders.get("/patient/123456"))
                .andExpect(SecurityMockMvcResultMatchers.unauthenticated());
    }

    @Test
    public void whenIncorrectCredentialsArePassed_shouldNotAuthenticateUser() throws Exception {
        mvc
                .perform(MockMvcRequestBuilders.get("/patient/123456")
                        .with(httpBasic("user", "password")))
                .andExpect(SecurityMockMvcResultMatchers.unauthenticated());
    }

    @Test
    public void whenUserHasRoleAdmin_shouldAuthenticateUser() throws Exception {
        mvc
                .perform(MockMvcRequestBuilders.get("/patient/123456")
                        .with(user("user").roles("ADMIN")))
                .andExpect(SecurityMockMvcResultMatchers.authenticated());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void whenRoleHasNoAdminRole_shouldReturnForbiddenStatus() throws Exception {
        //when
        mvc
                .perform(MockMvcRequestBuilders.get("/patient/54543"))

                //then
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void whenMrnDoesNotExist_shouldReturnBadRequest() throws Exception {
        //given
        String patientId = "54543";
        when(crdbPatientIdRetriever.resolve(patientId)).thenThrow(RestCRDBPatientIdRetriever
                .CmoPatientIdRetrievalException
                .class);

        //when
        mvc
                .perform(MockMvcRequestBuilders.get("/patient/" + patientId))

                //then
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void whenUnknownExceptionOccurs_shouldReturnServerError() throws Exception {
        //given
        String patientId = "54543";
        when(crdbPatientIdRetriever.resolve(patientId)).thenThrow(RuntimeException.class);

        //when
        mvc
                .perform(MockMvcRequestBuilders.get("/patient/" + patientId))

                //then
                .andExpect(status().isInternalServerError());
    }
}
