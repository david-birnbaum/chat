package chat;
import java.net.Socket;
import java.io.*;
import java.util.Date;
import java.util.Iterator;

import chat.message.ChatMessage;
import chat.message.Message;
import chat.io.StreamInOut;

/** One instance of this thread will run for each client */
public class ClientThread extends Thread {
	private static int uniqueId = 0;
	// the socket where to listen/talk
	protected Socket socket;
	protected ObjectInputStream in;
	protected ObjectOutputStream out;
	
	int id; // my unique id (easier for disconnection)
	protected String username; 	// the username of the Client
	
	public String getUsername() {
		return username;
	}
	
	public ObjectInputStream getInput() {
		return in;
	}

	public ObjectOutputStream getOutput() {
		return out;
	}

	Date date; // the date I connect

	public ClientThread(Socket socket) {
		id = ++uniqueId; // a unique id
		this.socket = socket;
	}

	/** send message to all clients */
	public void broadcast(Message msg) throws IOException {
		Iterator<ClientThread> itr = ChatServer.clients.iterator();
		while(itr.hasNext()) {
			ClientThread t = itr.next();
			if(t.isAlive()) t.writeMsg(new ChatMessage(msg));
		}
	}

	/** write a message to server */
	public void writeMsg(Message msg) throws IOException {
			StreamInOut.writeMsg(msg, out);
	}

	/** what will run forever */
	public void run() {
		/* Connection to client Creating both Data Stream */
		try
		{
			// create output first
			out = new ObjectOutputStream(socket.getOutputStream());
			in  = new ObjectInputStream(socket.getInputStream());
			
			ChatMessage cm = (ChatMessage) StreamInOut.readData(in);
			username = cm.getUser();
			broadcast(new ChatMessage(username + " just connected.", "server"));
			ChatServer.display(username + " connected.");
			Thread.sleep(200); //TODO #concurrency it seems that writeMsg is overwriting broadcast stream
			StreamInOut.writeMsg(new ChatMessage("Welcome to the Chat-Server! \r\n There are " + (ChatServer.clients.size()) + " users online.", "server"), out);
		}
		catch (IOException e) {
			ChatServer.display("Exception creating new Input/output Streams: " + e);
			e.printStackTrace();
			return;
		}
			catch (InterruptedException e) {
			e.printStackTrace();
		}
		date = new Date();
		
		// to loop until LOGOUT
		boolean keepGoing = true;
		
		// the big while loop listening for incoming messages
		while(keepGoing) {
			ChatMessage cm = null;
			try {
				cm = (ChatMessage) StreamInOut.readData(in);
			}
			catch (IOException e) {
				ChatServer.clients.remove(this);
				try {
					broadcast(new ChatMessage(username + " disconnected.", "server"));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				ChatServer.display(username + " disconnected");
				//display(username + " Exception reading Streams: " + e);
				break;
			}
			catch(InterruptedException e2) {
				e2.printStackTrace();
				break;
			}
			try {
				Thread.sleep(500); // delay for feeling ;-)
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				broadcast(cm);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			// close input/output stream but keep socket alive for other clients!
			out.close();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
