package byransha;

import byransha.access_control.User;

public interface ChatListener {
	void newChat(User user, Chat chat);

	void chatClosed(User user, Chat chat);
}
