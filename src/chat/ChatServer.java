package chat;

import java.io.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import java.util.Date;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;

public class ChatServer {

	public static List<ClientThread> clients;  // list of clients connected to the server
	protected static ServerSocket server;

	static final int INVALID_LOGDIR = -1;
	protected static final int PORT = 3142;

	protected static void setupLogging() {
		String logdir = "../log";
		if (Files.exists(Paths.get(logdir))) {
			try {
				System.setOut(new PrintStream(new File(logdir + File.separator + "log")));
				System.setErr(new PrintStream(new File(logdir + File.separator + "err")));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.err.println("logdir '" + logdir + "' does not exist");
			System.exit(INVALID_LOGDIR);
		}
	}

	protected static void setupServerSocket() throws IOException {
		server = new ServerSocket(PORT);
		clients = new CopyOnWriteArrayList<ClientThread>();

		display("Server is listening on port " + PORT);
		display("waiting for connections...");
	}

	/** prints the message on server */
	public static void display(String txt) {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat ft = new SimpleDateFormat("hh:mm:ss");
		System.out.println(ft.format(date) + " " + txt);
	}

	/** clean up list of clients */
	protected static void maintain() {
		for (ClientThread t : clients) {
			if (!t.isAlive()) {
				clients.remove(t);
				System.out.println("client " + t.getId() + " disconnected.");
			}
		}
	}

	protected static ObjectInputStream in;
	protected static ObjectOutputStream out;

	public static void main(String[] args) throws IOException {

		//setupLogging();
		setupServerSocket();

		// wait for client connections
		while (true) {
			Socket client = null;
			try {
				if (server.isClosed()) {
					System.err.println("Server socket is down. Trying to restart server.");
				}
				client = server.accept();
				ClientThread t = new ClientThread(client); // make a thread of it
				clients.add(t); // save it in the ArrayList
				t.start();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				maintain();
			}
		} // #while
	} // #main
}
