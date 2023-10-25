
import java.io.*;
import java.net.*;
import java.util.*;

public class WBSListenThread extends Thread
{
  private Hashtable clienthash = null;
  private final boolean listening = true;
  private int port = 1998;
  private ServerSocket serverSocket = null;

  public WBSListenThread(Hashtable clienthash, int port)
  {
    super("WBSListenThread");
    this.clienthash = clienthash;
    this.port = port;
  }
  @Override
  public void run()
  {
    // open the server listening socket at port
    try {
      serverSocket = new ServerSocket(port);
    }
    catch (IOException e) {
      System.err.println("error: cannot listen on port: " + port);
      System.exit(-1);
    }
    // for each new connection we see on the server listening port,
    // spawn a new thread to connect to the client and listen to it.
    try {
      while ( this.listening )
	new WBCListenThread(clienthash, serverSocket.accept()).start();
      serverSocket.close();
    }
    catch (IOException e) {
    }
  }
}
