package profile.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import profile.dto.PersonDto;
import profile.dto.PhoneDto;
import profile.repository.PersonRepository;


/**
 * @author giuseppe 
 * Spring tests Looks more like a integration tests... then this is a Integration Test.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProfileControllerIT {

	@Autowired
	private WebApplicationContext ctx;

	@Autowired
	private ObjectMapper mapper;
	
	private MockMvc mvc;

	@Autowired
	private PersonRepository repository;

	private String today;

	@Test
	public void shouldLoginWithCorrectCredentialsTest() throws Exception {
		recordPerson(buildWellKnonwPerson());
		//@formatter:off
		mvc
			.perform( 
				get("/login/jose@gmail.com/secret")
				)
				.andDo(print())
				.andExpect( status().isOk() )
				.andExpect( jsonPath("$.name",equalTo("Jose")))
				.andExpect( jsonPath("$.email",equalTo("jose@gmail.com")))
				.andExpect( jsonPath("$.password").exists())
				.andExpect( jsonPath("$.password",not(equalTo("secret"))))
				.andExpect( jsonPath("$.token").exists())
				.andExpect( jsonPath("$.id").exists())
				.andExpect( jsonPath("$.created").value(startsWith(today)))
				.andExpect( jsonPath("$.modified").value(startsWith(today)))
				.andExpect( jsonPath("$.last_login").value(startsWith(today)))
				.andExpect( jsonPath("$.phones[0].ddd",equalTo("11")))
				.andExpect( jsonPath("$.phones[0].number",equalTo("55890444")))
		;
		//@formatter:on
	}
	
	@Test
	public void shouldNotLoginWithWrongPasswordTest() throws Exception {
		recordPerson(buildWellKnonwPerson());
		//@formatter:off
		mvc
			.perform( 
				get("/login/jose@gmail.com/wrong-password")
				)
				.andDo(print())
				.andExpect( status().is4xxClientError() )
				.andExpect( jsonPath("$.mensagem",equalTo("Usuário e/ou senha inválidos")))
		;
		//@formatter:on
	}
	
	@Test
	public void shouldNotLoginWithWrongEmailTest() throws Exception {
		recordPerson(buildWellKnonwPerson());
		//@formatter:off
		mvc
			.perform( 
				get("/login/unkonow@gmail.com/secret")
				)
				.andDo(print())
				.andExpect( status().is4xxClientError() )
				.andExpect( jsonPath("$.mensagem",equalTo("Usuário e/ou senha inválidos")))
		;
		//@formatter:on
	}
	
	@Test
	public void shouldReceivePerfilUsingCorrectTokenAndIdTest() throws Exception {
		String personAsJson = recordPerson(buildWellKnonwPerson());
		PersonDto person = mapper.readValue(personAsJson, PersonDto.class);
		//@formatter:off
		mvc
			.perform( 
				get("/perfil/"+person.getId())
				.header("token", person.getToken())
				)
				.andDo(print())
				.andExpect( status().isOk() )
				.andExpect( jsonPath("$.name",equalTo("Jose")))
				.andExpect( jsonPath("$.email",equalTo("jose@gmail.com")))
				.andExpect( jsonPath("$.password").exists())
				.andExpect( jsonPath("$.id").exists())
				.andExpect( jsonPath("$.created").value(startsWith(today)))
				.andExpect( jsonPath("$.modified").value(startsWith(today)))
				.andExpect( jsonPath("$.last_login").value(startsWith(today)))
				.andExpect( jsonPath("$.phones[0].ddd",equalTo("11")))
				.andExpect( jsonPath("$.phones[0].number",equalTo("55890444")))
		;
		//@formatter:on
	}
	
	@Test
	public void shouldNotReceivePerfilUsingWrongTokenTest() throws Exception {
		String personAsJson = recordPerson(buildWellKnonwPerson());
		PersonDto person = mapper.readValue(personAsJson, PersonDto.class);
		//@formatter:off
		mvc
			.perform( 
				get("/perfil/"+person.getId())
				.header("token", "wrong-token")
				)
				.andDo(print())
				.andExpect( status().is4xxClientError() )
				.andExpect( jsonPath("$.mensagem",equalTo("Não autorizado")))
		;
		//@formatter:on
	}

	@Test
	public void shouldNotReceivePerfilUsingWrongIdTest() throws Exception {
		String personAsJson = recordPerson(buildWellKnonwPerson());
		PersonDto person = mapper.readValue(personAsJson, PersonDto.class);
		//@formatter:off
		mvc
			.perform( 
				get("/perfil/"+"ffffffff-ffff-ffff-ffff-ffffffffffff")
				.header("token", person.getToken())
				)
				.andDo(print())
				.andExpect( status().is4xxClientError() )
				.andExpect( jsonPath("$.mensagem",equalTo("User id não encontrado")))
		;
		//@formatter:on
	}

	@Test
	public void shouldNotReceivePerfilWithoutTokenTest() throws Exception {
		String personAsJson = recordPerson(buildWellKnonwPerson());
		PersonDto person = mapper.readValue(personAsJson, PersonDto.class);
		//@formatter:off
		mvc
			.perform( 
				get("/perfil/"+person.getId())
				)
				.andDo(print())
				.andExpect( status().is4xxClientError() )
				.andExpect( jsonPath("$.mensagem",equalTo("Não autorizado")))
		;
		//@formatter:on
	}
	
	@Test
	public void shouldCreateNewPersonTest() throws Exception {
		PersonDto person = buildWellKnonwPerson();
		String personAsJson = mapper.writeValueAsString(person);
		//@formatter:off
		mvc
			.perform( 
				post("/cadastro")
				.content(personAsJson)
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				)
				.andDo(print())
				.andExpect( status().isCreated() )
				.andExpect( jsonPath("$.name",equalTo("Jose")))
				.andExpect( jsonPath("$.email",equalTo("jose@gmail.com")))
				.andExpect( jsonPath("$.password").exists())
				.andExpect( jsonPath("$.password",not(equalTo("my_secret_password"))))
				.andExpect( jsonPath("$.token").exists())
				.andExpect( jsonPath("$.id").exists())
				.andExpect( jsonPath("$.created").value(startsWith(today)))
				.andExpect( jsonPath("$.modified").value(startsWith(today)))
				.andExpect( jsonPath("$.last_login").value(startsWith(today)))
				.andExpect( jsonPath("$.phones[0].ddd",equalTo("11")))
				.andExpect( jsonPath("$.phones[0].number",equalTo("55890444")))
		;
		//@formatter:on
	}

	@Test
	public void shouldReceiveDuplicateEmailErrorOnCreationTest() throws Exception {
		recordPerson(buildWellKnonwPerson());
		String personAsJson = mapper.writeValueAsString(buildWellKnonwPerson());
		//@formatter:off
		mvc.perform( 
				post("/cadastro")
				.content(personAsJson)
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				)
			.andDo(print())
			.andExpect(status().is4xxClientError())
			.andExpect(jsonPath("$.mensagem", equalTo("E-mail já existente")));
		//@formatter:on
	}

	@Test
	public void shouldReturnTheStandardJsonObjectForGenericErros() throws Exception {
		//@formatter:off
		mvc.perform( 
				get("/this-doesnt-exist")
				)
			.andDo(print())
			.andExpect(status().is4xxClientError());
		//@formatter:on
	}

	@Before
	public void setUp() {
		mvc = MockMvcBuilders.webAppContextSetup(ctx).build();
		repository.deleteAll();
		today = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
	}

	private PersonDto buildWellKnonwPerson() {
		ArrayList<PhoneDto> phones = new ArrayList<PhoneDto>();
		phones.add(new PhoneDto("11", "55890444"));
		PersonDto person = new PersonDto();
		person.setName("Jose");
		person.setEmail("jose@gmail.com");
		person.setPassword("secret");
		person.setPhones(phones);
		return person;
	}

	private String recordPerson(PersonDto person) throws JsonProcessingException, Exception {
		String json = mapper.writeValueAsString(person);
		//@formatter:off
		return mvc.perform( 
					post("/cadastro")
					.content(json)
					.contentType(MediaType.APPLICATION_JSON_UTF8))
				.andReturn()
				.getResponse()
				.getContentAsString();
		//@formatter:on
	}

}
