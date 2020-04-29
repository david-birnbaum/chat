package chat.io;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketException;

import chat.message.ChatMessage;
import chat.message.Message;

public class StreamInOut {
	
	 
	/** use readObject() for Input */
	public static Object readData(ObjectInputStream in) throws IOException, InterruptedException {
		try {
			return in.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/** use writeObject() for Output */
	public static void writeMsg(Message msg, ObjectOutputStream out) throws IOException {
		out.writeObject(msg);
	}
	
	/** use readChar() for Input */
	public static ChatMessage readCharData(ObjectInputStream in) throws IOException, InterruptedException {

		StringBuilder sb = new StringBuilder();

		for (char b = 0; ((b = in.readChar()) >= 0);) {
		    //System.out.println(b + " " + (char) b);
		    char c = (char) b; // convert byte to char

		    //System.out.println("available: " + in.available());
	        sb.append(c);
		    if(in.available() == 0) break;
		}
		if(sb.length() == 0) throw new EOFException(); //TODO Can we do this more efficiently?
		
       	return parseData(sb.toString());
	}
	
	/** use writeChars(String s) for Output */
	public static void writeCharMsg(Message msg, ObjectOutputStream sOutput) {
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(msg.getUser());
			sb.append("/"); // separator
			sb.append(msg.getMessage());

			//sOutput.writeBytes(sb.toString());
			sOutput.writeChars(sb.toString());
			//sOutput.writeChars(sb.toString());
			//sOutput.writeUTF(sb.toString());
			
			sOutput.flush();
		} catch (SocketException e) {
			//TODO try to reconnect
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static ChatMessage parseData(String data) {
		if(data.length() == 0) return null;
		String[] data_splitted = data.split("/");
		if(data_splitted.length > 1) {
			return new ChatMessage(data_splitted[1], data_splitted[0]);
		}
		else return new ChatMessage("", data_splitted[0]);
	}
	
	/** Non-blocking method to read bytes */
	/*public static String readData0(ObjectInputStream sInput) throws IOException, InterruptedException {
		// available stream to be read
		int length = 0;
		while(length == 0) {
			length = sInput.available();
			//System.out.println("waiting for input: " + length + " bytes to read");
			Thread.sleep(200);
		}
		
        //System.out.println(length + " bytes available");
        byte[] buf = new byte[length]; // create buffer
        sInput.readFully(buf); // read the full data into the buffer
        
        // for each byte in the buffer
        StringBuilder sb = new StringBuilder();
        for (byte b:buf) {   
           char c = (char)b; // convert byte to char
           sb.append(c);
        }
       	return sb.toString();
	}*/
	
	public static void printBytes(byte[] array, String name) {
	    for (int k = 0; k < array.length; k++) {
	        System.out.println(name + "[" + k + "] = " + "0x" +
	            UnicodeFormatter.byteToHex(array[k]));
	    }
	}
	
	public static class UnicodeFormatter  {
		 
	   static public String byteToHex(byte b) {
	      // Returns hex String representation of byte b
	      char hexDigit[] = {
	         '0', '1', '2', '3', '4', '5', '6', '7',
	         '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
	      };
	      char[] array = { hexDigit[(b >> 4) & 0x0f], hexDigit[b & 0x0f] };
	      return new String(array);
	   }
	 
	   static public String charToHex(char c) {
	      // Returns hex String representation of char c
	      byte hi = (byte) (c >>> 8);
	      byte lo = (byte) (c & 0xff);
	      return byteToHex(hi) + byteToHex(lo);
	   }
		 
	} // class
}
