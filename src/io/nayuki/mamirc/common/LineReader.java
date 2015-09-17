package io.nayuki.mamirc.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;


// Returns a byte array for each line parsed from the input stream.
public final class LineReader {
	
	/*---- Fields ----*/
	
	private InputStream input;
	
	private byte[] lineBuffer;
	private int lineLength;
	private boolean prevWasCr;
	
	private byte[] readBuffer;
	private int readLength;
	private int readOffset;
	
	
	/*---- Constructor ----*/
	
	public LineReader(InputStream in) {
		input = in;
		lineBuffer = new byte[1024];
		lineLength = 0;
		readBuffer = new byte[4096];
		readLength = 0;
		readOffset = 0;
		prevWasCr = false;
	}
	
	
	/*---- Methods ----*/
	
	// Has universal newline detection. The returned array is a unique instance, and contains no '\r' or '\n' characters.
	public byte[] readLine() throws IOException {
		if (lineBuffer == null)  // End of stream already reached previously
			return null;
		
		while (true) {
			// Gather more input data if needed
			while (readOffset >= readLength) {
				readLength = input.read(readBuffer);
				if (readLength == -1) {
					lineBuffer = null;
					readBuffer = null;
					return null;  // End of stream
				}
				readOffset = 0;
			}
			
			while (readOffset < readLength) {
				byte b = readBuffer[readOffset];
				readOffset++;
				switch (b) {
					case '\r':
					{
						byte[] result = Arrays.copyOf(lineBuffer, lineLength);
						lineLength = 0;
						prevWasCr = true;
						return result;
					}
					
					case '\n':
						if (prevWasCr) {
							prevWasCr = false;
							break;
						} else {
							byte[] result = Arrays.copyOf(lineBuffer, lineLength);
							lineLength = 0;
							return result;
						}
						
					default:
						if (lineLength == lineBuffer.length)
							lineBuffer = Arrays.copyOf(lineBuffer, lineBuffer.length * 2);
						lineBuffer[lineLength] = b;
						lineLength++;
						prevWasCr = false;
						break;
				}
			}
		}
	}
	
}