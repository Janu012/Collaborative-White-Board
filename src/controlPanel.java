import java.awt.*;
import java.awt.event.*;
import java.io.*;

class controlPanel extends Frame implements ActionListener, WindowListener, 
	    KeyListener 
{
  /** Are we currently logged into a server? */
  private boolean loggedIn = false;
    /** Whether or not we are currently allowed to talk */

  /** User list (who's currently logged in */
  private String userList[];

  /** Text area to display incoming chats in */
  private final TextArea chatBox = new TextArea(20,60);

  /** Text box to enter new chat messages */
  private final TextField chatArea = new TextField("",40);

  /** Our user name */
  private String uname = null;

  /** Our type */
  private String type = null;

  /**
   * Function called when a supervisor removes our ability to send messages
   */
  public void mute()
  {
    raiseHandButton.setEnabled(true); //Allow the student to raise their hand
  }
  /** 
   * Function called when our ability to send messages is restored
   */
  public void unMute()
  {
    raiseHandButton.setEnabled(false); //No need to bother with hand raising
  }
  public void setUsers(String s[])
  {
    userList = s;
    regenUserList();
    pack();
  }
  private void regenUserList()
  {
    int i;

    if ((userList.length % 5) != 0) {
      ErrorDialog ed = new ErrorDialog(this,"Bad UserList",
				       "Malformed user list received");
      ed.show();
      return;
    }

    guiUserList.removeAll();
    for(i=0;i<userList.length;i+=5) {
      if (userList[i].equals(uname)) {
	type = userList[i+1];
	our_parent.setType(type);
	if (userList[i+2].equalsIgnoreCase("false")) {
	  setHand(false);
	}
      }
      if (userList[i+1].equalsIgnoreCase("teacher") == false ) {
	if (userList[i+2].equalsIgnoreCase("true")) {
	  guiUserList.addItem(userList[i] + " (" + userList[i+1] + "," 
			      + "muted)");
	} else {
	  guiUserList.addItem(userList[i] + " (" + userList[i+1] + "," 
			      + "unmuted)");
	}
      } else {
	  guiUserList.addItem(userList[i] + " (" + userList[i+1] + ")");
      }	
    }
  }

  private void add(Component c, GridBagLayout gbl, GridBagConstraints gbc,
                   int x, int y, int w, int h)
  {
    gbc.gridx = x;
    gbc.gridy = y;
    gbc.gridwidth = w;
    gbc.gridheight = h;
    gbl.setConstraints(c, gbc);
    add(c);
  }

  /** A status bar that the program can display messages with */
  private final Label statusBar = new Label("Welcome to the White Board",Label.CENTER);
  public void setMessage(String s)
  {
    statusBar.setText(s);
  }
  private final Button loginButton;
  private final Button raiseHandButton;
  private final Button statusButton;
  private final Button muteButton;
  private final Button unmuteButton;
  private final Button sendButton;
  private final Button clearChatButton;
  private final Button quitButton;
  private final List guiUserList;
  private final whiteBoard our_parent;
  public controlPanel(whiteBoard p)
  {
    GridBagLayout gbl = new GridBagLayout();
    GridBagConstraints gbc = new GridBagConstraints();

    our_parent = p;
    gbc.weightx = 0;
    gbc.weighty = 0;
    gbc.insets = new Insets(5,5,5,5);
    setLayout(gbl);

    add(statusBar,gbl,gbc,0,0,6,1);  //6 wide, 1 high, top row

    gbc.fill    = GridBagConstraints.BOTH;
    loginButton = new Button("Login");
    loginButton.addActionListener(this);
    add(loginButton,gbl,gbc,0,1,2,1); //2 wide, 1 high, second row

    raiseHandButton = new Button("Raise Hand");
    raiseHandButton.addActionListener(this);
    raiseHandButton.setEnabled(false); //Initially have no need to raise hands
    add(raiseHandButton,gbl,gbc,0,2,2,1); //2 wide, 1 high, third row

    statusButton = new Button("Become an Instructor");
    statusButton.setEnabled(false); //Disabled till we login
    statusButton.addActionListener(this);
    add(statusButton,gbl,gbc,0,3,2,1);   //2 wide, 1 high, fourth row

    muteButton = new Button("Mute");
    muteButton.addActionListener(this);
    muteButton.setEnabled(false); //Disabled until we become a wizard
    add(muteButton,gbl,gbc,0,4,1,1); //1 wide, 1 high, fifth row

    unmuteButton = new Button("UnMute");
    unmuteButton.addActionListener(this);
    unmuteButton.setEnabled(false); //Disabled until we become a wizard
    add(unmuteButton,gbl,gbc,1,4,1,1); //1 wide, 1 high, fifth row

    clearChatButton = new Button("Clear Chat Box");
    clearChatButton.addActionListener(this);
    clearChatButton.setEnabled(false); //Disabled until we become a wizard
    add(clearChatButton,gbl,gbc,0,5,2,1); //1 wide, 1 high, fifth row

    quitButton = new Button("Quit");
    quitButton.addActionListener(this);
    add(quitButton,gbl,gbc,0,6,2,1); //2 wide, 1 high, sixth row

    gbc.fill    = GridBagConstraints.BOTH;
    guiUserList = new List(10,true);
    guiUserList.setEnabled(false);
    add(guiUserList,gbl,gbc,2,1,4,6);  //4 wide, 6 high, second row

    chatBox.setEditable(false); // Read only
    chatBox.setEnabled(false);  // Disabled till we log in
    add(chatBox,gbl,gbc,0,7,6,2);  // 6 wide, 2 high, sixth and seventh rows

    sendButton = new Button("Send");
    sendButton.setEnabled(false);
    sendButton.addActionListener(this);
    add(sendButton,gbl,gbc,0,9,1,1); // 1 wide, 1 high, 8th row

    chatArea.setEnabled(false); // Disabled till we log in
    chatArea.addKeyListener(this);
    add(chatArea,gbl,gbc,1,9,5,1); // 5 wide, 1 high, 8th row

    pack();
  }

  public void dispChat(String s)
  {
    chatBox.append(s + "\n");
  }
  public void doSend()
  {
    String s = chatArea.getText();
    String tmp = "";
    chatArea.setText("");

    if ((s.trim().equals("")) == false) {
      int indices[] = guiUserList.getSelectedIndexes();
      int i = 0;
      if (indices.length == 0) { tmp = "ALL"; }
      for(i=0;i<indices.length;i++) {
	if (i != (indices.length-1)) {
	  tmp += userList[indices[i]*5] + ",";
	} else {
	  tmp += userList[indices[i]*5];
	}
      }
      our_parent.socket.send(tmp,"chat",s.trim());
    }
  }
  private void doLogin()
  {
    loginDialog ld = new loginDialog(this);

    ld.setTitle("Login");
    ld.show();
    if (ld.getStatus()) {  //Valid data entered
      String userName = ld.getUserName();
      String hostName = ld.getHostName();
      int port        = ld.getPortNo();

      // Set up a cool networking object, probably passing it our_parent
      try {
	our_parent.connect(userName,hostName,port);
      } catch (DupUserException due) {
	ErrorDialog ed = new ErrorDialog(this,"Duplicate Handle",
					 "That user name is already in use.");
	ed.show();
	return;
      } catch (IOException ioe) {
	ErrorDialog ed = new ErrorDialog(this,"Connection Error",
					 "IO Error on connect: " + ioe);
	ed.show();
	return;
      }
      setStatus(true);
      uname = userName;
    }
  }

  public void dupUser(String username)
  {
    ErrorDialog ed = new ErrorDialog(this,"Duplicate Handle",
				     "User name " + username + " is already in use.");
    ed.show();
    setStatus(false);
    uname = "";
  }
  private void doLogout()
  {
    our_parent.socket.send("server","shutdown","");

    setStatus(false);  
  }

  public void setLevel(boolean areTeacher)
  {
    if (areTeacher) {
      statusButton.setLabel("Become a Student");
      muteButton.setEnabled(true);
      unmuteButton.setEnabled(true);
      raiseHandButton.setEnabled(false);
      clearChatButton.setEnabled(true);
    } else {
      statusButton.setLabel("Become an Instructor");
      muteButton.setEnabled(false);
      unmuteButton.setEnabled(false);
      raiseHandButton.setEnabled(true);
      clearChatButton.setEnabled(false);
    }
  }
  private void setStatus(boolean on)
  {
    statusButton.setEnabled(on);
    sendButton.setEnabled(on);
    chatArea.setEnabled(on);
    chatBox.setEnabled(on);
    guiUserList.setEnabled(on);
    muteButton.setEnabled(false);
    unmuteButton.setEnabled(false);

    guiUserList.removeAll();  //Empty the user list

    if(on) {
      loginButton.setLabel("Log Out");
      raiseHandButton.setEnabled(true);
    } else {
      loginButton.setLabel("Login");
      try {our_parent.socket.close();} catch (IOException e) {}
      our_parent.socket = null;
      raiseHandButton.setEnabled(false);
    }
    loggedIn = on;
  }
  void doQuit()
  {
    if (loggedIn) {
      doLogout();
    }
    our_parent.doQuit();
  }
//
 // void doSU()
 // {
   // if (type.equalsIgnoreCase("teacher")) {
   //   our_parent.socket.send("server","changetype","student");
   // } else {
  //    our_parent.socket.send("server","changetype","teacher");
  //  }
 // }
      
  public void doSU() {
    if (this.type != null && this.type.equalsIgnoreCase("student")) {
        // Handle student logic
    } else if (this.type != null && this.type.equalsIgnoreCase("teacher")) {
        // Handle teacher logic
    }
}
    
  void doMute()
  {
    if (type.equalsIgnoreCase("teacher")) {
      String tmp = "";

      int indices[] = guiUserList.getSelectedIndexes();
      int i = 0;
      if (indices.length == 0) { tmp = "ALL"; }
      for(i=0;i<indices.length;i++) {
	if (i != (indices.length-1)) {
	  tmp += userList[indices[i]*5] + ",";
	} else {
	  tmp += userList[indices[i]*5];
	}
      }
      our_parent.socket.send(tmp,"muteclient","true");  
    }
  }
  void doUnMute()
  {
    if (type.equalsIgnoreCase("teacher")) {
      String tmp = "";

      int indices[] = guiUserList.getSelectedIndexes();
      int i = 0;
      if (indices.length == 0) { tmp = "ALL"; }
      for(i=0;i<indices.length;i++) {
	if (i != (indices.length-1)) {
	  tmp += userList[indices[i]*5] + ",";
	} else {
	  tmp += userList[indices[i]*5];
	}
      }
      our_parent.socket.send(tmp,"muteclient","false");
      
    }
  }
  void doSignal()
  {
    if (raiseHandButton.getLabel().equals("Raise Hand")) {
      our_parent.socket.send("server","raisehand","true");
      setHand(true);
    } else {
      our_parent.socket.send("server","raisehand","false");
      setHand(false);
    }
  }
  public void setHand(boolean x)
  {
    if (raiseHandButton.getLabel().equals("Raise Hand")) {
      raiseHandButton.setLabel("Lower Hand");
    } else {
      raiseHandButton.setLabel("Raise Hand");
    }
  }
  void doClear()
  {
    chatBox.setText("");
    our_parent.socket.send("all","clearchatbox","");
  }
  public void eraseChatBox()
  {
    chatBox.setText("");
  }
  @Override
  public void actionPerformed(ActionEvent event)
  {
    Object src = event.getSource();

    if (src == loginButton) {
      if (loggedIn) {
	doLogout();
      } else {
	doLogin();
      } 
    } else if (src == quitButton) {
      doQuit();
    } else if (src == sendButton) {
      doSend();
    } else if (src == statusButton) {
      doSU();
    } else if (src == muteButton) {
      doMute();
    } else if (src == unmuteButton) {
      doUnMute();
    } else if (src == raiseHandButton) {
      doSignal();
    } else if (src == clearChatButton) {
      doClear();
    }
  }

  @Override
  public void windowClosed(WindowEvent event){
  }
  @Override
  public void windowDeiconified(WindowEvent event){
  }
  @Override
  public void windowIconified(WindowEvent event){
  }
  @Override
  public void windowActivated(WindowEvent event){
  }
  @Override
  public void windowDeactivated(WindowEvent event){
  }
  @Override
  public void windowOpened(WindowEvent event){
  }
  @Override
  public void windowClosing(WindowEvent event)
  {
    doQuit();
  }
  @Override
  public void keyTyped(KeyEvent evt)
  {
    if (evt.getKeyChar() == '\n') doSend();
  }
  @Override
  public void keyPressed(KeyEvent evt){
  }
  @Override
  public void keyReleased(KeyEvent evt){
  }
}



