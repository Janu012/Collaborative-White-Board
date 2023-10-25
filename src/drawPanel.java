import java.awt.*;
import java.awt.event.*;

class drawPanel extends Frame implements WindowListener, MouseMotionListener, 
	    ItemListener, MouseListener, ActionListener
{

/** A pointer to our network connection */
      /** A pointer to our parent */
  private final whiteBoard our_parent;

  /** The canvas we will be drawing on */
  private final whiteCanvas c;

  /** The previous point recorded */
  private Point lastPoint = null;

  /** whether we are allowed to draw to the whieBoard */
  private boolean canDraw = false;

  /** A choice of available colors */
  private final Choice colors;

  /** A choice of tools */
  private final Choice tools;

  /** A choice of available thicknesses */
  private final Choice sizes;

  /** The current color we are drawing in */
  private Color curColor = Color.black;

  private final Button clearButton;

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
  public drawPanel(whiteBoard p, int x, int y)
  {
    our_parent = p;
    c = new whiteCanvas();

    GridBagLayout gbl = new GridBagLayout();
    GridBagConstraints gbc = new GridBagConstraints();

    gbc.weightx = 0;
    gbc.weighty = 0;
    gbc.insets  = new Insets(2,2,2,2);
    gbc.fill    = GridBagConstraints.BOTH;
    setLayout(gbl);
    setLayout(gbl);
    c.setSize(x,y);
    c.addMouseMotionListener(this);
    c.addMouseListener(this);
    add(c,gbl,gbc,0,0,4,3);

    add(new Label("Color", Label.CENTER),gbl,gbc,0,3,1,1);
    add(new Label("Tool", Label.CENTER),gbl,gbc,1,3,1,1);
    add(new Label("Size",Label.CENTER),gbl,gbc,2,3,1,1);
    
    clearButton = new Button("Clear");
    clearButton.addActionListener(this);
    colors = new Choice();
    
    colors.add("Black");
    colors.add("Blue");
    colors.add("Red");
    colors.add("Green");
    colors.add("Erase");
    colors.addItemListener(this);

    tools = new Choice();

    tools.add("Line");
    tools.add("Rectangle");
    tools.add("Circle");

    sizes = new Choice();

    sizes.add("1");
    sizes.add("4");
    sizes.add("8");
    sizes.add("12");
    sizes.add("Solid");
    sizes.addItemListener(this);

    add(colors,gbl,gbc,0,4,1,1);
    add(tools,gbl,gbc,1,4,1,1);
    add(sizes,gbl,gbc,2,4,1,1);
    gbc.fill    = GridBagConstraints.NONE;
    add(clearButton,gbl,gbc,3,3,1,2);
    clearButton.setEnabled(false);
    pack();
  }
  void clear()
  {
    c.clear();
    our_parent.socket.send("all","clear","");
  }
  public void erase()
  {
    c.clear();
  }
  public void setDrawable(boolean x)
  {
    canDraw = x;
    clearButton.setEnabled(x);
  }
  private void doQuit()
  {
    our_parent.doQuit();
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

  int last_x = -1;
  int last_y = -1;
  int last_w = -1;
  int last_h = -1;

  @Override
  public void mouseDragged(MouseEvent e)
  {
    if ((canDraw) && (lastPoint != null)) {
      if (tools.getSelectedItem().equals("Line")) { //Start drawing now
	c.drawLine(lastPoint.x, lastPoint.y, e.getPoint().x, e.getPoint().y);

	our_parent.socket.send("all","line", lastPoint.x + "," + lastPoint.y 
			       + "," + e.getPoint().x + "," +
			       e.getPoint().y + "," + c.getThickness() + "," + 
			       curColor.getRed() + "," +
			       curColor.getGreen() + "," +
			       curColor.getBlue());
	
	lastPoint = e.getPoint();

       
      } else { //Else it's a shape, draw a temporary bounding rect
	Graphics g = c.getGraphics();
	int x, y, w, h;

	g.setXORMode(Color.orange);

	if (last_x != -1) {
	  g.drawRect(last_x,last_y,last_w,last_h);
	}

	w = lastPoint.x - e.getPoint().x;
	h = lastPoint.y - e.getPoint().y;
	if (w<0) { w *= -1; }
	if (h<0) { h *= -1; }

	if (lastPoint.x < e.getPoint().x) {
	  x = lastPoint.x;
	} else {
	  x = e.getPoint().x;
	}

	if (lastPoint.y > e.getPoint().y) {
	  y = e.getPoint().y;
	} else {
	  y = lastPoint.y;
	}

	g.drawRect(x,y,w,h);
	last_x = x;
	last_y = y;
	last_w = w;
	last_h = h;
      }
    }
  }

  public void drawLine(int x1, int y1, int x2, int y2, int thickness, Color x)
  {
    int tmp;
    c.setColor(x);
    tmp = c.getThickness();
    c.setThickness(thickness);
    c.drawLine(x1,y1,x2,y2);
    c.setColor(curColor);
    c.setThickness(tmp);
  }

  public void drawRect(int x1, int y1, int w, int h, int thickness, Color x)
  {
    int tmp;
    c.setColor(x);
    tmp = c.getThickness();
    c.setThickness(thickness);
    c.drawRect(x1,y1,w,h);
    c.setColor(curColor);
    c.setThickness(tmp);
  }

  public void drawCircle(int x1, int y1, int w, int h, int thickness, Color x)
  {
    int tmp;
    c.setColor(x);
    tmp = c.getThickness();
    c.setThickness(thickness);
    c.drawOval(x1,y1,w,h);
    c.setColor(curColor);
    c.setThickness(tmp);
  }


  @Override
  public void mouseMoved(MouseEvent e){
  }

  @Override
  public void mouseClicked(MouseEvent e){
  }

  @Override
  public void mousePressed(MouseEvent e) {
    if (canDraw) {
      lastPoint = e.getPoint();
    }
  }

  @Override
  public void mouseReleased(MouseEvent e)
  {
    Graphics g = getGraphics();

    if (last_x != -1) {
      g.setColor(Color.white);
      g.drawRect(last_x,last_y,last_w,last_h);
    }

    if (tools.getSelectedItem().equals("Rectangle")) {
      c.drawRect(last_x, last_y, last_w, last_h);
      our_parent.socket.send("all","rectangle", last_x + "," + last_y + "," + 
			     last_w + "," + last_h + "," + 
			     c.getThickness() + "," + 
			     curColor.getRed() + "," +
			     curColor.getGreen() + "," +
			     curColor.getBlue());

    } else if (tools.getSelectedItem().equals("Circle")) {
      c.drawOval(last_x,last_y,last_w,last_h);
      our_parent.socket.send("all","circle", last_x + "," + last_y + "," + 
			     last_w + "," + last_h + "," + 
			     c.getThickness() + "," + 
			     curColor.getRed() + "," +
			     curColor.getGreen() + "," +
			     curColor.getBlue());
    }

    lastPoint = null; //Clear last point
  }

  @Override
  public void mouseEntered(MouseEvent e){
  }

  @Override
  public void mouseExited(MouseEvent e){
  }

  @Override
  public void itemStateChanged(ItemEvent e)
  {
    Object src = e.getSource();
    String s;
    
    if (src == colors) { //Set a new color
      s = colors.getSelectedItem();
        switch (s) {
            case "Black" -> {
                c.setColor(Color.black);
                curColor = Color.black;
            }
            case "Blue" -> {
                c.setColor(Color.blue);
                curColor = Color.blue;
            }
            case "Green" -> {
                c.setColor(Color.green);
                curColor = Color.green;
            }
            case "Red" -> {
                c.setColor(Color.red);
                curColor = Color.red;
            }
            case "Erase" -> {
                c.setColor(Color.white);
                curColor = Color.white;
            }
            default -> {
            }
        }
    } else if (src == sizes) { //Set a new Size
      s = sizes.getSelectedItem();
        switch (s) {
            case "1" -> c.setThickness(1);
            case "4" -> c.setThickness(2);
            case "8" -> c.setThickness(5);
            case "12" -> c.setThickness(10);
            case "Solid" -> c.setThickness(-1);
            default -> {
            }
        }
    }
  }

  @Override
  public void actionPerformed(ActionEvent event){
    Object src = event.getSource();

    if (src==clearButton) {
      clear();
    }
  }
}

