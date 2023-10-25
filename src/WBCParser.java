import java.io.*;
import java.util.*;

public class WBCParser
{ 
  public static int doBroadcast(Hashtable clienthash, String msg)
  {
    ClientInfo ci;
    // iterate through each client in the Hashtable and spawn a thread
    // to send the msg to each
    for ( Enumeration en = clienthash.elements(); en.hasMoreElements(); ) {
      ci = (ClientInfo) en.nextElement();
      if ( ci.getAlive() ) {
	new WBCPrintThread(ci, msg).start();
      }
    }
    return 1;
  }
  public static int doChangetype(Hashtable clienthash, ClientInfo clientinfo,
				 PrintWriter out, String cmd, String from,
				 String to, String command, String args)
  {
      String old;
    // makes no sense if no argument is given
    if ( args == null )
      return 0;
    // a no-op if the new type is the same as the old
    old = clientinfo.getType();
    if ( old.equalsIgnoreCase(args) )
      return 0;

    // change the type
    clientinfo.setType(args);

    doUserlistBroadcast(clienthash);

    if ( WBServer.debug ) {
      System.out.println("### CHANGETYPE: " + clientinfo.getHostport() +
			 " \"" + clientinfo.getUsername() + "\": \"" +
			 old + "\" -> \"" + clientinfo.getType() + "\"");
    }

    return 1;
  }
 
  
  public static int doGeneric(Hashtable clienthash, ClientInfo clientinfo,
				PrintWriter out, String cmd, String from,
				String to, String command, String args)
  {
    boolean all, allowed;
    ClientInfo ci;
    StringTokenizer tok1;
    String msg, user;
      // determine if this client is allowed to do broadcasts
      allowed = clientinfo.getType().equalsIgnoreCase("teacher") ||
              !clientinfo.getMute();
    // determine if this client wants to do a broadcast
    all = false;
    for ( tok1 = new StringTokenizer(to, ","); tok1.hasMoreTokens(); ) {
      user = tok1.nextToken();
      if ( user.equalsIgnoreCase("all") )
	all = true;
    }
    // if this is a broadcast...
    if ( all ) {
      // only do it if the client is allowed to do broadcasts
      if ( allowed ) {
	// does the command have any arguments?
	if ( args == null ) {
	  msg = clientinfo.getUsername() + ":ALL:" + command + ":" + "\n";
	}
	else {
	  msg = clientinfo.getUsername() + ":ALL:" + command + ":" + args +
	    "\n";
	}
	// send the broadcast out
	doBroadcast(clienthash, msg);
      }
      // return 0 if the client is not allowed to broadcast
      else {
	return 0;
      }
    }
    // if this is a private message sent to specific individuals
    else {
      // does the command have any arguments
      if ( args == null )
	msg = clientinfo.getUsername() + ":" + to + ":" + command + ":" + "\n";
      else
	msg = clientinfo.getUsername() + ":" + to + ":" + command + ":" +
	  args + "\n";

      // go through the list of targetted usernames in the comma separated
      // "to" field
      for ( tok1 = new StringTokenizer(to, ","); tok1.hasMoreTokens(); ) {
	user = tok1.nextToken();
	// can we do a direct lookup on this username?
	if ( (ci = (ClientInfo) clienthash.get(user)) != null ) {
	  // hash get succeeded, so send the command to this client
	  if ( ci.getAlive() ) {
	    new WBCPrintThread(ci, msg).start();
	  }
	}
	// we couldn't find the target client, so iterate through the
	// clienthash to see if we can find the destination using a
	// caseless string match.
	else {
	  // check every client
	  for ( Enumeration en = clienthash.elements();
		en.hasMoreElements(); ) {
	    ci = (ClientInfo) en.nextElement();
	    // found the target client, so send the command to it
	    if ( ci.getUsername().equalsIgnoreCase(user) ) {
	      if ( ci.getAlive() ) {
		new WBCPrintThread(ci, msg).start();
	      }
	    }
	  }
	}
      }
      // echo the command back to the sender 
      if ( clientinfo.getAlive() ) {
	new WBCPrintThread(clientinfo, msg).start();
      }
    }

    if ( WBServer.debug ) {
      System.out.println("### COMMAND: " + clientinfo.getHostport() +
			 " \"" + clientinfo.getUsername() + "\": \"" +
			 msg + "\"");
    }  
    return 1;
  }
  public static int doLogin(Hashtable clienthash, ClientInfo clientinfo,
			    PrintWriter out, String cmd, String from,
			    String to, String command, String args)
  {
    boolean found;
    ClientInfo ci, ci2;
    String old;
    // makes no sense if no argument is specified
    if ( args == null )
      return 0;
    // check if the new username contains illegal ":" or "," characters
    if ( (args.contains(":")) || (args.contains(",")) )
      return 0;
    found = false;
    if ( args.equalsIgnoreCase("server") || args.equalsIgnoreCase("all") ) {
      found = true;
    }

    ci = null;
    if ( !found ) {
      // see if any other client is using this username
      for ( Enumeration en = clienthash.elements(); en.hasMoreElements(); ) {
	ci2 = (ClientInfo) en.nextElement();
	// found a client with this username already
	if ( ci2.getUsername().equalsIgnoreCase(args) ) {
	  ci = ci2;
	  found = true;
	}
      }
    }

    // no one is using this username, so this client may use it
    if ( !found ) {
      // remove this client's ClientInfo from clienthash because it's
      // using the old username.  We synchronize on the clienthash so
      // as not to interfere with other threads.
      old = clientinfo.getUsername();
      synchronized ( clienthash ) {
	clienthash.remove(old);
      }
      // change to the new username
      clientinfo.setUsername(args);
      // Insert this client's ClientInfo back into the clienthash, but
      // keyed on the new username.
      synchronized ( clienthash ) {
	clienthash.put(clientinfo.getUsername(), clientinfo);
      }

      if ( WBServer.debug ) {
	System.out.println("### LOGIN: " + clientinfo.getHostport() +
			   " \"" + clientinfo.getUsername() + "\": \"" +
			   old + "\" -> \"" + clientinfo.getUsername() + "\"");
      }
      // Broadcast the new userlist to all the clients
      doUserlistBroadcast(clienthash);
    }
    // someone else is using this username, so send a duphandle response
    // to the client
    else if ( ci != clientinfo ) {
      if ( WBServer.debug ) {
	System.out.println("### DUPHANDLE: " + clientinfo.getHostport() +
			   " \"" + clientinfo.getUsername() +
			   "\": cannot \"" +
			   clientinfo.getUsername() + "\" -> \"" + args
			   + "\"");
      }

      // send duphandle message to the client
      if ( clientinfo.getAlive() ) {
	new WBCPrintThread(clientinfo, "SERVER:" + clientinfo.getUsername() +
			   ":DUPHANDLE:" + args + "\n").start();
      }
      return 0;
    }

    return 1;
  }

