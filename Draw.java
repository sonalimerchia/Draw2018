import java.util.ArrayList; 
import java.io.IOException;
import java.io.File;
import javax.imageio.ImageIO;

import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JFrame;
import javax.swing.ImageIcon;
import javax.swing.JButton; 
import javax.swing.JToggleButton; 
import javax.swing.JTextField; 
import javax.swing.ButtonGroup;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import java.awt.Color;
import java.awt.Dimension; 
import java.awt.Font;
import java.awt.Image;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;

/**
 * This class allows the user to draw using rectangles, ovals, lines,
 * freehand drawing, etc. It also allows them to save their pictures and
 * create custom colors
 * 
 * @author Sonali Merchia
 * @since October 5th, 2018
 */
public class Draw
{
	Options side; //This class handles users changing modes and colors
	Drawing top;  //This class is the area in which the user can draw
	Color use, background, custom;	//Current color, background color, and custom
	JFrame temp;	//The frame for the minor applications and major one
	Selector colorPicker;//This class handles custom colors
	BrushSize penChanger;//This class handles changing pen size
	
	/**
	 * This constructor initializes all the field variables except the 
	 * JFrame temp which is created in a local method later on.
	 */
	public Draw()
	{
		use = new Color( 0, 0, 0 );
		background = new Color( 0, 0, 0 );
		custom = new Color( 0, 0, 0 );
		
		side = new Options();
		top = new Drawing();
		colorPicker = new Selector();
		penChanger = new BrushSize();
	}
	/**
	 * This method sets up the program and window
	 */
	public void run()
	{
		//Create the JFrame
		JFrame large = new JFrame( "Drawing" );
		large.setLocation(800, 0);
		large.setSize( 800, 1000 );
		large.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		
		//Create the content panel
		JPanel content = new JPanel( new BorderLayout() );
		content.add( side, BorderLayout.EAST );
		content.add( top, BorderLayout.CENTER );
		
		//Make contents visible
		large.setContentPane( content );
		side.setBackground( Color.WHITE );
		large.setVisible( true );
	}
	public JPanel runInProgram( int height, int width )
	{
		JPanel content = new JPanel( new BorderLayout() );
		content.setSize( width, height );
		content.add( side, BorderLayout.EAST );
		content.add( top, BorderLayout.CENTER );
		
		//Make contents visible
		side.setBackground( Color.WHITE );
		return content; 
	}
	/**
	 * This class handles user choices on color changes and options
	 */
	 class Options extends JPanel implements ActionListener
	 {
		 JToggleButton[] colorButtons; //The buttons for color selection
		 JToggleButton[] optionsButtons;	//The buttons for options
		
