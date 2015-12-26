package me.nallar.jdds.internal.model;

import me.nallar.jdds.internal.ddsutil.PixelFormats;

import java.awt.image.*;
import java.io.*;


public abstract class AbstractTextureImage implements TextureImage {

	protected int height;
	protected int width;
	protected int pixelformat;
	protected File file = null;
	protected boolean hasMipMaps = false;
	protected int numMipMaps = 0;

	/**
	 * MipMap at the highest Level, ie the original
	 */
	protected MipMaps mipMaps = new MipMaps();

	/**
	 * Returns the associated {@link File}
	 *
	 * @return File
	 */
	public File getFile() {
		return this.file;
	}

	/**
	 * Width of the topmost MipMap
	 *
	 * @return
	 */
	public int getHeight() {
		return this.height;
	}

	/**
	 * Height of the topmost MipMap
	 *
	 * @return
	 */
	public int getWidth() {
		return this.width;
	}


	/**
	 * Get the Format in which pixel are stored in the file as internal stored Integer-value.
	 *
	 * @return in
	 */
	public int getPixelformat() {
		return this.pixelformat;
	}


	/**
	 * Returns whether or not the dds-file has MipMaps.
	 * Usually only textures whose size is a power of two may have mipmaps.
	 *
	 * @return boolean
	 */
	public boolean hasMipMaps() {
		return this.hasMipMaps;
	}

	/**
	 * Returns the number of MipMaps in this file.
	 *
	 * @return int Number of MipMaps
	 */
	public int getNumMipMaps() {
		return numMipMaps;
	}


	/**
	 * Returns true if the dds-file is compressed as DXT1-5
	 *
	 * @return boolean
	 */
	public boolean isCompressed() {
		return PixelFormats.isDXTCompressed(pixelformat);
	}

	/**
	 * Checks if a value is a power of two
	 *
	 * @param value
	 * @return
	 */
	public static boolean isPowerOfTwo(final int value) {
		double p = Math.floor(Math.log(value) / Math.log(2.0));
		double n = Math.pow(2.0, p);
		return (n == value);
	}
}