  public static int doMuteclient(Hashtable clienthash, ClientInfo clientinfo,
				 PrintWriter out, String cmd, String from,
				 String to, String command, String args)
  {
    boolean all, newstate, old;
    ClientInfo ci;
    String msg, user;
    StringTokenizer tok1 = null;
    // makes no sense if there's no argument
    if ( args == null )
      return 0;
    // only clients of type "teacher" are allowed to issue muteclient commands
    if ( !clientinfo.getType().equalsIgnoreCase("teacher") )
      return 0;
    // see if the muteclient argument is true or false
    newstate = true;
    if ( args != null ) {
      if ( args.equalsIgnoreCase("true") )
	newstate = true;
      else if ( args.equalsIgnoreCase("false") )
	newstate = false;
    }
    // see if this command is being sent to all clients
    all = false;
    for ( tok1 = new StringTokenizer(to, ","); tok1.hasMoreTokens(); ) {
      user = tok1.nextToken();
      if ( user.equalsIgnoreCase("all") )
	all = true;
    }
    // this muteclient command is being sent to all clients
    if ( all ) {
      // change the muteclient status for all clients, and set all
      // their raisehand status to be false
      msg = clientinfo.getUsername() + ":ALL:MUTECLIENT:" + newstate + "\n";
      // Iterate through all clients in clienthash
      for ( Enumeration en = clienthash.elements(); en.hasMoreElements(); ) {
	ci = (ClientInfo) en.nextElement();
	old = ci.getMute();
	// set the mute status
	if ( old != newstate )
	  ci.setMute(newstate);
	// set the raisehand status
	if ( ci.getRaisehand() )
	  ci.setRaisehand(false);
	// send the client a message telling them what's happened
	if ( ci.getAlive() ) {
	  new WBCPrintThread(ci, msg).start();
	}
      }
    }
    // this muteclient command is being sent only to specific clients
    else {
      // the message to be sent to the clients
      msg = clientinfo.getUsername() + ":" + to + ":MUTECLIENT:" +
	newstate + "\n";

      // go through the list of usernames in the "to" field and see if we
      // can find them with a hash get
      for ( tok1 = new StringTokenizer(to, ","); tok1.hasMoreTokens(); ) {
	user = tok1.nextToken();
	// hash get succeeded
	if ( (ci = (ClientInfo) clienthash.get(user)) != null ) {
	  old = ci.getMute();
	  // set the mute status
	  if ( old != newstate )
	    ci.setMute(newstate);
	  // set the raisehand status
	  if ( ci.getRaisehand() )
	    ci.setRaisehand(false);
	  // send a message to the client
	  if ( ci.getAlive() ) {
	    new WBCPrintThread(ci, msg).start();
	  }
	}
	// couldn't find the targetted client with a hash get, so
	// check all clients with a caseless string match on the username
	else {
	  // Iterate through all clients in clienthash
	  for ( Enumeration en = clienthash.elements();
		en.hasMoreElements(); ) {
	    ci = (ClientInfo) en.nextElement();
	    // found the client we want
	    if ( ci.getUsername().equalsIgnoreCase(user) ) {
	      old = ci.getMute();
	      // set the mute status
	      if ( old != newstate )
		ci.setMute(newstate);
	      // set the raisehand status
	      if ( ci.getRaisehand() )
		ci.setRaisehand(false);
	      // send a message to the client
	      if ( ci.getAlive() ) {
		new WBCPrintThread(ci, msg).start();
	      }
	    }
	  }
	}
      }
    }

      int doUserlistBroadcast = doUserlistBroadcast(clienthash);
    if ( WBServer.debug ) {
      System.out.println("### MUTECLIENT: " + clientinfo.getHostport() +
			 " \"" + clientinfo.getUsername() + "\": \"" +
			 cmd + "\"");
    }
    return 1;
  }
 
