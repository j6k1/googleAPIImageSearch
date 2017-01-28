package net.will_co21.application.googleAPIImageSearch;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class ImageWindow extends JFrame {

	/**
	 *
	 */
	private static final long serialVersionUID = -1544051844072547236L;

	public ImageWindow(String title)
	{
		super(title);

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e){
				dispose();
			}
		});
	}

	public void displayImage(File imagePath, int w, int h)
	{
		JLabel img = new JLabel(new ImageIcon(imagePath.getAbsolutePath()));

		this.setBounds(0, 0, w, h + 30);

		this.add(img);
		this.setVisible(true);
	}
}
