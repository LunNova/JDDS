/**
 *
 */
package me.nallar.jdds.internal.ddsutil;

import java.awt.*;


/**
 * Some helper methods for MipMap generation
 *
 * @author danielsenff
 */
public class MipMapsUtil {

	/**
	 * Number of MipMaps that will be generated from this image sizes.
	 *
	 * @param width
	 * @param height
	 * @return
	 */
	public static int calculateMaxNumberOfMipMaps(final int width, final int height) {
		return ((int) Math.floor(Math.log(Math.max(width, height)) / Math.log(2.0))) + 1; // plus original
	}

}
