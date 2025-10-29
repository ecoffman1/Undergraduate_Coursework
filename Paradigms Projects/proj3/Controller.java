import java.awt.event.MouseListener;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.swing.JList;

import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

class Controller implements ActionListener, MouseListener, KeyListener
{
	View view;
	Model model;
	boolean keyLeft;
	boolean keyRight;
	boolean keyUp;
	boolean keyDown;

	Controller(Model m, View v)
	{
		this.model = m;
		this.view = v;
		this.view.addMouseListener(this);
		this.view.load.addActionListener(this);
		this.view.save.addActionListener(this);
	}

	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == this.view.save)
			{	
				System.out.print("hey");	
				try 
				{
					FileWriter writer = new FileWriter("map.json");
					Json map = this.model.marshal();
					map.add("ScrollX", this.view.scrollX);
					map.add("ScrollY", this.view.scrollY);
					writer.write(map.toString());
					writer.close();
				}

				catch (IOException exception) 
				{
					exception.printStackTrace();
					System.exit(1);
				}
			}
			
		if(e.getSource() == this.view.load)
		{
			Json map = Json.load("map.json");
			this.view.scrollX = (int)map.getLong("ScrollX");
			this.view.scrollY = (int)map.getLong("ScrollY");
			map = map.get("items");
			this.model.unmarshal(map);
		}
	}

	public void mousePressed(MouseEvent e)
	{
		if (e.getButton() == 1){
			this.model.setDestination(e.getX(), e.getY());
			if (e.getX() < 200 && e.getY() < 200) {
				this.model.change_item();
				return;
			}else{
				this.model.addMapItem(e.getX() + view.scrollX, e.getY() + view.scrollY);
			}
		}
		if (e.getButton() == 3){
			if(this.model.items.size() > 0)
			{
			int closest = 0;
			int x = e.getX() - this.view.scrollX;
			int y = e.getY() - this.view.scrollY;
			for(int i = 1;i < model.items.size();i++){
				if(this.model.items.get(i).distance(x, y) < this.model.items.get(closest).distance(x,y)){
					closest = i;
				}
			}
			this.model.items.remove(closest);
			}
		}
	}

	public void mouseReleased(MouseEvent e) 
	{	}
	
	public void mouseEntered(MouseEvent e) 
	{	}
	
	public void mouseExited(MouseEvent e) 
	{	}
	
	public void mouseClicked(MouseEvent e) 
	{	}
	
	public void keyPressed(KeyEvent e)
	{
		switch(e.getKeyCode())
		{
			case KeyEvent.VK_RIGHT: 
				this.keyRight = true; 
				break;
			case KeyEvent.VK_LEFT: 
				this.keyLeft = true; 
				break;
			case KeyEvent.VK_UP: 
				this.keyUp = true; 
				break;
			case KeyEvent.VK_DOWN: 
				this.keyDown = true; 
				break;
		}
	}

	public void keyReleased(KeyEvent e)
	{
		switch(e.getKeyCode())
		{
			case KeyEvent.VK_RIGHT: 
				this.keyRight = false; 
				break;
			case KeyEvent.VK_LEFT: 
				this.keyLeft = false; 
				break;
			case KeyEvent.VK_UP: 
				this.keyUp = false; 
				break;
			case KeyEvent.VK_DOWN: 
				this.keyDown = false; 
				break;
			case KeyEvent.VK_ESCAPE:
				System.exit(0);
		}
		char c = Character.toLowerCase(e.getKeyChar());
		if(c == 'q')
			System.exit(0);
        if(c == 'r')
            this.model.reset();
	}

	public void keyTyped(KeyEvent e)
	{	}

	void update()
	{
		int speed = 10;
		if(this.keyRight) 
            this.view.scrollX += speed;
		if(this.keyLeft) 
			this.view.scrollX -= speed;
		if(this.keyDown) 
			this.view.scrollY += speed;
		if(this.keyUp)
			this.view.scrollY -= speed;
	}
}
