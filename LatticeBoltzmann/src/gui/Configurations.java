package gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Configurations provides static methods for obtaining and saving user
 * preferences as well as modification time and image groupings at closing of
 * the ILB (Image List Browser)
 * 
 * @author bonifantmc
 * 
 */
public class Configurations {
	/** default starting width of an ILB */
	public static final int dw = 700;

	/** default starting height of an ILB */
	public static final int dh = 700;

	/** default starting x coord of an ILB */
	public static final int dx = 0;

	/** default starting y coord of an ILB */
	public static final int dy = 0;

	/** default position of scroll bar for ImageHandler */
	public static final int dtick = 0;

	/** default size of thumbnails for ImageHandler */
	public static final int dsize = 150;

	/** default list file absolute path for ImageHandler */
	public static final String dl = null;

	/** default directory absolute path for ImageHandler */
	public static final String dr = null;

	/**
	 * Loads the initial location and size of the ImageListBrowser and sets
	 * them.
	 * 
	 * @param iLB
	 *            the browser whose fields will be set
	 * @param fileName
	 *            name of the configuration file
	 */
	public static void getDisplayProperties(LBMGui gui, String fileName) {
		Properties prop = new Properties();
		File f = new File(fileName);
		// defaults
		int w = dw, h = dh, x = dx, y = dy;

		if (f.exists()) {
			try (FileInputStream input = new FileInputStream(f)) {
				prop.load(input);
				try {
					w = Integer
							.parseInt(prop.getProperty("windowwidth", "700"));
				} catch (NumberFormatException e) {
					w = dw;
				}

				try {
					h = Integer.parseInt(prop
							.getProperty("windowheight", "700"));
				} catch (NumberFormatException e) {
					h = dh;
				}

				try {
					x = Double.valueOf((prop.getProperty("positionX", "0")))
							.intValue();
				} catch (NumberFormatException e) {
					x = dx;
				}

				try {
					y = Double.valueOf((prop.getProperty("positionY", "0")))
							.intValue();

				} catch (NumberFormatException e) {
					y = dy;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// check for negative values, someone could hardcode it into the file,
		// and if a window is closed while minimized (at least in windows) the
		// position is then given as a negative
		if (w < 0)
			w = dw;
		if (h < 0)
			h = dh;
		if (x < 0)
			x = dx;
		if (y < 0)
			y = dy;

	}

	

	

	/**
	 * Store user configurations to a configuration file for use on next start
	 * up of ILB
	 * 
	 * @param iLB
	 *            the ILB to store information for
	 * @param h
	 *            the Image Handler used by this ILB
	 * @param fileName
	 *            the name of the config file to save to
	 */
	static void setProperties(LBMGui gui, String fileName) {
		Properties prop = new Properties();
		try (FileOutputStream os = new FileOutputStream(fileName)) {
			prop.store(os, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}