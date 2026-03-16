package byransha.graph;

import byransha.nodes.system.User;

public interface CurrentUserListener {
	void userSwitchedTo(User formerUser, User newUser);
}