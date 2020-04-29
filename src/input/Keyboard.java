package input;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

/** handles reading from keyboard input */
public class Keyboard {
	// the System Input Stream
	private static final InputStream in = System.in;
	private static Reader reader = new InputStreamReader(in, Charset.forName("UTF-8"));
	
	// line buffer
	private static StringBuilder line = new StringBuilder(1024);
	
	// current offset in line buffer (in readCharSequence)
	private static int lineOffset = -1;
	
	// last character that was read
	private static int lastChar = -1;
	
	// read and return next character
	private static int nextChar() throws IOException {
		//int c = in.read();
		int c = reader.read();
		if (c == -1) throw new IOException("Input stream closed.");
		return c;
	}
	
	/** read and return next line, excluding any line feed chars */
	public static String nextLine() {
		// clear buffer
		lineOffset = -1;
		line.setLength(0);
		// read until any line feed char is encountered
		try {
			int c;
			do {
				c = nextChar();
				if (c == '\n' && lastChar == '\r') c = nextChar(); // skip \n if last char was \r
				lastChar = c;
				//System.out.println("kb " + (char) c);
				line.append((char) c);
			} while (c != '\n' && c != '\r' && c != '\u0085' && c != '\u000C');
		} catch (IOException e) {
			// fatal error, terminate application
			System.out.println();
			System.out.println("Fatal Error: " + e.getMessage());
			System.exit(0);
		}
		line.setLength(line.length()-1);
		return line.toString();
	}
}