  public static int doRaisehand(Hashtable clienthash, ClientInfo clientinfo,
				PrintWriter out, String cmd, String from,
				String to, String command, String args)
  {
    boolean newstate, old;
    String msg;
    // see if the new status is true or false
    old = clientinfo.getRaisehand();
    newstate = true;
    if ( args != null ) {
      if ( args.equalsIgnoreCase("true") )
	newstate = true;
      else if ( args.equalsIgnoreCase("false") )
	newstate = false;
    }
    // a no-op if there's no change
    if ( old == newstate )
      return 0;
    // change the raisehand status
    clientinfo.setRaisehand(newstate);
    if ( WBServer.debug ) {
      System.out.println("### RAISEHAND: " + clientinfo.getHostport() +
			 " \"" + clientinfo.getUsername() + "\": \"" +
			 old + "\" -> \"" + clientinfo.getRaisehand() + "\"");
    }
    // broadcast the new raisehand status to all the clients
    msg = clientinfo.getUsername() + ":ALL:RAISEHAND:" + newstate + "\n";
    doBroadcast(clienthash, msg);
    
    return 1;
  }
  public static int doShutdown(Hashtable clienthash, ClientInfo clientinfo,
			       PrintWriter out, String cmd, String from,
			       String to, String command, String args)
  {
    if ( WBServer.debug ) {
      System.out.println("### SHUTDOWN: " + clientinfo.getHostport() +
			 " \"" + clientinfo.getUsername() + "\"");
    }
    out.println("SERVER:" + clientinfo.getUsername() + ":chat:" + "Bye!");
    // tell the WBCListenThread that it should end as soon as possible
    clientinfo.setAlive(false);

    return 1;
  }
  public static int doUserlist(Hashtable clienthash, ClientInfo clientinfo,
			       PrintWriter out, String cmd, String from,
			       String to, String command, String args)
  {
    boolean first = true;
    ClientInfo ci;
    String msg = null;
    // create a userlist command containing the username, host, and port
    // of every connected client
    msg = "SERVER:" + clientinfo.getUsername() + ":USERLIST:";
    for ( Enumeration en = clienthash.elements(); en.hasMoreElements(); ) {
      ci = (ClientInfo) en.nextElement();
      if ( first ) {
	msg += ci.getUsername() + "," + ci.getType() + "," +
	  ci.getMute() + "," + ci.getHostname() + "," + ci.getPort();
	first = false;
      }
      else {
	msg += "," + ci.getUsername() + "," + ci.getType() + "," +
	  ci.getMute() + "," + ci.getHostname() + "," +  ci.getPort();
      }
    }
    msg += "\n";
    // send the userlist back to the client
    if ( clientinfo.getAlive() ) {
      new WBCPrintThread(clientinfo, msg).start();
    }
    if ( WBServer.debug ) {
      System.out.print("### USERLIST: " + msg);
      System.out.flush();
    }
    return 1;
  }
  public static int doUserlistBroadcast(Hashtable clienthash)
  {
    boolean first = true;
    ClientInfo ci;
    String msg = null;
    // create a userlist command containing the username, host, and port
    // of every connected client
    msg = "SERVER:ALL:USERLIST:";
    for ( Enumeration en = clienthash.elements(); en.hasMoreElements(); ) {
      ci = (ClientInfo) en.nextElement();
      if ( first ) {
	msg += ci.getUsername() + "," + ci.getType() + "," +
	  ci.getMute() + "," + ci.getHostname() + "," + ci.getPort();
	first = false;
      }
      else {
	msg += "," + ci.getUsername() + "," + ci.getType() + "," +
	  ci.getMute() + "," + ci.getHostname() + "," + ci.getPort();
      }
    }
    msg += "\n";
    // broadcast the userlist message to all connected clients
    doBroadcast(clienthash, msg);

    if ( WBServer.debug ) {
      System.out.print("### USERLIST: " + msg);
      System.out.flush();
    }

    return 1;
  }

