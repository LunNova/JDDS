package me.nallar.jdds.internal.ddsutil;

import me.nallar.jdds.internal.model.MipMaps;
import me.nallar.jdds.internal.model.SingleTextureMap;
import me.nallar.jdds.internal.model.TextureMap;

import java.awt.image.*;

/**
 * @author danielsenff
 */
public class TextureFactory {

	/**
	 * @param generateMipMaps
	 * @param sourceImage
	 * @return
	 */
	public static TextureMap createTextureMap(final boolean generateMipMaps, final BufferedImage sourceImage) {
		TextureMap maps;
		if (generateMipMaps) {
			maps = new MipMaps();
			((MipMaps) maps).generateMipMaps(sourceImage);
		} else
			maps = new SingleTextureMap(sourceImage);

		return maps;
	}
}
