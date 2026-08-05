package byransha.graph;

import byransha.system.User;

public interface CurrentUserListener {
	void userSwitchedTo(User formerUser, User newUser);
}