		/**
		 * This constructor initializes the field variables and sets up
		 * the buttons
		 */
		public Options()
		{
			setPreferredSize( new Dimension(200, 800) );
			setLayout( new GridLayout( 12, 2, 2, 2 ) );
			setBackground( Color.WHITE );
			
			colorButtons = new JToggleButton[ 12 ];
			optionsButtons = new JToggleButton[ 12 ];
			JButton[] nonSelect = new JButton[ 4 ];
			
			Color[] colors = { custom, Color.RED, Color.ORANGE,
			Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE,
			Color.MAGENTA, Color.PINK, new Color(153, 102, 0), Color.WHITE,
			Color.BLACK };
			
			String[] labels = { "BACK", "FREEHAND", "OVAL", "RECT", 
				"TRIANGLE", "LINE", "ERASE", "LOAD", "CUSTOM", "BRUSHSIZE",
				"UNDO", "SAVE"	};
				
			ButtonGroup g1 = new ButtonGroup();
			ButtonGroup g2 = new ButtonGroup();
			
			String[] colorNames = { "COLOR", "RED", "ORANGE", "YELLOW", "GREEN", 
				"CYAN", "BLUE","MAGENTA","PINK", "BROWN", "WHITE", "BLACK" };
			for( int x = 0; x < colorButtons.length; x++ )
			{
				colorButtons[ x ] = new JToggleButton( colorNames[ x ] );
				colorButtons[ x ].addActionListener( this );
				colorButtons[ x ].setBackground( colors[ x ]);
				colorButtons[ x ].setForeground( colors[ x ]);
				g2.add( colorButtons[ x ] );
				
				optionsButtons[ x ] = new JToggleButton( labels [ x ] );
				optionsButtons[ x ].addActionListener( this );
				g1.add( optionsButtons[ x ] );
			}
			for( int x = 0; x < nonSelect.length; x++)
			{
				nonSelect[x] = new JButton( labels[ x+8 ] );
				nonSelect[x].addActionListener( this );
			}
			
			for( int x = 0; x < colorButtons.length ; x++)
			{
				if( x>7 ) add( nonSelect[ x-8 ] );
				else add( optionsButtons[ x ] );
				add( colorButtons[ x ] );
			}
		}
		public void actionPerformed( ActionEvent e) 
		{
			String c = e.getActionCommand();
			for( int x = 0; x < colorButtons.length; x++)
			{
				if( c.equals( colorButtons[x].getText() ) )
					if( optionsButtons[0].isSelected() )
					{
						top.back = null;
						background = new Color( colorButtons[x].getBackground().getRGB() );
					}
					else 
					use = new Color( colorButtons[x].getBackground().getRGB() ); 
					
				if( c.equals( optionsButtons[ x ].getText() ) )
					ittyBitty( c );
			}
		}
		public void ittyBitty( String cmd )
		{
			if( cmd.equals("CUSTOM") || cmd.equals("BRUSHSIZE") )
            {   temp = new JFrame("");
                temp.setSize(600, 550);
                temp.setLocation( getX()+getY() , getY() );
                
                if(cmd.equals("CUSTOM")) temp.setContentPane( colorPicker );   
                else if (cmd.equals("BRUSHSIZE")) temp.setContentPane( penChanger ); 
                
                temp.setVisible(true); 
            }
            switch( cmd )	{
            case "UNDO" : 
				if( !top.prior.isEmpty() )
				top.prior.remove( top.prior.size() - 1 );
				top.selected = "";
				break;
			case "SAVE":
				savePicture();
				top.selected = "";
				break;
			case "LOAD": loadImage(); 
			top.selected = "";
			break;
			default: top.changeEnum(cmd);
			}
			top.repaint();
		}
		public void loadImage()
		{
			Image image = new ImageIcon( "Drawing.png").getImage();
			top.back = image; 
		}
		public void savePicture()
		{
			BufferedImage img = new BufferedImage(top.getWidth(),
			top.getHeight(), BufferedImage.TYPE_INT_RGB);
            top.paint(img.getGraphics());
            try
            {ImageIO.write(img, "png", new File("Drawing.png"));}
            catch(IOException e){}
		}
	 }
	 class Drawing extends JPanel implements MouseListener, MouseMotionListener
	 {
		 int brushsize = 25;
		 ArrayList<Commands> prior = new ArrayList<Commands>(0);
         Commands current;
         String selected; 
         Image back; 
         
         public Drawing()
        {   setPreferredSize(new Dimension(800, 800));
            background = new Color(255, 255, 255);
            back = null;  
            use = new Color(0, 0, 0); 
            addMouseListener(this);
            addMouseMotionListener(this);
            selected = "FREEHAND";
        }
        public void paintComponent(Graphics g)
        {   super.paintComponent(g);
            setBackground(background);
            if( back != null )
            g.drawImage( back, 0, 0, getWidth(), getHeight(), null );
            
            for(int index=0; index<prior.size(); index++)
            if(prior.get(index)!= null) 
            prior.get(index).paint(g);
            
            g.setColor(use);      
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(brushsize));
            
