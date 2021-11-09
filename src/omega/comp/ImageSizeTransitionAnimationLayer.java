package omega.comp;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Image;

import java.awt.image.BufferedImage;

import java.util.LinkedList;
public abstract class ImageSizeTransitionAnimationLayer implements AnimationLayer{
	
	public LinkedList<BufferedImage> images = new LinkedList<>();
	public BufferedImage image;
	public Color color;

	public volatile boolean useAddOn = false;

	public ImageSizeTransitionAnimationLayer setUseAddOn(boolean value){
		this.useAddOn = value;
		return this;
	}

	public synchronized void prepareImages(TextComp comp, int distance, boolean useClear){
		Color tempColor = comp.color2;
		if(tempColor == color && image == comp.image)
			return;

		if(!useAddOn)
			images.clear();
		
		color = tempColor;
		image = comp.image;
		
		int sizeW = comp.w;
		int sizeH = comp.h;
		boolean positive = distance >= 0;
		int factor = !positive ? +1 : -1;
		while((positive ? (distance >= 0) : (distance < 0))){
			BufferedImage image = new BufferedImage(comp.getWidth(), comp.getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D)image.getGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			if(useClear){
				g.setColor(color);
				g.fillRoundRect(0, 0, comp.getWidth(), comp.getHeight(), comp.arcX, comp.arcY);
				comp.paintArc(g);
			}
			g.drawImage(comp.image.getScaledInstance(sizeW, sizeH, Image.SCALE_SMOOTH), comp.getWidth()/2 - sizeW/2, comp.getHeight()/2 - sizeH/2, sizeW, sizeH, null);
			g.dispose();
			images.add(image);
			
			sizeW += -factor;
			sizeH += -factor;
			distance += factor;
		}
	}
	
	@Override
	public void animate(TextComp comp){
	}
	
}
