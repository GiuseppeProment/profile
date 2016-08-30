package profile.exception;

public class UserIdNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public UserIdNotFoundException() {
		super("User id não encontrado");
	}

}