  public static int parseCommand(Hashtable clienthash, ClientInfo clientinfo,
				  PrintWriter out, String cmd)
  {
    StringTokenizer tok1;
    String from, to, command, args;
    tok1 = new StringTokenizer(cmd, ":");
    if ( tok1.countTokens() < 3 ) {
      doBroadcast(clienthash,
		  clientinfo.getUsername() + ":ALL:CHAT:" + cmd + "\n");
      return 0;
    }
    from = tok1.nextToken();			// who sent the command
    to = tok1.nextToken();			// intended recipient
    command = tok1.nextToken();			// command word
    if ( tok1.hasMoreTokens() )			// arguments, if any
      args = tok1.nextToken("");
    else
      args = null;

    // handle the different types of commands
    if ( command.equalsIgnoreCase("changetype") ) {
      doChangetype(clienthash, clientinfo, out, cmd, from, to, command, args);
    }
    else if ( command.equalsIgnoreCase("login") ) {
      doLogin(clienthash, clientinfo, out, cmd, from, to, command, args);
    }
    else if ( command.equalsIgnoreCase("muteclient") ) {
      doMuteclient(clienthash, clientinfo, out, cmd, from, to, command, args);
    }
    else if ( command.equalsIgnoreCase("raisehand") ) {
      doRaisehand(clienthash, clientinfo, out, cmd, from, to, command, args);
    }
    else if ( command.equalsIgnoreCase("shutdown") ) {
      doShutdown(clienthash, clientinfo, out, cmd, from, to, command, args);
    }
    else if ( command.equalsIgnoreCase("userlist") ) {
      doUserlist(clienthash, clientinfo, out, cmd, from, to, command, args);
    }
    // no specific handler for this type of command, so we just
    // send it along
    else {
      doGeneric(clienthash, clientinfo, out, cmd, from, to, command, args);
    }
    return 1;
  }
}