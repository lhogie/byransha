package byransha.access_control;

public interface CurrentUserListener {
	void userSwitchedTo(User formerUser, User newUser);
}