            if(current!=null) current.paint(g); 
        }
        public void changeEnum( String c )
		{
			switch( c )
			{
				case "FREEHAND":
				case "OVAL":
				case "RECT":
				case "TRIANGLE":
				case "LINE":
				case "ERASE":
				selected = c;
				break;
			}
		}
        public void mouseClicked(MouseEvent e){}
        public void mousePressed(MouseEvent e)
        {   
            switch( selected )
            {
				case "FREEHAND" : 
				current = new Commands(new Point(e.getX()-brushsize/2, 
							e.getY()-brushsize/2, brushsize, use));
				break;
				
				case "OVAL": 
				current = new Commands(new Oval(e.getX(), e.getY(), use));
				break;
				
				case "RECT":
				current = new Commands(new MyRectangle(e.getX(), e.getY(), use)); 
				break;
				
				case "TRIANGLE" : 
				current = new Commands(new Triangle(e.getX(), e.getY(), use));
				break;
				
				case "LINE":
				current = new Commands(new Line(e.getX(), e.getY(), brushsize, use));
				break;
				
				case "ERASE": 
				current = new Commands(new Eraser(e.getX()-brushsize/2, 
						  e.getY()-brushsize/2, brushsize));
			}  
            repaint(); 
        }
        public void mouseReleased(MouseEvent e)
        {   
			prior.add(current);
            current = null;
            repaint(); 
        }
        public void mouseEntered(MouseEvent e){}
        public void mouseExited(MouseEvent e){}
        public void mouseMoved(MouseEvent e){}
        public void mouseDragged(MouseEvent e)
        {   
			if(side.optionsButtons[1].isSelected() || side.optionsButtons[6].isSelected())
			current.add(e.getX()-brushsize/2, e.getY()-brushsize/2);
			
            else if(current != null) current.change(e.getX(), e.getY());
            repaint();
        }
    }
    class Commands
    {   
		MyRectangle rect; 
        
        public Commands(){}
        public Commands(MyRectangle getRect)
        {   rect = getRect;  }
        
        public Commands(Oval getOval)
        {   rect = getOval; }
        
        public Commands(Triangle getTri)
        {   rect = getTri;  }
        
        public Commands(Point getPoint)
        {   rect = getPoint;  }
        
        public Commands(Eraser getPoint)
        {   rect = getPoint;	}
        
        public Commands(Line getLine)
        {   rect = getLine;	}
        
        public void add(int x, int y)
        {   
			if(rect.current<rect.xCoor.length)
            {   rect.xCoor[rect.current] = x;
                rect.yCoor[rect.current] = y;
                rect.current++;
            }
            else
            {   top.prior.add(top.current);
                top.current = null; 
                top.repaint();
                if(side.optionsButtons[1].isSelected())
                top.current = new Commands(new Point(x, y, top.brushsize, use));
                else
                top.current = new Commands(new Eraser(x, y, top.brushsize)); 
            }
        }
        public void change(int x, int y)
        {   rect.width= x-(int)rect.getX();
            rect.height = y-(int)rect.getY(); 
        }
        public void paint(Graphics g)
        {  rect.draw(g); }
    }
    class BrushSize extends JPanel implements ChangeListener, ActionListener
    {   int value = top.brushsize; 
        JSlider slider; 
        JTextField type; 
        JButton button; 
        public BrushSize()
        {   setLayout(new BorderLayout());
            
            slider = new JSlider(JSlider.HORIZONTAL, 0, 200, top.brushsize);
            slider.setMajorTickSpacing(25);
            slider.setMinorTickSpacing(5); 
            slider.setPaintTicks(true);
            slider.setPaintLabels(true);
            slider.setPreferredSize(new Dimension(400, 50)); 
            slider.addChangeListener(this);
            
            type = new JTextField(3);
            type.setFont(new Font("Arial", Font.BOLD, 25));
            type.addActionListener(this);
            type.setHorizontalAlignment(JTextField.CENTER);  
            type.setText(""+value);
            
            button = new JButton("USE BRUSHSTROKE");
            button.setPreferredSize(new Dimension(400, 50)); 
            button.addActionListener(this);
            
            JPanel pan = new JPanel(new GridLayout(1, 2));
            
            pan.add(type); pan.add(button); 
            add(pan, BorderLayout.NORTH); 
            add(slider, BorderLayout.SOUTH); 
        }
        public void paintComponent(Graphics g)
        {   super.paintComponent(g);
            g.setColor(use); 
            g.fillOval(300-value/2, 250-value/2, value, value);
        }
        public void stateChanged(ChangeEvent e)
        {   value = slider.getValue();
            type.setText(""+value); 
            repaint(); 
        }
        public void actionPerformed(ActionEvent e)
        {   
            if(e.getActionCommand().equals("USE BRUSHSTROKE"))
            {   top.brushsize = value; 
                temp.dispose(); }
            else
            {   try
                {   value = Integer.parseInt(type.getText());
                    slider.setValue(value);    }
                catch(NumberFormatException x){}
            }
        }
    }
    class Selector extends JPanel implements MouseListener, MouseMotionListener,
