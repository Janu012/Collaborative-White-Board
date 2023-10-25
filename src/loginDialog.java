import java.awt.*;
import java.awt.event.*;

public class loginDialog extends Dialog implements ActionListener,
                                                   WindowListener
{
  /** To enter the username */
  private final TextField username = new TextField("",10);

  /** To enter the hostname */
  private final TextField hostname = new TextField("",10);

  /** To enter the port number */
  private final TextField portno   = new TextField("",5);

  /** A button to complete the login */
  private final Button okButton = new Button("Ok");
  
  /** A button to abort the login */
  private final Button cancelButton = new Button("Cancel");

  /** Whether the user successfully completed the <code>loginDialog</code> */
  private boolean isValid = false;

  /** Save a pointer to our parent for error dialogs */
  Frame our_parent;

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
  public loginDialog(Frame f)
  {
    super(f,"Connect",true);

    our_parent = f;
    GridBagLayout gbl = new GridBagLayout();
    GridBagConstraints gbc = new GridBagConstraints();

    gbc.weightx = 0;
    gbc.weighty = 0;
    gbc.fill= GridBagConstraints.NONE;
    setLayout(gbl);
    
    add(new Label("User Name:",Label.RIGHT),gbl,gbc,0,0,1,1);
    add(new Label("Server Name:",Label.RIGHT),gbl,gbc,0,1,1,1);
    add(new Label("Port Number:",Label.RIGHT),gbl,gbc,0,2,1,1);

    gbc.insets = (new Insets(0,0,0,5));
    
    add(username,gbl,gbc,1,0,1,1);
    add(hostname,gbl,gbc,1,1,1,1);
    add(portno,gbl,gbc,1,2,1,1);

    add(new Label("---",Label.CENTER),gbl,gbc,0,3,2,1);

    gbc.insets = (new Insets(5,0,5,0));
    
    add(okButton,gbl,gbc,0,5,1,1);
    add(cancelButton,gbl,gbc,1,5,1,1);
    okButton.addActionListener(this);
    cancelButton.addActionListener(this);   
    addWindowListener(this);
    pack();
  }
  public boolean getStatus()
  {
    return (isValid);
  }
  public String getUserName()
  {
    if (isValid) {
      return (username.getText());
    } else return null;
  }
  public String getHostName()
  {
    if (isValid) {
      return(hostname.getText());
    } else return null;
  }
  public int getPortNo()
  {
    if (isValid) {
      Integer test;
      try {
	test = Integer.valueOf(portno.getText());
	return(test);
      } catch (NumberFormatException exc) {
	return(-1);
      }
    } else return(-1);
  }
  private boolean Validate()     
  {
    String foo = portno.getText();
    ErrorDialog ed;
    
    try {
        Integer.valueOf(foo);
    } catch (NumberFormatException exc) {
      ed = new ErrorDialog(our_parent,"Bad Port",
			   "Incomprehensible port number entered");
      ed.setVisible(true);
      portno.setText("");
      return false;
    } 
    if (username.getText().equals("")) {
      //Do a warning about a bad username
      ed = new ErrorDialog(our_parent,"Bad Username","No username entered");
      ed.setVisible(true);
      return false;
    } else if (hostname.getText().equals("")) {
      //Do a warning about non-existant hostname
      ed = new ErrorDialog(our_parent,"Bad Hostname","No Server name entered");
      ed.setVisible(true);
      return false;
    } else {
      return true;
    }
  }
  private void end()
       //Function called to exit the Dialog
  {
    dispose();
  }
  //Below here is action handling code
  @Override
  public void actionPerformed(ActionEvent event) {
    Object src = event.getSource();
    
    if (src.equals(okButton)) {
      isValid = Validate();
      if (isValid) {
	end();
      }
    } else if (src.equals(cancelButton)) {
      end();
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
    end();
  }
}


