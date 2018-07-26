import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Main extends JFrame {
	
	public static int width, height;
	public static double factor;
	
	public Main(BufferedImage image) throws Exception {
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		
		JLabel label = new JLabel();
		
		label.setIcon(new ImageIcon(image));
		
		
		this.setContentPane(label);
		this.pack();
		this.setVisible(true);
	}

	public static void main(String[] args) throws Exception {		
		
		String filename = "cjc.jpg";
		
		BufferedImage image = ImageIO.read(new File(filename));
		
		if(image.getWidth() * image.getHeight() > Setting.MAX_PIXELS) {
			factor = Math.sqrt(Setting.MAX_PIXELS / (double)(image.getWidth() * image.getHeight()));
			image = ImageProcess.scale(image, factor);
		}
		
		width = image.getWidth();
		height = image.getHeight();
		
		new Main(image);
		System.out.println(image.getType());
		
		//Genetic.gaussianKernel(0.84089642);
		//BufferedImage image = ImageIO.read(new File("eiffel1.jpg"));
		
		//image = ImageProcess.scale(image, 0.25);
		//image = ImageProcess.toGrayScale(image);
		//image = ImageProcess.gaussianSmooth(image, 1);
		//image = ImageProcess.edgeDetector(image, 100);
		//image = ImageProcess.reduceNoice(image, 70);
		//image = ImageProcess.edgeEnchance(image, 1);
		
//		Setting.WIDTH = image.getWidth();
//		Setting.HEIGHT = image.getHeight();
		Display component = new Display(image, filename);
		component.start();
		
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
		
		frame.getContentPane().add(component);
		frame.pack();
		frame.setVisible(true);

		
		Scanner in = new Scanner(System.in);
		String str;
		while(!(str = in.nextLine()).equals("exit")) {
			if(str.equals("save"))
				component.save = true;
		}
		
		System.exit(0);
	}
	

}
