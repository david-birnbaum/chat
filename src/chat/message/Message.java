package chat.message;

public interface Message {
	public static final int MESSAGE = 1;
	public int getType();
	public String getMessage();
	public String getUser();
	public String toString();
}
