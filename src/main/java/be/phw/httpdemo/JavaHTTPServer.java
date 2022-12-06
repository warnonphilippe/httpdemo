package be.phw.httpdemo;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.StringTokenizer;

public class JavaHTTPServer implements Runnable{

	static final String DEFAULT_FILE = "index.html";
	static final String FILE_NOT_FOUND = "404.html";

	static final String HTTP_SERVER_1_0 = "Server: Demo Java HTTP Server : 1.0";
	static final String HTTP_OK = "HTTP/1.1 200 OK";

	// Client Connection via Socket Class
	private Socket connect;

	private InfosService infosService;
	
	public JavaHTTPServer(Socket connect, InfosService infosService) {
		this.connect = connect;
		this.infosService = infosService;
	}
	
	@Override
	public void run() {
		// we manage our particular client connection
		BufferedReader in = null; PrintWriter out = null; BufferedOutputStream dataOut = null;
		String fileRequested = null;
		
		try {
			// we read characters from the client via input stream on the socket
			in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
			// we get character output stream to client (for headers)
			out = new PrintWriter(connect.getOutputStream());
			// get binary output stream to client (for requested data)
			dataOut = new BufferedOutputStream(connect.getOutputStream());
			
			// get first line of the request from the client
			String input = in.readLine();
			// we parse the request with a string tokenizer
			StringTokenizer parse = new StringTokenizer(input);
			String method = parse.nextToken().toUpperCase(); // we get the HTTP method of the client
			// we get file requested
			fileRequested = parse.nextToken().toLowerCase();

			infosService.log(fileRequested);

			// we support only GET and HEAD methods, we check
			if (Arrays.asList("GET", "HEAD").contains(method)) {

				// GET or HEAD method
				if (fileRequested.endsWith("/")) {
					fileRequested = DEFAULT_FILE;
				} else {
                    fileRequested = fileRequested.substring(1);
                }

				File file = getFileFromResource(fileRequested);

				int fileLength = (int) file.length();
				String content = getContentType(fileRequested);
				
				if (method.equals("GET")) { // GET method so we return content
					byte[] fileData = readFileData(file, fileLength);
					
					writeHeaders(out, fileLength, content, HTTP_OK, HTTP_SERVER_1_0);

					dataOut.write(fileData, 0, fileLength);
					dataOut.flush();
				}
				
			}
			
		} catch (FileNotFoundException fnfe) {
			fileNotFound(out, dataOut);
		} catch (Exception ex) {
			System.err.println("Server error : " + ex.getMessage());

        } finally {
			try {
				in.close();
				out.close();
				dataOut.close();
				connect.close(); // we close socket connection
			} catch (Exception e) {
				System.err.println("Error closing stream : " + e.getMessage());
			} 

		}
		
		
	}

	private File getFileFromResource(String fileRequested) throws FileNotFoundException, URISyntaxException {
		URL resource = getClass().getClassLoader().getResource(fileRequested);
		if (resource == null) {
			throw new FileNotFoundException("file not found!");
		}
		File file =  new File(resource.toURI());
		return file;
	}

	private void writeHeaders(PrintWriter out, int fileLength, String contentMimeType, String http, String server) {
		// we send HTTP Headers with data to client
		out.println(http);
		out.println(server);
		out.println("Date: " + new Date());
		out.println("Content-type: " + contentMimeType);
		out.println("Content-length: " + fileLength);
		out.println(); // blank line between headers and content, very important !
		out.flush(); // flush character output stream buffer
	}

	private byte[] readFileData(File file, int fileLength) throws IOException {
		FileInputStream fileIn = null;
		byte[] fileData = new byte[fileLength];
		
		try {
			fileIn = new FileInputStream(file);
			fileIn.read(fileData);
		} finally {
			if (fileIn != null) 
				fileIn.close();
		}
		
		return fileData;
	}
	
	// return supported MIME Types
	private String getContentType(String fileRequested) {
		if (fileRequested.endsWith(".htm")  ||  fileRequested.endsWith(".html"))
			return "text/html";
		else
			return "text/plain";
	}
	
	private void fileNotFound(PrintWriter out, OutputStream dataOut) {

		try {
			URL resource = getClass().getClassLoader().getResource(FILE_NOT_FOUND);
			File file =  new File(resource.toURI());

			int fileLength = (int) file.length();
			String content = "text/html";
			byte[] fileData = readFileData(file, fileLength);

			writeHeaders(out, fileLength, content, "HTTP/1.1 404 File Not Found", "Server: Demo Java HTTP Server : 1.0");

			dataOut.write(fileData, 0, fileLength);
			dataOut.flush();

		} catch (Exception ex){
			System.err.println("Server error : " + ex.getMessage());
		}

	}
	
}
