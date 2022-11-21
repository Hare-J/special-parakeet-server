
// PROG1415 Test 1 - Hands On
// James Hare

package prog1415;

public class Message {
	
	// fields
	private final String commandList = "SYSTEM - For list of commands, type: \n\t\"/help\""
			+ "\nTo send a private message, type: \n\t\"/w <recipient> <message>\""
			+ "\nTo get a list of active users, type: \n\t\"/users\"";
			
	private String user;
	private String message;
	
	private boolean isCommand = false;
	
	private volatile boolean isWhisper = false;
	private String target;
	private String whisper;
	
	private volatile boolean isHelp = false;
	private volatile boolean requestUsers = false;
		
	// properties
	public String getUser() {
		return this.user;
	}
	
	public String getMessage() {
		return this.message;
	} 
	
	public String IsCommand() {
		return this.IsCommand();
	}
	
	public boolean IsWhisper() {
		return this.isWhisper;
	}
	
	public String getTarget() {
		return this.target;
	}
	
	public String getWhisper() {
		return this.whisper;
	}
	
	public boolean IsHelp() {
		return this.isHelp;
	}
	public String getHelp() {
		return this.commandList;
	}
	
	public boolean RequestUsers() {
		return this.requestUsers;
	}
	
	// constructor
	public Message(String user, String message) {
		this.user = user;
		this.message = message;
		if (this.message.charAt(0) == '/') {
			checkCommand(message);
			this.isCommand = true;
		}
	}
	
	// methods
	private void checkCommand(String msg) {
		
		if (msg.charAt(1) == 'w') {
			isWhisper = true;
			parseWhisper(msg);
		}
		else if (msg.charAt(1) == 'h' && msg.charAt(2) == 'e' && msg.charAt(3) == 'l' && msg.charAt(4) == 'p') {
			this.isHelp = true;
		}
		else if (msg.charAt(1) == 'u' && msg.charAt(2) == 's' && msg.charAt(3) == 'e' && msg.charAt(4) == 'r' && msg.charAt(5) == 's') {
			this.requestUsers = true;
		}
		else {
			isWhisper = true;
			this.target = this.user;
			this.user = "SYSTEM";
			this.whisper = "Command not found: Please enter a valid command.";
		}
	}
	
	private void parseWhisper(String msg) {
		String target = null;
		String whisper = null;
		int ws = 0;
		int curr = 3;
		
		// get whisper target
		for (int i=3;ws<1;i++) {
			if (Character.isWhitespace(msg.charAt(i)) == true) 
				ws++;
			else {
				if (target == null) {
					target = Character.toString(msg.charAt(i));
					curr++;
				}
				else {
					target += Character.toString(msg.charAt(i));
					curr++;
				}
			}
		}
		
		//get whispered message
		for (int i=curr;i<msg.length();i++) {
			if (whisper == null)
				whisper = Character.toString(msg.charAt(i));
			else
				whisper += Character.toString(msg.charAt(i));
		}
		
		if (target.length() > 0 || target != null)
			this.target = target;
		if (whisper.length() > 0 || whisper != null)
			this.whisper = whisper;
	}
	
	
	@Override
	public String toString() {
		if (user.compareTo("<server>") == 0)
			return this.message;
		else
			return this.user + " - " + this.message;
	}
}
