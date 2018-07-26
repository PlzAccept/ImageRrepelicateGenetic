import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ImageProcess {
	
	public static BufferedImage toGrayScale(BufferedImage color) {
		BufferedImage grayImage = new BufferedImage(color.getWidth(), color.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		Graphics2D g = grayImage.createGraphics();
		g.drawImage(color, 0, 0, null);
		g.dispose();
		return grayImage;
	}
	
	public static BufferedImage threshHold(BufferedImage image, int t, boolean contract) {
		BufferedImage after = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		byte[] source = ((DataBufferByte)image.getRaster().getDataBuffer()).getData();
		byte[] target = ((DataBufferByte)after.getRaster().getDataBuffer()).getData();
		
		for(int i = 0; i < source.length; i++) {
			if((source[i] & 0xff) > t && !contract) target[i] = -1;
			if((source[i] & 0xff) <= t && contract) target[i] = -1;
		}
		
		return after;
	}
	
	public static double[][] gaussianKernel(double sigma) {
		int len = (int)Math.round(sigma * 3) * 2 + 1;
		double[][] kernel = new double[len][len];
		int ci = len/2, cj = len/2;
		
		for(int i = 0; i < len; i++) for(int j = 0; j < len; j++) {
			double dx = Math.abs(i - ci), dy = Math.abs(j - cj);
			kernel[i][j] = (1 / (2 * Math.PI * sigma * sigma)) * Math.pow(Math.E, -((dx*dx + dy*dy) / (2 * sigma*sigma)));
		}
		
//		double sum = 0;
//		for(int i = 0; i < len; i++) {
//			for(int j =  0; j < len; j++) {
//				sum += kernel[i][j];
//				System.out.print(kernel[i][j] + " ");
//			}
//			System.out.println();
//		}
//		System.out.println(sum);
//		
		return kernel;
	}
	
	public static BufferedImage gaussianSmooth(BufferedImage image, double sigma) {
		BufferedImage blurred = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		byte[] source = ((DataBufferByte)image.getRaster().getDataBuffer()).getData();
		byte[] target = ((DataBufferByte)blurred.getRaster().getDataBuffer()).getData();
		
		System.arraycopy(source, 0, target, 0, source.length);
		
		int width = image.getWidth();
		int height = image.getHeight();
		
		double[][] kernel = gaussianKernel(sigma);
		int klen = kernel.length;
				
//		for(int i = 0; i + klen < height; i++) for(int j = 0; j + klen < width; j++) {
//			double sum = 0;
//			for(int k = 0; k < klen; k++) for(int l = 0; l < klen; l++) {
//				sum += kernel[k][l] * (double)(((int)source[(i + k) * width + (j + l)]) & 0xff);
//			}
//			target[(i + klen / 2) * width + (j + klen / 2)] = (byte)(int)sum;
//		}
		
		for(int i = 0; i < height; i++) for(int j = 0; j < width; j++) {
			double sum = 0;
			for(int k = 0; k < klen; k++) for(int l = 0; l < klen; l++) {
				int m = i - klen / 2 + k;
				int n = j - klen / 2 + l;
				if(m >= 0 && m < height && n >= 0 && n < width)
					sum += kernel[k][l] * (double)(((int)source[m * width + n]) & 0xff);
			}
			target[i * width + j] = (byte)(int)sum;
		}
		
		return blurred;
	}
	
	public static BufferedImage gaussianSmoothRGB(BufferedImage image, double sigma) {
		BufferedImage blurred = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		byte[] source = ((DataBufferByte)image.getRaster().getDataBuffer()).getData();
		byte[] target = ((DataBufferByte)blurred.getRaster().getDataBuffer()).getData();
		
		
		int width = image.getWidth();
		int height = image.getHeight();
		
		double[][] kernel = gaussianKernel(sigma);
		int klen = kernel.length;
		
		for(int i = 0; i < height; i++) for(int j = 0; j < width; j++) {
			for(int c = 0; c < 3; c++) {
				double sum = 0;
				for(int k = 0; k < klen; k++) for(int l = 0; l < klen; l++) {
					int m = i - klen / 2 + k;
					int n = 3 * (j - klen / 2 + l);
					if(m >= 0 && m < height && n >= 0 && n < 3 * width)
						sum += kernel[k][l] * (double)(((int)source[m * 3 * width + n + c]) & 0xff);
				}
				target[i * 3 * width + 3 * j + c] = (byte)(int)sum;
			}
		}
		
		return blurred;
	}
	
	public static BufferedImage edgeDetector(BufferedImage image, int threshold) {
		BufferedImage edge = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		byte[] source = ((DataBufferByte)image.getRaster().getDataBuffer()).getData();
		byte[] target = ((DataBufferByte)edge.getRaster().getDataBuffer()).getData();
		
		int width = image.getWidth();
		int height = image.getHeight();
		
		int[][] x_kernel = { {-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1} };
		int[][] y_kernel = { {-1, -2, -1}, {0, 0, 0}, {1, 2, 1} };
		
		int[] G = new int[source.length];
		int max = 0;
		for(int i = 0; i + 2 < height; i++) for(int j = 0; j + 2 < width; j++) {
			int gx = 0, gy = 0;
			for(int k = 0; k < 3; k++) for(int l = 0; l < 3; l++) {
				gx += x_kernel[k][l] * (source[(i + k) * width + (j + l)] & 0xff);
				gy += y_kernel[k][l] * (source[(i + k) * width + (j + l)] & 0xff);
			}
			G[(i + 1) * width + (j + 1)] = (int)Math.sqrt(gx*gx + gy*gy);
			max = Math.max(max, G[(i + 1) * width + (j + 1)]);
		}
		
		double factor = 255.0 / max;
		for(int i = 0; i < target.length; i++) {
			//target[i] = (byte)(int)(factor * G[i]);
			target[i] = (byte)(G[i] > threshold ? 0 : -1);
		}
		
		// clear border
		for(int i = 0; i < width; i++) {
			for(int j = 0; j < 3; j++) {
				target[j * width + i] = -1;
				target[(height - j - 1) * width + i] = -1;
			}
		}
		
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < 3; j++) {
				target[i * width + j] = -1;
				target[i * width + (width - j - 1)] = -1;
			}
		}
		
		return edge;
	}
	
	public static BufferedImage scale(BufferedImage image, double factor) {
		int width = (int)(image.getWidth() * factor);
		int height = (int)(image.getHeight() * factor);
		
		Image tmp = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		BufferedImage newImg = new BufferedImage(width, height, image.getType());
		
		Graphics2D g = newImg.createGraphics();
		g.drawImage(tmp, 0, 0, null);
		g.dispose();
		
		return newImg;
		
	}
	
	public static BufferedImage edgeEnchance(BufferedImage image, int factor) {
		BufferedImage edge = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		byte[] source = ((DataBufferByte)image.getRaster().getDataBuffer()).getData();
		byte[] target = ((DataBufferByte)edge.getRaster().getDataBuffer()).getData();
		
		int width = image.getWidth();
		int height = image.getHeight();
		
		Arrays.fill(target, (byte)-1);
		for(int i = 0; i < height; i++) for(int j = 0; j < width; j++) {
			if(source[i * width + j] != 0) continue;
			for(int ii = i - factor; ii <= i + factor; ii++) for(int jj = j - factor; jj <= j + factor; jj++) {
				if(ii >= 0 && ii < height && jj >= 0 && jj < width) {
					target[ii * width + jj] = 0;
				}
			}
		}
		
		return edge;
	}
	
	public static BufferedImage reduceNoice(BufferedImage image, int threshHold) {
		BufferedImage edge = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		byte[] source = ((DataBufferByte)image.getRaster().getDataBuffer()).getData();
		byte[] target = ((DataBufferByte)edge.getRaster().getDataBuffer()).getData();
		
		int width = image.getWidth();
		int height = image.getHeight();
		
		boolean[] visited = new boolean[source.length];
		List<Integer> bubble_pixels = new ArrayList<>();
		
		Arrays.fill(target, (byte)-1);
		
		for(int i = 0; i < height; i++) for(int j = 0; j < width; j++) {
			if(!visited[i * width + j] && source[i * width + j] == 0) {
				bubble_pixels.clear();
				bfs(i, j, width, height, source, visited, bubble_pixels);
				
				if(bubble_pixels.size() > threshHold) {
					for(int k : bubble_pixels)
						target[k] = 0;
				}
			}
		}
		
		return edge;
	}
	
	
	private static void bfs(int i, int j, int width, int height, byte[] src, boolean[] visited, List<Integer> bubble) {
		Queue<Point> q = new LinkedList<>();
		q.add(new Point(i, j));
		visited[i * width + j] = true;
		while(!q.isEmpty()) {
			Point p = q.poll();
						
			bubble.add(p.x * width + p.y);
			
			for(int k = p.x - 1; k <= p.x + 1; k++) for(int l = p.y - 1; l <= p.y + 1; l++) {
				if(k < 0 || k >= height || l < 0 || l >= width) continue;
				if(src[k * width + l] != 0) continue;
				if(visited[k * width + l]) continue;
				visited[k * width + l] = true;
				
				q.add(new Point(k, l));
			}
		}
	}
	
	
}
