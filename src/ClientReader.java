import java.io.*;

class ClientReader extends Thread {
  ClientSocket d_socket;
  public ClientReader(ClientSocket c) {
    super("Client Reader");
    this.d_socket = c;
  }
  @Override
  public void run() {
    BufferedReader d_in = null;
    String d_line;
    try {
      d_in =
              new BufferedReader(new InputStreamReader(d_socket.getInputStream()));
      while(true) {
	d_line = d_in.readLine();

	if(d_line == null) { 
	  System.out.println("Server closed connection.");
	  break;
	}
	// THOR -- debugging
	//System.out.println("======== incoming message ========");
	//System.out.println(d_line);
	//System.out.println("==================================");
	// dispatch the incoming command to the ClientSocket
	d_socket.dispatch(d_line);
      }
    }
    catch(IOException e) {
      System.out.println("ClientReader: " + e);
    }
    finally {
      try {
	if(d_in != null)
	  d_in.close();
      }
      catch(IOException e) {
	System.out.println("ClientReader: " + e);
      }
    }
    System.exit(0);
  }
}