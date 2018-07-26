import java.awt.Polygon;
import java.util.Random;

public class Genome {
	
	private static long GENE_COUNT;
	
	Polygon p;
	int r, g, b, a;
	long id;

	
	public Genome(int width, int height) {
		Random rand = new Random();
		
		int[] x = new int[Setting.VERTEX];
		int[] y = new int[Setting.VERTEX];
		
		for(int i = 0; i < Setting.VERTEX; i++) {
			x[i] = rand.nextInt(width + 20) - 10;
			y[i] = rand.nextInt(height + 20) - 10;
		}
		
		this.p = new Polygon(x, y, Setting.VERTEX);
		this.r = rand.nextInt(256);
		this.g = rand.nextInt(256);
		this.b = rand.nextInt(256);
		this.a = rand.nextInt(256);
		
		this.id = GENE_COUNT++;
		//HIS_GENES.put(geneStr, id);
	}
	
	public void softMutation(int width, int height) {
		Random rand = new Random();
		int d = rand.nextInt(6);
		if(d == 0) {
			int newr;
			do { newr = r + rand.nextInt(21) - 10; } while(newr < 0 || newr >= 256);
			this.r = newr;
		} else if(d == 1) {
			int newg;
			do { newg = g + rand.nextInt(21) - 10; } while(newg < 0 || newg >= 256);
			this.g = newg;
		} else if(d == 2) {
			int newb;
			do { newb = b + rand.nextInt(21) - 10; } while(newb < 0 || newb >= 256);
			this.b = newb;
		} else if(d == 3) {
			int newa;
			do { newa = a + rand.nextInt(21) - 10; } while(newa < 0 || newa >= 256);
			this.a = newa;
		} else if(d == 4){
			int i = rand.nextInt(p.npoints);
			int newx;
			do { newx = p.xpoints[i] + rand.nextInt(21) - 10; } while(newx < -10 || newx >= width + 10);
			p.xpoints[i] = newx;
		} else {
			int i = rand.nextInt(p.npoints);
			int newy;
			do { newy = p.ypoints[i] + rand.nextInt(21) - 10; } while(newy < -10 || newy >= height + 10);
			p.ypoints[i] = newy;
		}
		
		this.id = GENE_COUNT++;
		
	}
	
	public void mediumMutation(int width, int height) {
		Random rand = new Random();
		int d = rand.nextInt(6);
		if(d == 0) r = rand.nextInt(256);
		else if(d == 1) g = rand.nextInt(256);
		else if(d == 2) b = rand.nextInt(256);
		else if(d == 3) a = rand.nextInt(256);
		else if(d == 4) {
			int i = rand.nextInt(p.npoints);
			p.xpoints[i] = rand.nextInt(width + 20) - 10;
		} else {
			int i = rand.nextInt(p.npoints);
			p.ypoints[i] = rand.nextInt(height + 20) - 10;
		}
		this.id = GENE_COUNT++;
	}
	
	public void hardMutation(int width, int height) {
		Random rand = new Random();
		r = rand.nextInt(256);
		g = rand.nextInt(256);
		b = rand.nextInt(256);
		a = rand.nextInt(256);
		
		int i = rand.nextInt(p.npoints);
		p.xpoints[i] = rand.nextInt(width + 20) - 10;
		p.ypoints[i] = rand.nextInt(height + 20) - 10;
		this.id = GENE_COUNT++;
	}
	
	public Genome(Genome g) {
		this.p = new Polygon(g.p.xpoints.clone(), g.p.ypoints.clone(), g.p.npoints);
		this.r = g.r;
		this.g = g.g;
		this.b = g.b;
		this.a = g.a;
		this.id = g.id;
	}
}
