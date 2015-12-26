/**
 *
 */
package me.nallar.jdds.internal.ddsutil;

import java.awt.image.*;

/**
 * Interface for defining and encapsulating image scaling algorithms.
 *
 * @author danielsenff
 */
public abstract class Rescaler {

	/**
	 * @param image
	 * @param width
	 * @param height
	 * @return
	 */
	public abstract BufferedImage rescaleBI(BufferedImage image, int width, int height);


}
