package ru.smartech.app.resources.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.smartech.app.dto.EmailDto;
import ru.smartech.app.entity.Email;
import ru.smartech.app.entity.User;
import ru.smartech.app.repository.AccountRepository;
import ru.smartech.app.repository.EmailRepository;
import ru.smartech.app.repository.PhoneRepository;
import ru.smartech.app.repository.UserRepository;
import ru.smartech.app.service.SecurityService;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@AutoConfigureMockMvc(addFilters = false)
class EmailManagementResourceImplTest {

    @MockBean
    private EmailRepository emailRepository;
    @MockBean
    private SecurityService securityService;

    @MockBean
    private AccountRepository accountRepository;
    @MockBean
    private PhoneRepository phoneRepository;
    @MockBean
    private UserRepository userRepository;

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;

    @Test
    void linkMail_successfully() throws Exception {
        final String LINK_MAIL_ENDPOINT = "/api/v1/email-management/email/{mail}/link/{userId}";
        final String MOCK_MAIL = "xxx@mail.ru";
        final long MOCK_USER_ID = 1L;

        Mockito.when(securityService.isPrincipal(1L))
                .thenReturn(true);
        Mockito.when(emailRepository.findByEmail(any()))
                .thenReturn(Optional.empty());
        Mockito.when(emailRepository.save(any()))
                .thenAnswer(invoc -> {
                    var email = invoc.getArgument(0, Email.class);
                    return email.setId(1L);
                });

        MvcResult response = mvc.perform(post(LINK_MAIL_ENDPOINT, MOCK_MAIL, MOCK_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        EmailDto result = mapper.readValue(response.getResponse().getContentAsString(), EmailDto.class);
        assertEquals(MOCK_MAIL, result.getEmail());
    }

    @Test
    void linkMail_throw() throws Exception {
        final String LINK_MAIL_ENDPOINT = "/api/v1/email-management/email/{mail}/link/{userId}";
        final String MOCK_MAIL = "xxx@mail.ru";
        final long MOCK_USER_ID = 1L;
        final long UNAUTHORIZED_USER_ID = 2L;

        Mockito.when(securityService.isPrincipal(anyLong()))
                .thenAnswer(invoc -> {
                    long userId = invoc.getArgument(0, Long.class);
                    return userId == MOCK_USER_ID;
                });
        Mockito.when(emailRepository.findByEmail(MOCK_MAIL))
                .thenReturn(Optional.of(
                        new Email()
                                .setId(1L)
                                .setEmail(MOCK_MAIL)
                                .setUser(new User().setId(1L)))
                );
        Mockito.when(emailRepository.save(any()))
                .thenAnswer(invoc -> {
                    var email = invoc.getArgument(0, Email.class);
                    return email.setId(1L);
                });

        mvc.perform(post(LINK_MAIL_ENDPOINT, MOCK_MAIL, UNAUTHORIZED_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        mvc.perform(post(LINK_MAIL_ENDPOINT, MOCK_MAIL, MOCK_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void updateMail_successfully() throws Exception {
        final String LINK_MAIL_ENDPOINT = "/api/v1/email-management/email/{mailId}/update/{mail}";
        final String OLD_MAIL = "sss@mail.ru";
        final String MOCK_MAIL = "xxx@mail.ru";
        final long MOCK_MAIL_ID = 1L;

        Mockito.when(securityService.isPrincipal(1L))
                .thenReturn(true);
        Mockito.when(emailRepository.findById(MOCK_MAIL_ID))
                .thenReturn(Optional.of(
                        new Email()
                                .setId(1L)
                                .setEmail(OLD_MAIL)
                                .setUser(new User().setId(1L)))
                );
        Mockito.when(emailRepository.save(any()))
                .thenAnswer(invoc -> invoc.getArgument(0, Email.class));

        MvcResult refreshed = mvc.perform(put(LINK_MAIL_ENDPOINT, MOCK_MAIL_ID, MOCK_MAIL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        EmailDto result = mapper.readValue(refreshed.getResponse().getContentAsString(), EmailDto.class);
        assertEquals(MOCK_MAIL, result.getEmail());
    }

    @Test
    void updateMail_throw() throws Exception {
        final String LINK_MAIL_ENDPOINT = "/api/v1/email-management/email/{mailId}/update/{mail}";
        final String OLD_MAIL = "sss@mail.ru";
        final String MOCK_MAIL = "xxx@mail.ru";
        final long EMPTY_MAIL_ID = 1L;
        final long MOCK_USER_ID = 1L;
        final long UNAUTHORIZED_MAIL_ID = 2L;

        Mockito.when(securityService.isPrincipal(anyLong()))
                .thenAnswer(invoc -> {
                    long userId = invoc.getArgument(0, Long.class);
                    return userId == MOCK_USER_ID;
                });
        Mockito.when(emailRepository.findById(anyLong()))
                .thenAnswer(invoc -> {
                    long mailId = invoc.getArgument(0, Long.class);
                    if (mailId == EMPTY_MAIL_ID)
                        return Optional.empty();
                    return Optional.of(new Email()
                                    .setId(mailId)
                                    .setEmail(OLD_MAIL)
                                    .setUser(new User().setId(mailId)));
                });

        mvc.perform(put(LINK_MAIL_ENDPOINT, EMPTY_MAIL_ID, MOCK_MAIL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        mvc.perform(put(LINK_MAIL_ENDPOINT, UNAUTHORIZED_MAIL_ID, MOCK_MAIL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void unlinkMail_successfully() throws Exception {
        final String UNLINK_MAIL_ENDPOINT = "/api/v1/email-management/email/{mailId}/unlink/{userId}";
        final long MOCK_MAIL_ID = 1L;
        final long MOCK_USER_ID = 1L;
        final Email MOCK_MAIL = new Email()
                .setId(MOCK_MAIL_ID)
                .setEmail("email1@mail.ru")
                .setUser(new User().setId(MOCK_USER_ID));
        Set<Email> mockMails = new HashSet<>();
        mockMails.add(MOCK_MAIL);
        mockMails.add(new Email()
                .setId(2L)
                .setEmail("email2@mail.ru"));

        Mockito.when(securityService.isPrincipal(1L))
                .thenReturn(true);
        Mockito.when(emailRepository.findByUserId(MOCK_USER_ID))
                .thenReturn(mockMails);
        Mockito.when(emailRepository.findById(MOCK_MAIL_ID))
                .thenReturn(Optional.of(MOCK_MAIL));

        mvc.perform(delete(UNLINK_MAIL_ENDPOINT, MOCK_MAIL_ID, MOCK_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void unlinkMail_throw() throws Exception {
        final String UNLINK_MAIL_ENDPOINT = "/api/v1/email-management/email/{mailId}/unlink/{userId}";
        final long MOCK_MAIL_ID = 1L;
        final long MOCK_USER_ID = 1L;
        final long UNAUTHORIZED_USER_ID = 2L;
        final Email MOCK_MAIL = new Email()
                .setId(MOCK_MAIL_ID)
                .setEmail("email1@mail.ru")
                .setUser(new User().setId(MOCK_USER_ID));
        Set<Email> mockMails = new HashSet<>();
        mockMails.add(MOCK_MAIL);

        Mockito.when(securityService.isPrincipal(anyLong()))
                .thenAnswer(invoc -> {
                    long userId = invoc.getArgument(0, Long.class);
                    return userId == MOCK_USER_ID;
                });
        Mockito.when(securityService.isPrincipal(1L))
                .thenReturn(true);
        Mockito.when(emailRepository.findByUserId(MOCK_USER_ID))
                .thenReturn(mockMails);
        Mockito.when(emailRepository.findById(MOCK_MAIL_ID))
                .thenReturn(Optional.of(MOCK_MAIL));

        mvc.perform(delete(UNLINK_MAIL_ENDPOINT, MOCK_MAIL_ID, UNAUTHORIZED_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        mvc.perform(delete(UNLINK_MAIL_ENDPOINT, MOCK_MAIL_ID, MOCK_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}