ActionListener
    {   Color picked, withDark; 
        JButton[] prev = new JButton[10]; 
        int coorX, coorY, dX, where; 
        JButton addit; 
        JTextField type; 
        
        public Selector()
        {   setLayout(null); 
            addMouseListener(this);
            addMouseMotionListener(this);
            
            type = new JTextField(6);
            type.setFont(new Font("Arial", Font.BOLD, 25));
            type.addActionListener(this);
            type.setHorizontalAlignment(JTextField.CENTER); 
            type.setBounds(240, 20, 110, 55); 
            add(type);
            
            addit = new JButton("USE COLOR");
            addit.addActionListener(this); 
            addit.setBounds(455, 20, 110, 55);
            add(addit); 
            
            picked = new Color(255, 255, 255); 
            withDark = new Color(255, 255, 255); 
            prev = new JButton[10];
            for(int index=0; index<prev.length; index++)
            {   prev[index] = new JButton("");
                for(int index1=0; index1<index; index1++)
                prev[index].setText(prev[index].getText()+" ");
                prev[index].addActionListener(this); 
                prev[index].setVisible(false);
                prev[index].setBorderPainted(false); 
                prev[index].setBounds(25+index*55, 90, 50, 40); 
                add(prev[index]); 
            }
            coorX = 25; 
            coorY = 415; 
            dX = 565; 
        }
        public void paintComponent(Graphics g)
        {   
			super.paintComponent(g);
            g.setColor(Color.WHITE);
            g.fillRect(25, 150, 540, 265);
            
            g.setColor(withDark);
            g.fillRect(25, 20, 110, 55);
            
            double inc = 255.0/90;
            for(int bris = 0; bris<255; bris++)
            {   for(int num = 0; num<=90; num++)
                {   g.setColor(new Color(255,
					(int)(num*inc)+(int)((255-num*inc)/255*bris), bris)); 
                    g.drawLine(num+25, 150+bris, num+25, 415);
                    
                    g.setColor(new
					Color(255-(int)(num*inc)+(int)((num*inc)/255*bris),255, bris));
                    g.drawLine(num+115, 150+bris, num+115, 415);
                    
                    g.setColor(new Color(bris, 255,
					(int)(num*inc)+(int)((255-num*inc)/255*bris)));
                    g.drawLine(num+205, 150+bris, num+205, 415);
                    
                    g.setColor(new Color(bris,
					255-(int)(num*inc)+(int)((num*inc)/255*bris), 255));
                    g.drawLine(num+295, 150+bris, num+295, 415);
                    
                    g.setColor(new Color((int)(num*inc)+(int)((255-num*inc)/255*bris), 
                    bris, 255));
                    g.drawLine(num+385, 150+bris, num+385, 415);
                    
                    g.setColor(new Color(255, bris,
					255-(int)(num*inc)+(int)((num*inc)/255*bris)));
                    g.drawLine(num+475, 150+bris, num+475, 415);
                }  
            }
            g.setColor(picked);
            g.fillRect(25, 450, 540, 25);
            double[]incres = {picked.getRed()/540.0, picked.getGreen()/540.0,
			picked.getBlue()/540.0};
            
            for(int num=0; num<=540; num++)
            {   g.setColor(new Color((int)(incres[0]*num), (int)(incres[1]*num),
				(int)(incres[2]*num)));
                g.drawLine(num+25, 450, num+25, 475);   
            }
            g.setColor(Color.BLACK); 
            g.drawOval(coorX-2, coorY-2, 4, 4);
            g.drawRect(dX-1, 448, 2, 29);   
        }
        public void saveColor()
        {
            boolean run = true; 
            for(int index=prev.length-1; index>0; index--)
            {
                prev[index].setBackground(new
Color(prev[index-1].getBackground().getRGB())); 
                prev[index].setForeground(new
Color(prev[index-1].getForeground().getRGB())); 
            }
            prev[0].setBackground(withDark); 
            prev[0].setForeground(withDark); 
            if(where<prev.length)
            prev[where].setVisible(true); 
            where++; 
        }
        public void mouseClicked(MouseEvent e){}
        public void actionPerformed(ActionEvent e)
        {   
            String cmd = e.getActionCommand(); 
            if(e.getSource()==type && type.getText().length()==6)
             colorFinder(type.getText().toLowerCase());
            else if(cmd.equals("USE COLOR"))
            {   custom = new Color(withDark.getRGB()); 
                side.colorButtons[0].setBackground(custom);
                side.colorButtons[0].setForeground(custom);
                if(side.colorButtons[0].isSelected()) use = new Color(withDark.getRGB());
                saveColor();
                temp.remove(this); 
                temp.dispose();
            }
            else
            {   Color col = new Color(prev[cmd.length()].getBackground().getRGB());
                numsToColor(col.getRed(),col.getGreen(), col.getBlue()); 
            }
            repaint(); 
        }
        public void colorFinder(String code)
        {   int red1, blue1, green1;
            red1=blue1=green1=255; 
            try
            {   red1 = Integer.parseInt(code.substring(0, 2), 16);
                green1 = Integer.parseInt(code.substring(2, 4), 16);
                blue1 = Integer.parseInt(code.substring(4, 6), 16);  }
            catch(NumberFormatException e){}
            numsToColor(red1,green1, blue1);
        }
        public void numsToColor(int red, int green, int blue)
        {
            withDark = new Color(red, green, blue);
            
            int max = Math.max(red, Math.max(green, blue)); 
            double inc = 255.0/540.0;
            dX = (int)((max + 25*inc)/inc);
            
            int[]nums = {form(red), form(green), form(blue)};
            for(int index=0; index<3; index++)
            if(nums[index]>255)nums[index]=255; 
            else if(nums[index]<0) nums[index]=0; 
            
            if(nums[0]==nums[1] && nums[1]==nums[2])
            picked = new Color(255, 255, 255); 
            else picked = new Color(nums[0], nums[1], nums[2]);
            
            red = picked.getRed(); 
            green = picked.getGreen(); 
            blue = picked.getBlue(); 
            
            int min = Math.min(red, Math.min(green, blue)); 
            int div = 0;
            int pick = 0; 
            inc = 255.0/90;
            coorY=150+min;
           
            if(red>=254)
                {if(green>blue) {div=0; pick=green;}
                else if(green<=blue) {div=5; pick=blue;}}
            else if(green>=254)
                {if(red>blue) {div=1; pick=red;}
                else if(red<=blue) {div=2; pick=blue;}}
            else if(blue>=254)
                {if(green>red) {div=3; pick=green;}
                else if(green<=red) {div=4; pick=red;}}
            
            if(div%2==0) pick= (int)((pick-min)/(inc-inc*min/255.0));
            else pick = (int)((pick-255.0)/(inc*min/255.0 - inc));
            
            String[]comps = {Integer.toString(withDark.getRed(), 16), 
                             Integer.toString(withDark.getGreen(), 16), 
                             Integer.toString(withDark.getBlue(), 16)};
            type.setText("");
            for(int index=0; index<3; index++)
            {   if(comps[index].length()<2) 
                    if(comps[index].length()==0) comps[index]="00";
                    else comps[index]="0"+comps[index];
                type.setText(type.getText()+comps[index]);
            }
            
            coorX = 25+(div*90)+pick;
        }
        public int form(int x)
        {
            x= (int)((x/(dX-25.0))*540.0);
            return x;
        }
        public void mousePressed(MouseEvent e)
        {   int x = e.getX();
            int y = e.getY();
            
            if(x>=25 && x<=565 && y>=150 && y<=415)
            {   coorX = x; coorY = y; 
                pickedColor(x-25, y-150);   }
            else if(x>=25 && x<=565 && y>=450 && y<475)
            {   dX = x; 
                pickedColor(x); }
            repaint(); 
        }
        public void pickedColor(int x, int y)
        {   int div = x/90;
            double inc = 255.0/90; 
            int edit = 0; 
            if(div%2==0) edit = (int)(x%90*inc)+(int)((255.0-x%90*inc)/255.0*y); 
            else edit = 255-(int)(x%90*inc)+(int)((x%90*inc)/255.0*y); 
            
            try
            {   if(div==0) picked = new Color(255, edit, y);
                else if(div==1) picked = new Color(edit, 255, y);
                else if(div==2) picked = new Color(y, 255, edit);
                else if(div==3) picked = new Color(y, edit, 255);
                else if(div==4) picked = new Color(edit, y, 255);
                else if(div==5) picked = new Color(255, y, edit);   }
            catch(IllegalArgumentException e)
            { picked = new Color(255, 255, 255); }
            pickedColor(dX); 
        }
        public void pickedColor(int dark)
        {   dark-=25;
            double[] incres = {picked.getRed()/540.0, picked.getGreen()/540.0,
picked.getBlue()/540.0};
            withDark = new Color((int)(incres[0]*dark), (int)(incres[1]*dark),
(int)(incres[2]*dark));
            
            String[]nums = {Integer.toString(withDark.getRed(), 16),
Integer.toString(withDark.getGreen(), 16),
Integer.toString(withDark.getBlue(), 16)};
            for(int index=0; index<3; index++)
            if(nums[index].length()<2)
            if(nums[index].length()==1)nums[index]="0"+nums[index];
            else nums[index]="00";
            type.setText(nums[0]+nums[1]+nums[2]);
            type.setText(type.getText().toLowerCase()); 
        }
        public void mouseReleased(MouseEvent e){}
        public void mouseEntered(MouseEvent e){}
        public void mouseExited(MouseEvent e){}
        public void mouseMoved(MouseEvent e){}
        public void mouseDragged(MouseEvent e)
        {   int x = e.getX();
            int y = e.getY();
            
            if(x>=24 && x<=565 && y>=150 && y<=415)
            {   coorX = x; coorY=y; 
                pickedColor(x-25, y-150);   }
            else if(x>=25 && x<=566 && y>=450 && y<475)
            {   dX = x; 
                pickedColor(x); }
            repaint(); 
        }
    }
    class MyRectangle extends Rectangle
    { Color color; 
      int current; 
      int[] xCoor; int[]yCoor; 
      public MyRectangle()
      {super(); }
      public MyRectangle(int x2, int y2, Color getColor)
      { super(x2, y2, 1, 1); 
        color = getColor;   }
      public MyRectangle(int x1, int y1, int width1, int height1)
      { super(x1,y1,width1,height1);    }
      public MyRectangle(int x1, int y1, int width1, int height1, Color getColor)
      { super(x1,y1,width1,height1);
        color = getColor;   }
      public void draw(Graphics g)
      { g.setColor(color);
        g.fillRect((int)getX(), (int)getY(), (int)getWidth(), (int)getHeight());    }
		  
    }
    class Oval extends MyRectangle
    { public Oval ()
      {super(); }
      public Oval(int x2, int y2, Color getColor)
      { super(x2, y2, 1, 1); 
        color = getColor;   }
      public void draw(Graphics g)
      { g.setColor(color);
        g.fillOval((int)getX(), (int)getY(), (int)getWidth(), (int)getHeight());    }
    }
    class Triangle extends MyRectangle
    { public Triangle()
      {super(); }
      public Triangle(int x2, int y2, Color getColor)
      { super(x2, y2, 1, 1); 
        color = getColor;   }
      public void draw(Graphics g)
      { g.setColor(color);
        int[]arr1 = {(int)getX(), (int)(getX()+getWidth()/2),
(int)(getX()+getWidth())};
        int[]arr2 = {(int)(getY()+getHeight()), (int)getY(),
(int)(getY()+getHeight())};
        g.fillPolygon(arr1, arr2, 3);    
      }
    }
    class Point extends MyRectangle
    { int brush; 
      public Point()
      {super(); }
      public Point(int x2, int y2, int brush1, Color getColor)
      { super(x2-brush1/2, y2-brush1/2, brush1, brush1); 
        brush = brush1; 
        xCoor = new int[50]; 
        yCoor = new int[50]; 
        xCoor[0] = x2; yCoor[0]=y2; 
        current = 1; 
        color = getColor; }
      public Point(int x2, int y2, int brush1)
      { super(x2-brush1/2, y2-brush1/2, brush1, brush1); 
        brush = brush1; 
        xCoor = new int[50]; 
        yCoor = new int[50]; 
        xCoor[0] = x2; yCoor[0]=y2; 
        current = 1; 
        color = background; }
      public void draw(Graphics g)
      { g.setColor(color);
        for(int index=0; index<current; index++)
        g.fillOval(xCoor[index], yCoor[index], brush, brush);   }
    }
    class Eraser extends Point
    { public Eraser()
      {super(); }
      public Eraser(int x2, int y2, int brush1)
      { super(x2, y2, brush1);  }
      public void draw(Graphics g)
      { g.setColor(background);
        for(int index=0; index<current; index++)
        g.fillOval(xCoor[index], yCoor[index], brush, brush);   }
    }
    class Line extends MyRectangle
    { int x1, y1, x2, y2, brush; 
      public Line()
      {super(); }
      public Line(int x2, int y2, int brush1, Color getColor)
      { super(x2, y2, brush1, brush1); 
        brush = brush1; 
        x1=x2; y1=y2;
        color = getColor;   }
      public void draw(Graphics g)
      { g.setColor(color);
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(brush));
        g.drawLine((int)getX(), (int)getY(), (int)(getWidth()+getX()),
(int)(getHeight()+getY()));   }

	}
    
	/**
	 * This method creates an instance of the class which runs the whole
	 * program. 
	 * 
	 * @param args 		The arguments added after the file name when
	 * 					the program is run 
	 */
	public static void main(String[] args) 
	{
		Draw obj = new Draw();
		obj.run();
	}
}
