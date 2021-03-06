package profile.exception;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * My responsibility is handle meaningfull exceptions.
 * @author giuseppe
 */
@ControllerAdvice
public class ExceptionHandle {
	
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DuplicateEmailException.class)
    @ResponseBody ErrorInfo 
    handleBadRequest(HttpServletRequest req, Exception ex) {
        return new ErrorInfo(ex.getMessage());
    } 
    
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseBody ErrorInfo
    handleNotFound(HttpServletRequest req, Exception ex) {
        return new ErrorInfo(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserIdNotFoundException.class)
    @ResponseBody ErrorInfo
    handleIdNotFound(HttpServletRequest req, Exception ex) {
        return new ErrorInfo(ex.getMessage());
    }
    
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedException.class)
    @ResponseBody ErrorInfo
    handleUnauthorized(HttpServletRequest req, Exception ex) {
        return new ErrorInfo(ex.getMessage());
    } 
    
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(InvalidTokenException.class)
    @ResponseBody ErrorInfo
    handleUnauthorizedToken(HttpServletRequest req, Exception ex) {
        return new ErrorInfo(ex.getMessage());
    } 
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(InvalidSessionException.class)
    @ResponseBody ErrorInfo
    handleStaleToken(HttpServletRequest req, Exception ex) {
        return new ErrorInfo(ex.getMessage());
    }
    
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ServletRequestBindingException.class)
    @ResponseBody ErrorInfo 
    handleLackOfToken(HttpServletRequest req, Exception ex) {
        return new ErrorInfo( "Não autorizado");
    }
	
}
