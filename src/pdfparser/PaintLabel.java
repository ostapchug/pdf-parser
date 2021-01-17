package pdfparser;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JLabel;

class PaintLabel extends JLabel {
	
	ArrayList<Shape> shapes = new ArrayList<>();
    Point startDrag, endDrag;
    
    PaintLabel() {
    	    	
    	this.addMouseListener(new MouseAdapter() {
    		
    		@Override
            public void mousePressed(MouseEvent e) {
                if(e.getButton()==1){
                    startDrag = new Point(e.getX(), e.getY());
                    endDrag = startDrag;
                    repaint();
                }
                requestFocusInWindow();
            }
            
    		@Override
            public void mouseReleased(MouseEvent e) {
                if(e.getButton()==1){
                    if(startDrag.x!=e.getX()){
                        Shape r = makeRectangle(startDrag.x, startDrag.y, e.getX(), e.getY());
                        shapes.add(r);
                    }
                    startDrag = null;
                    endDrag = null;
                    repaint();
                }
            }
    	});
    	
    	this.addMouseMotionListener(new MouseMotionAdapter() {
    		
    		@Override
            public void mouseDragged(MouseEvent e) {
                endDrag = new Point(e.getX(), e.getY());
                repaint();
            }            
        });
    	
    	this.addKeyListener(new KeyAdapter() {
    		
    		@Override
            public void keyPressed(KeyEvent e) {
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_Z) {
                    if(!shapes.isEmpty()){
                        shapes.remove(shapes.size()-1);
                        repaint();
                    }
                }                
            }
        });	
    }
    
    @Override
    public void paintComponent(Graphics g){
    	super.paintComponent(g);
    	
    	Graphics2D g2 = (Graphics2D) g;
    	
    	g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color[] colors = { Color.YELLOW, Color.MAGENTA, Color.CYAN , Color.RED, Color.BLUE, Color.PINK};
        int colorIndex = 0;
        
        g2.setStroke(new BasicStroke(2));
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.50f));
        
        for (Shape s : shapes) {
            g2.setPaint(Color.BLACK);
            g2.draw(s);
            g2.setPaint(colors[(colorIndex++) % 6]);
            g2.fill(s);
        }
        
        if (startDrag != null && endDrag != null) {
            g2.setPaint(Color.LIGHT_GRAY);
            Shape r = makeRectangle(startDrag.x, startDrag.y, endDrag.x, endDrag.y);
            g2.draw(r);
        }
    }
    
    private Rectangle2D.Float makeRectangle(int x1, int y1, int x2, int y2) {
        return new Rectangle2D.Float(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1 - x2), Math.abs(y1 - y2));
    }
    
    public ArrayList<Shape> getShapes (){
        return shapes;
    }
}
