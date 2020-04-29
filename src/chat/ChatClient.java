package chat;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import chat.io.StreamInOut;
import chat.message.ChatMessage;
import input.Keyboard;


public class ChatClient {
	
	/** this thread is listening to incoming messages from server */
	public final static class ListeningThread implements Runnable {
		private Thread t;
		private String threadName;
		//private ObjectInputStream sInput;

		public ListeningThread(String name) {
		   threadName = name;
		   //this.sInput = sInput;
		}
		
		public void run() {
			ChatMessage cm;
			while(true) {
				try {
					cm = (ChatMessage) StreamInOut.readData(in);
					
					removePrompt(); // remove the prompt
					
					System.out.println(cm);
					
					// print the prompt
					System.out.print(username + ": ");
					System.out.flush();
				}
				catch (EOFException eofe) {
					System.err.println("Server closed connection.");
					break;
				}
				catch (IOException ioe) {
					System.out.println(" Exception reading Streams: " + ioe);
					ioe.printStackTrace();
					break;
				}
				catch(ClassCastException cce) {
					continue;
				} catch (InterruptedException ie) {
					ie.printStackTrace();
				}
			}
		   System.out.println("Thread " +  threadName + " exiting.");
		   System.exit(-1);
		}
		
		public void start () {
		   if (t == null) {
		      t = new Thread (this, threadName);
		      t.start ();
		   }
		}
	}

	protected static Socket server = null;
	protected static ObjectInputStream in;
	protected static ObjectOutputStream out;
	protected static String username;
	
	/** remove the prompt with backspace characters */
	public static void removePrompt() {
		for(int i = 0; i < username.length(); i++) {
			System.out.print("\b");
		}
		System.out.print("\b\b");
	}

	public static void main( String[] args ) {
	    try {
	    	/* Connection to server */
	    	// pass server ip (10.0.0.1 or localhost) as commandline argument
	    	InetAddress addr;
	    	if(args.length > 0)
	    		addr = InetAddress.getByName(args[0]);
	    	else
	    		addr = InetAddress.getByName("localhost");

	    	server = new Socket( addr, 3142);
			out = new ObjectOutputStream(server.getOutputStream());
			in  = new ObjectInputStream(server.getInputStream());
			
	
			//System.out.print("Enter your username: ");
			//username = Keyboard.nextLine();
			username = args[1];

			// you have to start listenig before you start writing
			ListeningThread listener = new ListeningThread( "Listener-1");
			listener.start();
			
			StreamInOut.writeMsg(new ChatMessage(username), out);
			out.flush();

			// command loop
			while(true) {
				String cmd = input.Keyboard.nextLine(); // clients command	
				
				// send message to server
				StreamInOut.writeMsg(new ChatMessage(cmd, username), out);

			}
			
		} catch ( UnknownHostException e ) {
			System.err.println("UnknownHostException");
			e.printStackTrace();
	    } catch ( IOException e ) {
	    	System.err.println("Server is not reachable.");
	    	//e.printStackTrace();
	    } finally {
	    	if ( server != null )
	    		try { server.close(); } catch ( IOException e ) { }
	    }
	}
}
