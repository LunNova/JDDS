/**
 *
 */
package me.nallar.jdds.internal.ddsutil;

import java.awt.*;
import java.awt.image.*;


/**
 * @author danielsenff
 */
public class BIUtil {

	private BIUtil() {
	}

	/**
	 * Get an {@link BufferedImage} from an {@link Image}-Object
	 *
	 * @param image
	 * @param type
	 * @return
	 */
	public static BufferedImage convertImageToBufferedImage(final Image image, final int type) {
		BufferedImage result = new BufferedImage(
				image.getWidth(null), image.getHeight(null), type);
		Graphics g = result.createGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();
		return result;
	}

}
