import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Launch {
	
	static String currentTextureSet;
	static BufferedImage body;
	static BufferedImage body90;
	static BufferedImage toolImage;
	
	static JTextArea textArea = new JTextArea();
	static JTextField input = new JTextField();
	static JButton button = new JButton("Generate images");
	
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setSize(500, 500);
		
		String textForArea = "Please put the file in a folder by this program, the folder shall be called res.";
		textForArea += System.getProperty("line.separator");
		textForArea += "Then type the filename:";
		textArea.setText(textForArea);
		textArea.setFont(new Font(textArea.getFont().getName(), Font.PLAIN, 15));
		input.setFont(new Font(textArea.getFont().getName(), Font.PLAIN, 35));
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				generateImages();
			}
		});
		
		frame.setLayout(new GridLayout(3, 1, 50, 50));
		frame.add(textArea);
		frame.add(input);
		frame.add(button);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	public static void generateImages() {
		currentTextureSet = input.getText();
		input.setText("");
		
		readImage();
		body90 = new BufferedImage(body.getWidth(), body.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2 = body90.createGraphics();
		AffineTransform at = AffineTransform.getRotateInstance(Math.PI/2, body.getWidth()/2, body.getHeight()/2);
		g2.transform(at);
		g2.drawImage(body, 0 ,0, null);
		g2.dispose();
		
		for(int i = 0; i < 32; i++) {
			generateImage(i);
		}
	}
	
	private static void generateImage(int timesPushed) {
		Graphics2D g2 = null;
		
		//BufferedImage bufferBody = body.getSubimage(0, 0, body.getWidth(), body.getHeight());
		//BufferedImage bufferBody90 = body90.getSubimage(0, 0, body.getWidth(), body.getHeight());
		BufferedImage bufferBody = new BufferedImage(body.getWidth(), body.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		BufferedImage bufferBody90 = new BufferedImage(body90.getWidth(), body90.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		
		if(timesPushed != 0) {
			g2 = bufferBody.createGraphics();
			g2.drawImage(body.getSubimage(0, 0, body.getWidth(), timesPushed), 0, body.getHeight() - timesPushed, null);
			g2.drawImage(body.getSubimage(0, timesPushed, body.getWidth(), body.getHeight() - timesPushed), 0, 0, null);
			g2.dispose();
			
			g2 = bufferBody90.createGraphics();
			g2.drawImage(body90.getSubimage(body.getWidth() - timesPushed, 0, timesPushed, body.getHeight()), 0, 0, null);
			g2.drawImage(body90.getSubimage(0, 0, body.getWidth() - timesPushed, body90.getHeight()), timesPushed, 0, null);
			g2.dispose();
		}
		
		else {
			bufferBody = body.getSubimage(0, 0, body.getWidth(), body.getHeight());
			bufferBody90 = body90.getSubimage(0, 0, body.getWidth(), body.getHeight());
		}
		
		toolImage = new BufferedImage(bufferBody.getWidth(), bufferBody.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		g2 = toolImage.createGraphics();
		for(int x = 0; x < bufferBody.getWidth(); x++) {
			for(int y = 0; y < bufferBody.getHeight(); y++) {
				if(x == y) {
					if(x == 0) continue;
					Color c1, c2, c3, c4;
					
					int sampleSize = 2;
					int r = 0;
					int g = 0;
					int b = 0;
					
					c1 = new Color(bufferBody.getRGB(x-1, y), true);
					c2 = new Color(bufferBody90.getRGB(x, y-1), true);
					
					r += c1.getRed();
					g += c1.getGreen();
					b += c1.getBlue();
					
					r += c2.getRed();
					g += c2.getGreen();
					b += c2.getBlue();
					
					if((x+1) != bufferBody.getWidth() || (y+1) != bufferBody.getHeight()) {
						c3 = new Color(bufferBody.getRGB(x, y+1), true);
						c4 = new Color(bufferBody90.getRGB(x+1, y), true);
						
						r += c3.getRed();
						g += c3.getGreen();
						b += c3.getBlue();
						
						r += c4.getRed();
						g += c4.getGreen();
						b += c4.getBlue();
						
						sampleSize += 2;
					}
					
					Color pixelColor = new Color(r/sampleSize, g/sampleSize, b/sampleSize);
					g2.setColor(pixelColor);
					g2.fillRect(x, y, 1, 1);
				}
				else if(y > x) {
					g2.drawImage(bufferBody.getSubimage(x, y, 1, 1), x, y, null);
				}
				else if(y < x) {
					g2.drawImage(bufferBody90.getSubimage(x, y, 1, 1), x, y, null);
				}
			}
		}
		
		g2.dispose();
		saveImageToFile(currentTextureSet + timesPushed, toolImage);
	}
	
	private static void saveImageToFile(String name, BufferedImage image) {
		try {
			File outputFile = new File(name + ".png");
			ImageIO.write(image, "png", outputFile);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void readImage() {
		String fileName = currentTextureSet;
		
		try {
			body = ImageIO.read(new File("res/" + fileName + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
