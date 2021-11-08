package omega.comp;
import java.util.LinkedList;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Image;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
public class Animations {
	public static final String ANIMATION_STATE = "Animation Running";
	
	public static final int ACTION_MOUSE_ENTERED = 0;
	public static final int ACTION_MOUSE_EXITED = 1;
	public static final int ACTION_MOUSE_PRESSED = 2;
	public static final int ACTION_MOUSE_CLICKED = 3;
	public static final int ACTION_MOUSE_DOUBLE_CLICKED = 4;

	public static final LinkedList<TextComp> comps = new LinkedList<>();
	
	public static void prepareTextComp(TextComp comp){
		comp.map.put(ANIMATION_STATE, false);
	}

	public static void putComp(TextComp comp, AnimationLayer layer){
		comp.setAnimationLayer(layer);
		comps.add(comp);
	}

	public synchronized static void animateAll(long delay){
		new Thread(()->{
			try{
				Thread.sleep(delay);
			}
			catch(Exception e){
				e.printStackTrace();
			}
			comps.forEach((cx)->{
				cx.triggerAnimation();
			});
		}).start();
	}
	
	public static void putAnimationLayer(TextComp comp, AnimationLayer layer, int action){
		if(!isActionApplicable(action))
			return;
		prepareTextComp(comp);
		if(action == ACTION_MOUSE_ENTERED){
			comp.addMouseListener(new MouseAdapter(){
				@Override
				public void mouseEntered(MouseEvent e){
					layer.animate(comp);
				}
			});
		}
		else if(action == ACTION_MOUSE_EXITED){
			comp.addMouseListener(new MouseAdapter(){
				@Override
				public void mouseExited(MouseEvent e){
					layer.animate(comp);
				}
			});
		}
		else if(action == ACTION_MOUSE_PRESSED){
			comp.addMouseListener(new MouseAdapter(){
				@Override
				public void mousePressed(MouseEvent e){
					layer.animate(comp);
				}
			});
		}
		else if(action == ACTION_MOUSE_CLICKED){
			comp.addMouseListener(new MouseAdapter(){
				@Override
				public void mouseExited(MouseEvent e){
					if(e.getClickCount() == 1)
						layer.animate(comp);
				}
			});
		}
		else if(action == ACTION_MOUSE_DOUBLE_CLICKED){
			comp.addMouseListener(new MouseAdapter(){
				@Override
				public void mouseExited(MouseEvent e){
					if(e.getClickCount() == 2)
						layer.animate(comp);
				}
			});
		}
	}
	
	public static AnimationLayer getLineAnimationLayer(int rate) {
		return (comp)->{
			boolean animationRunning = (boolean)comp.getValue(ANIMATION_STATE);
			if(animationRunning)
				return;
			new Thread(()->{
				Graphics2D g = (Graphics2D)comp.getGraphics();
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				
				comp.map.put(ANIMATION_STATE, true);
				
				int length = 1;
				
				while(comp.isDrawingImage() ? (length < comp.w) : (length < comp.textWidth)){
					g.setColor(comp.color3);
					
					if(comp.isDrawingImage())
						g.fillRect(comp.getWidth()/2 - length/2, comp.getHeight()/2 + comp.h/2, length++, 3);
					else
						g.fillRect(comp.alignX != -1 ? comp.textX : (comp.getWidth()/2 - length/2), comp.textY + 3, length++, 3);
					
					try{
						Thread.sleep(rate);
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
				
				comp.map.put(ANIMATION_STATE, false);
				g.dispose();

				if(!comp.enter)
					comp.repaint();
			}).start();
		};
	}

	public static AnimationLayer getImageBoxAnimationLayer(int rate){
		return (comp)->{
			boolean animationRunning = (boolean)comp.getValue(ANIMATION_STATE);
			if(animationRunning || !comp.isDrawingImage())
				return;
			new Thread(()->{
				Graphics2D g = (Graphics2D)comp.getGraphics();
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				
				comp.map.put(ANIMATION_STATE, true);
				
				int lengthW = 1;
				int lengthH = 1;
				boolean canPaint = true;
				int width = comp.w;
				int height = comp.h;
				
				g.setColor(comp.color3);
				
				while(canPaint){
					if(comp.isDrawingImage()){
						//Drawing Horizontal Layer
						if(lengthW <= width){
							g.fillRect(comp.getWidth()/2 - lengthW/2, comp.getHeight()/2 + comp.h/2, lengthW, 3);
							g.fillRect(comp.getWidth()/2 - lengthW/2, comp.getHeight()/2 - comp.h/2, lengthW, 3);
						}
						if(lengthH <= height){
							g.fillRect(comp.getWidth()/2 - comp.w/2, comp.getHeight()/2 - lengthH/2, 3, lengthH);
							g.fillRect(comp.getWidth()/2 + comp.w/2, comp.getHeight()/2 - lengthH/2, 3, lengthH);
						}
					}
					
					if(lengthW > width && lengthH > height){
						g.fillRect(comp.getWidth()/2 - lengthW/2, comp.getHeight()/2 + comp.h/2, lengthW + 2, 3);
						canPaint = false;
						comp.map.put(ANIMATION_STATE, false);
						break;
					}
					
					lengthH++;
					lengthW++;
					
					try{
						Thread.sleep(rate);
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
				
				g.dispose();
				
				if(!comp.enter)
					comp.repaint();
			}).start();
		};
	}
	
	public static AnimationLayer getImageSizeAnimationLayer(int rate, int distance, boolean useClear){
		return (comp)->{
			boolean animationRunning = (boolean)comp.getValue(ANIMATION_STATE);
			if(animationRunning || !comp.isDrawingImage())
				return;
			new Thread(()->{
				Graphics2D g = (Graphics2D)comp.getGraphics();
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				
				comp.map.put(ANIMATION_STATE, true);
				
				boolean canPaint = true;
				boolean positive = distance >= 0;
				int factor = positive ? +1 : -1;
				int width = comp.w;
				int height = comp.h;
				int maxWidth = comp.getWidth();
				int maxHeight = comp.getHeight();
				int currentDistance = distance;
				
				g.setColor(comp.color2);
				
				while(canPaint && (positive ? (currentDistance-- >= 0) : (currentDistance++ < 0)) && (width <= maxWidth && height <= maxHeight)){
					if(useClear)
						g.fillRect(0, 0, comp.getWidth(), comp.getHeight());
					g.drawImage(comp.image.getScaledInstance(width, height, Image.SCALE_SMOOTH), comp.getWidth()/2 - width/2, comp.getHeight()/2 - height/2, width, height, null);
					width += factor;
					height += factor;
					
					try{
						Thread.sleep(rate);
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
				
				g.dispose();
				
				comp.map.put(ANIMATION_STATE, false);
				
				if(!comp.enter)
					comp.repaint();
			}).start();
		};
	}
	
	public static boolean isActionApplicable(int action){
		return action >= ACTION_MOUSE_ENTERED && action <= ACTION_MOUSE_DOUBLE_CLICKED;
	}
}
