import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class Display extends JPanel implements Runnable {
	
	boolean save;
	String filename;
	
	Thread thread;
	boolean keep_going;
	BufferedImage image;
	
	/****single image *****
	HillClimbing hc;
	****single image *****/
	//GeneticAlgorithm GA;
	
	HillClimbing[][] subHC;
	BufferedImage[][] subImages;
	int width, height;
	
	public Display(BufferedImage source, String filename) throws Exception {
		this.filename = filename.substring(0, filename.indexOf('.'));
		
		width = (int)Math.ceil(source.getWidth() / 100.0);
		height = (int)Math.ceil(source.getHeight() / 100.0);
		
		subHC = new HillClimbing[height][width];
		subImages = new BufferedImage[height][width];
		
		for(int i = 0; i < height; i++) for(int j = 0; j < width; j++) {
			int w = 100, h = 100;
			if(i == height - 1) h = source.getHeight() - i * 100;
			if(j == width - 1) w = source.getWidth() - j * 100;
			
			BufferedImage img = source.getSubimage(j * 100, i * 100, w, h);
			subImages[i][j] = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
			Graphics2D g = subImages[i][j].createGraphics();
			g.drawImage(img, 0, 0, null);
			g.dispose();
			
			subHC[i][j] = new HillClimbing(subImages[i][j]);
		}
		
		save = false;
		
		/****single image *****
		hc = new HillClimbing(source);
		****single image *****/
		
		//GA = new GeneticAlgorithm(source);
		//GA.initialize();
		setPreferredSize(new Dimension(source.getWidth(), source.getHeight()));
	}
	
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		super.paintComponent(g2);
		
		if(image != null) {
			g2.drawImage(image, 0, 0, getWidth(), getHeight(), this);
		}
	}
	
	public void render(double fitness) {
		if(getWidth() == 0 || getHeight() == 0) return;
		
		BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		
		/****single image *****
		Graphics2D g2 = image.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		hc.draw(g2);
		****single image *****/
		
		Graphics2D g2 = image.createGraphics();
		for(int i = 0; i < height; i++) for(int j = 0; j < width; j++) {
			g2.drawImage(subHC[i][j].tmp2, j * 100, i * 100, subHC[i][j].tmp2.getWidth(), subHC[i][j].tmp2.getHeight(), null);
		}
		
		if(save) {
			try {
				File output = new File(String.format("%s_fitness_%.2f", filename, fitness * 100) + ".jpg");
				ImageIO.write(image, "jpg", output);
				
				System.out.println(output.getName() + " is saved!");
				save = false;
			} catch(IOException e) { e.printStackTrace(); }
		}
		
		g2.setColor(Color.black);
		g2.fillRect(0, 0, 50, 15);
		g2.setColor(Color.white);
		g2.drawString(String.format("%.2f%%", fitness * 100), 5, 12);
		
		
		
		//GA.best.draw(g2);
		this.image = image;
		repaint();
	}
	
	public void run() {
		keep_going = true;
		
		try { Thread.sleep(2000); }
		catch(InterruptedException e) {}
		
		int it = 0;
		while(keep_going) {
			
			/****single image *****
			System.out.println("fitness: " + (1 - (double)hc.difference / (double)hc.worstDifference));
			****single image *****/
			
			//System.out.println("generation: " + GA.generation + ": " + GA.best.fitness);
			//render();
			
			//for(int i = 0; i < 10; i++)
			
			/****single image *****
			hc.step(100);
			****single image *****/
			
			//GA.nextGeneration();
			
			/****single image *****
			it++;
			if(it == 100) {
				save(hc.count);
				it = 0;
			}
			****single image *****/
			
				
			
			List<HillClimbing> hcs = new ArrayList<>();
			for(int i = 0; i < height; i++) for(int j = 0; j < width; j++) {
				subHC[i][j].step(10);
				hcs.add(subHC[i][j]);
			}
			
			Collections.sort(hcs);
			for(int i = 0; i < 5 && i < hcs.size(); i++) {
				hcs.get(i).step(20);
			}
			
			render(hcs.get(0).fitness());
			//System.out.println(hcs.get(0).fitness());
			
			try { Thread.sleep(1); }
			catch(InterruptedException e) {}
		}
	}
	
	/****single image *****
	public void save(long i) {
		try {
			BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_3BYTE_BGR);
			
			Graphics2D g2 = image.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			hc.draw(g2);
			g2.dispose();
			
			File output = new File(i + ".jpg");
			ImageIO.write(image, "jpg", output);
		} catch(IOException e) {
			
		}
	}
	
	****single image *****/
	
	public void start() {

	    thread = new Thread(this);
	    thread.start();
	}


	public void stop() {

	    if(thread == null) return;
	    keep_going = false;
	    try { thread.join(); }
	    catch(InterruptedException e) {}

	}
}
