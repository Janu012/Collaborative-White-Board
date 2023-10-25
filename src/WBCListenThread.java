
import java.io.*;
import java.net.*;
import java.util.*;

public class WBCListenThread extends Thread
{
  private Socket socket = null;
  private ClientInfo clientinfo = null;
  private Hashtable clienthash = null;
  public WBCListenThread(Hashtable clienthash, Socket socket)
  {
    super("WBCListenThread");
    this.socket = socket;
    this.clienthash = clienthash;
    try {
      // create a new ClientInfo object to store client information
      this.clientinfo = new ClientInfo(socket);
      // make sure we don't interfere with other threads
      synchronized ( clienthash ) {
	clienthash.put(this.clientinfo.getUsername(), this.clientinfo);
      }
    }
    catch (IOException e) {
    }
  }
  @Override
  public void run()
  {
    BufferedReader in = clientinfo.getBrin();
    PrintWriter out = clientinfo.getPwout();
    String inputLine, outputLine;
    ClientInfo ci;
    try {
      // display message indicating new connection
      System.out.println("### Connected to " + clientinfo.getHostport());
      // send an initial chat message to the client telling some basic info
      outputLine = "SERVER:ALL:CHAT: Your hostname = " +
	clientinfo.getHostname() + "\nSERVER:ALL:CHAT: Your port     = " +
	clientinfo.getPort() + "\nSERVER:ALL:CHAT: Connected on  = " +
	clientinfo.getDate() + "\n";
      // spawn a thread to send this string to the client
      if ( clientinfo.getAlive() ) {
	new WBCPrintThread(clientinfo, outputLine).start();
      }
      // broadcast a new userlist to all connected clients
      WBCParser.doUserlistBroadcast(clienthash);

      // if the client is still "alive", sit on the socket and read any
      // strings that come from the client.  Call the parser on the strings.
      while ( clientinfo.getAlive() &&
	      ((inputLine = in.readLine()) != null) ) {
	// the parser handles all commands
	WBCParser.parseCommand(clienthash, clientinfo, out, inputLine);
	if ( WBServer.debug ) {
	  System.out.println("--- " + clientinfo.getHostport() + " \"" +
			     clientinfo.getUsername() + "\": " + inputLine);
	}
      }
      // shut down the client
      out.close();
      in.close();
      socket.close();

      // remove this client's entry from the clienthash hashtable
      ci = (ClientInfo) clienthash.get(clientinfo.getUsername());
      if ( ci == clientinfo ) {
	// make sure we don't interfere with other threads accessing clienthash
	synchronized ( clienthash ) {
	  clienthash.remove(clientinfo.getUsername());
	}
	if ( WBServer.debug ) {
	  System.out.println("### Removed \"" + clientinfo.getUsername() +
			     "\" from clienthash");
	}
      }
      // broadcast a new userlist to all connected clients
      WBCParser.doUserlistBroadcast(clienthash);

      // display a message that the client has disconnected
      System.out.println("### Disconnected " + clientinfo.getHostport());
      if ( WBServer.debug ) {
	for ( Enumeration en = clienthash.elements(); en.hasMoreElements(); ) {
	  ci = (ClientInfo) en.nextElement();
	  System.out.println("%%% clienthash: " + ci.getUsername());
	}
      }
    } catch (IOException e) {
    }

  }
}
