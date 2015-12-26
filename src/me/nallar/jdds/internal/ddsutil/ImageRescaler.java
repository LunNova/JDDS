/**
 *
 */
package me.nallar.jdds.internal.ddsutil;

import java.awt.*;
import java.awt.image.*;

/**
 * Java Graphics2D Rescaler
 *
 * @author danielsenff
 */
public class ImageRescaler extends Rescaler {

	/**
	 * Graphics2D Scale algorithm
	 */
	private final int scaleAlgorithm;

	/**
	 *
	 */
	public ImageRescaler() {
		scaleAlgorithm = Image.SCALE_SMOOTH;
	}

	/**
	 * @return
	 */
	@Override
	public BufferedImage rescaleBI(final BufferedImage originalImage,
								   final int newWidth, final int newHeight) {

		Image rescaledImage = originalImage.getScaledInstance(newWidth, newHeight, scaleAlgorithm);
		BufferedImage bi;
		if (rescaledImage instanceof BufferedImage)
			bi = (BufferedImage) rescaledImage;
		else
			bi = BIUtil.convertImageToBufferedImage(rescaledImage, BufferedImage.TYPE_4BYTE_ABGR);

		return bi;
	}

}
