import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HillClimbing implements Comparable<HillClimbing> {
	
	public int worstDifference;
	
	public List<Genome> dna;
	public int difference;
	
	private byte[] target;
	
	public BufferedImage tmp2;
	private BufferedImage tmp;
	private int width, height;
	
	public long count;
	
	public HillClimbing(BufferedImage image) {
		width = image.getWidth();
		height = image.getHeight();
		
		dna = new ArrayList<>();
		dna.add(new Genome(width, height));
		difference = Integer.MAX_VALUE;
		
		target = ((DataBufferByte)image.getRaster().getDataBuffer()).getData();
		tmp = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
		tmp2 = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
		
		worstDifference = 0;
		for(int i = 0; i < target.length; i++)
			worstDifference += Math.max(Math.abs(target[i] & 0xff), Math.abs(~target[i] & 0xff));
		
		count = 0;
	}
	
	public int diff() {
		Graphics2D g = tmp.createGraphics();
		draw(g);
		g.dispose();
		
		byte[] src = ((DataBufferByte)tmp.getRaster().getDataBuffer()).getData();
		
		int difference = 0;
		for(int i = 0; i < src.length; i ++) {
			difference += Math.abs((src[i] & 0xff) - (target[i] & 0xff));
		}
		
		return difference;
	}
	
	public void draw(Graphics2D g) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);
		for(Genome gen : dna) {
			g.setColor(new Color(gen.r, gen.g, gen.b, gen.a));
			g.fillPolygon(gen.p);
		}
	}
	
	public void step(int k) {
		while(k-->0) {
		
			List<Genome> copy = new ArrayList<>();
			for(int i = 0; i < dna.size(); i++)
				copy.add(new Genome(dna.get(i)));
		
			Random rand = new Random();
			if(dna.size() < Setting.NUM_POLYGON) {
				dna.add(new Genome(width, height));
			} else {
				double d = Math.random();
				int i = rand.nextInt(dna.size());
				if(d < 0.1) {
					if(d < 0.01) {
						dna.get(i).hardMutation(width, height);
					} else {
						dna.get(i).mediumMutation(width, height);
					}
				} else {
					dna.get(i).softMutation(width, height);
				}
			}
		
			int newDiff = diff();
			if(newDiff > difference) {
				dna = copy;
			} else {
				difference = newDiff;
				
				Graphics2D g = tmp2.createGraphics();
				g.drawImage(tmp, 0, 0, null);
				g.dispose();
			}
			
			count++;
		}
	}
	
	public double fitness() {
		return 1.0 - (double)difference / (double)worstDifference;
	}
	
	public int compareTo(HillClimbing x) {
		return Double.compare(fitness(), x.fitness());
	}
}
