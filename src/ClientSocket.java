import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;

public final class ClientSocket extends Socket {
  whiteBoard d_whiteBoard;	// pointer back to the ui
  String d_username;		// username associated with this client
  Thread d_reader;		// reader thread for this ClientSocket
  public ClientSocket(whiteBoard wb, String user, String host, int port)
    throws IOException, DupUserException {
    super(host, port);
    d_whiteBoard = wb;
    d_username = user; // remember our username
    send("server", "login", d_username);
    // Create reader thread
    d_reader = new ClientReader(this);
    // Start the reader thread and give control over to that thread
    d_reader.start();
  }
  public static String arrayToString(String [] array) {
    StringBuilder buf = new StringBuilder("");
    for(int i=0; i < array.length; ++i) {
      buf.append(array[i]);
      if(i != array.length-1)
	buf.append(',');	
    }
    return(buf.toString());
  }
  public static String [] stringToArray(String str) {
    StringTokenizer t = new StringTokenizer(str, ",");
    String [] array = new String[t.countTokens()];

    for(int i=0; t.hasMoreTokens(); ++i) {
      array[i] = t.nextToken();
    }
    return(array);
  }
  public void send(String recipient, String command, String arg) {
    doSend(d_username + ":" + recipient + ":" + command + ":" + arg);
  }
  public void send(String recipient, String command, String [] args) {
    doSend(d_username + ":" + recipient + ":" + command + ":" +
	   arrayToString(args));
  }
  public void send(String [] recipients, String command, String arg) {
    doSend(d_username + ":" + arrayToString(recipients) + ":" +
	   command + ":" + arg);
  }
  public void send(String [] recipients, String command, String [] args) {
    doSend(d_username + ":" + arrayToString(recipients) + ":" +
	   command + ":" + arrayToString(args));
  }
  public void doSend(String msg) {
    // THOR -- debugging
    //System.out.println("^^^^^^^^ outgoing message ^^^^^^^^");
    //System.out.println(msg);
    //System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
    try {
      PrintWriter o = new PrintWriter(getOutputStream());
      o.println(msg);
      o.flush();		// flush the output stream
    }
    catch(IOException e) {
      System.err.println("Problem communicating with server: " + e);
    }
  }

  public void dispatch(String msg) {
    StringTokenizer st = new StringTokenizer(msg, ":");
    String from = st.nextToken();
    String to   = st.nextToken();
    String cmd  = st.nextToken();
    String args = "";
    if(st.hasMoreTokens())	// some commands do not have arguments
      args = st.nextToken();
    if(cmd.equalsIgnoreCase("DUPHANDLE")) {
      d_whiteBoard.cp.dupUser(args);
    }

    // check to see if the message is addressed to me
    StringTokenizer tot = new StringTokenizer(to, ",");
    while(tot.hasMoreTokens()) {
      String who = tot.nextToken();

      if(who.equalsIgnoreCase(d_username) || who.equalsIgnoreCase("ALL") ||
	 (from.equalsIgnoreCase(d_username) && cmd.equalsIgnoreCase("chat"))) {

	if(cmd.equalsIgnoreCase("chat")) {
	  d_whiteBoard.cp.dispChat("From " + from + " to " + to + " --> " +
				   args);
	}
	else if(cmd.equalsIgnoreCase("userlist")) {
	  d_whiteBoard.cp.setUsers(stringToArray(args));
	}
	else if(cmd.equalsIgnoreCase("line")) {
	  StringTokenizer a = new StringTokenizer(args, ",");
	  
	  d_whiteBoard.dp.drawLine(Integer.parseInt(a.nextToken()),
				   Integer.parseInt(a.nextToken()),
				   Integer.parseInt(a.nextToken()),
				   Integer.parseInt(a.nextToken()),
				   Integer.parseInt(a.nextToken()),
				   new Color(Integer.parseInt(a.nextToken()),
					     Integer.parseInt(a.nextToken()),
					     Integer.parseInt(a.nextToken())));
	}
	else if(cmd.equalsIgnoreCase("rectangle")) {
	  StringTokenizer a = new StringTokenizer(args, ",");
	  
	  d_whiteBoard.dp.drawRect(Integer.parseInt(a.nextToken()),
				   Integer.parseInt(a.nextToken()),
				   Integer.parseInt(a.nextToken()),
				   Integer.parseInt(a.nextToken()),
				   Integer.parseInt(a.nextToken()),
				   new Color(Integer.parseInt(a.nextToken()),
					     Integer.parseInt(a.nextToken()),
					     Integer.parseInt(a.nextToken())));
	}
	else if(cmd.equalsIgnoreCase("circle")) {
	  StringTokenizer a = new StringTokenizer(args, ",");
	  
	  d_whiteBoard.dp.drawCircle(Integer.parseInt(a.nextToken()),
				     Integer.parseInt(a.nextToken()),
				     Integer.parseInt(a.nextToken()),
				     Integer.parseInt(a.nextToken()),
				     Integer.parseInt(a.nextToken()),
				     new Color(Integer.parseInt(a.nextToken()),
					       Integer.parseInt(a.nextToken()),
					       Integer.parseInt(a.nextToken()))
				     );
	}
	else if(cmd.equalsIgnoreCase("raisehand")) {
	  if(args.equalsIgnoreCase("true")) {
	    d_whiteBoard.cp.dispChat("(user " + from +
				     " wishes to speak)");
	  }
	  else {
	    d_whiteBoard.cp.dispChat("(user " + from +
				     " no longer wishes to speak)");
	  }
	}
	else if(cmd.equalsIgnoreCase("clearchatbox")) {
	  d_whiteBoard.cp.eraseChatBox();
	}
	else if(cmd.equalsIgnoreCase("clear")) {
	  d_whiteBoard.dp.erase();
	}
	else if(cmd.equalsIgnoreCase("muteclient")) {
	  if(args.equalsIgnoreCase("false")) {
	    d_whiteBoard.cp.dispChat("(you have been called on by " + from +
				     ")");

	  }
	  System.out.println("\t\tMUTECLIENT RECEIVED WITH ARGS --> " + args);
	}
	else {
	  System.err.println("Unrecognized command from server: '" + cmd +
			     "' -- ignoring.");
	}

	return;
      }
    }
  }
}
