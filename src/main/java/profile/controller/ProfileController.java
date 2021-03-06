package profile.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import profile.dto.PersonDto;
import profile.service.ProfileService;

/**
 * My responsibility is take care of rest reception calling the service.
 * @author giuseppe
 */
@RestController
public class ProfileController {
    
	@Autowired
	private ProfileService service;

	@RequestMapping(value="/cadastro", method=RequestMethod.POST )
    @ResponseStatus(HttpStatus.CREATED)
    public PersonDto cadastro(  @RequestBody PersonDto person ) {
    	return service.cadastro(person);
    }
	
    @RequestMapping("/login/{email}/{password}")
    @ResponseStatus(HttpStatus.OK)
    public PersonDto login( @PathVariable String email, @PathVariable String password ) {
        return service.login(email,password);
    }
    
    @RequestMapping("/perfil/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public PersonDto perfil( @RequestHeader("token") String token, @PathVariable String userId ) {
        return service.perfil(token, userId);
    }
    
    @RequestMapping(value="/",produces=MediaType.TEXT_HTML_VALUE)
    public String hello() {
    	return "<!DOCTYPE html>"
    			+ "<html><body>"
    			+ "<b>Available urls</b>"
    			+ "<p>/cadastro (post with json body)</p>"
    			+ "<p>/login/email/password</p>"
    			+ "<p>/perfil/userId</p>"
    			+ "</body></html>";		
    }
}

