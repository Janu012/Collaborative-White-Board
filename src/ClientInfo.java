import java.io.*;
import java.net.*;
import java.util.*;

public class ClientInfo
{
  private Socket socket = null;			// client socket
  private boolean alive = true;			// alive status
  private BufferedReader brin = null;		// input
  private Date date = null;			// date of connection
  private String hostname = null;		// hostname of client
  private String hostport = null;		// description of client
  private boolean mute = true;		        // mute status
  private int port = -1;		        // port number of client
  private PrintWriter pwout = null;	        // output
  private boolean raisehand = false;	        // raisehand status
  private String type = null;		        // type of client
  private String username = null;	        // username of client
  public ClientInfo(Socket socket) throws IOException
  {
    this.socket = socket;
    this.date = new Date();
    this.hostname = socket.getInetAddress().getHostName();
    this.port = socket.getPort();
    this.hostport = this.hostname + "/" + this.port;
    this.username = this.hostport;
    this.brin =
      new BufferedReader(new InputStreamReader(socket.getInputStream()));
    this.pwout = new PrintWriter(socket.getOutputStream(), true);
    this.mute = true;
    this.raisehand = false;
    this.type = "student";
  }
  public void setAlive(boolean alive)		{ this.alive = alive; }
  public void setBrin(BufferedReader brin)	{ this.brin = brin; }
  public void setDate(Date date)		{ this.date = date; }
  public void setHostname(String hostname)	{ this.hostname = hostname; }
  public void setHostport(String hostport)	{ this.hostport = hostport; }
  public void setMute(boolean mute)		{ this.mute = mute; }
  public void setPort(int port)			{ this.port = port; }
  public void setPwout(PrintWriter pwout)	{ this.pwout = pwout; }
  public void setRaisehand(boolean raisehand)	{ this.raisehand = raisehand; }
  public void setSocket(Socket socket)		{ this.socket = socket; }
  public void setType(String type)		{ this.type = type; }
  public void setUsername(String username)	{ this.username = username; }
  public boolean getAlive()			{ return alive; }
  public BufferedReader getBrin()		{ return brin; }
  public Date getDate()				{ return date; }
  public String getHostname()			{ return hostname; }
  public String getHostport()			{ return hostport; }
  public boolean getMute()			{ return mute; }
  public int getPort()				{ return port; }
  public PrintWriter getPwout()			{ return pwout; }
  public boolean getRaisehand()			{ return raisehand; }
  public Socket getSocket()			{ return socket; }
  public String getType()			{ return type; }
  public String getUsername()			{ return username; }
    
}