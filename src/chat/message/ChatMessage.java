package chat.message;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
/** wrapper class for sending string based messages over network */
public class ChatMessage implements Message, Serializable {

	private static final long serialVersionUID = -5436669498542756495L;
	int type = MESSAGE;
	String message;
	String username;
	long date; // better for serialization than Date?

	public ChatMessage(String message, String username) {
		this.message = message;
		this.username = username;
		Date d = new Date(System.currentTimeMillis());
		date = d.getTime();
	};
	
	public ChatMessage(String username) {
		this.username = username;
		this.message = "";
		Date d = new Date(System.currentTimeMillis());
		date = d.getTime();
	}
	
	public ChatMessage(Message msg) {
		this.message = msg.getMessage();
		this.username = msg.getUser();
		Date d = new Date(System.currentTimeMillis());
		date = d.getTime();
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public int getType() {
		return type;
	}
	
	@Override
	public String getUser() {
		return username;
	}
	
	@Override
	public String toString() {
		SimpleDateFormat ft = new SimpleDateFormat ("HH:mm:ss");
		return ft.format(date) + " <" + getUser() + "> " + getMessage();
	}

	
}
