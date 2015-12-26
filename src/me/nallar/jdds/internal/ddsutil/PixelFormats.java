package me.nallar.jdds.internal.ddsutil;

import gr.zdimensions.jsquish.Squish;
import me.nallar.jdds.internal.jogl.DDSImage;
import me.nallar.jdds.internal.model.TextureImage.PixelFormat;

import javax.activation.UnsupportedDataTypeException;

/**
 * Collects pixelformat conversions and interfaces.
 * This is WIP and not used yet, as it is the prelude to refactoring.
 *
 * @author danielsenff
 */
public class PixelFormats {

	/**
	 * @param pixelFormat DDSImage pixelformat
	 * @return
	 * @throws UnsupportedDataTypeException
	 */
	public static Squish.CompressionType getSquishCompressionFormat(final int pixelFormat)
			throws UnsupportedDataTypeException {
		switch (pixelFormat) {
			case DDSImage.D3DFMT_DXT1:
				return Squish.CompressionType.DXT1;
			case DDSImage.D3DFMT_DXT3:
				return Squish.CompressionType.DXT3;
			case DDSImage.D3DFMT_DXT5:
				return Squish.CompressionType.DXT5;
			default:
				throw new UnsupportedDataTypeException("given pixel format not supported me.nallar.jdds.internal.compression format");
		}
	}

	/**
	 * Returns the verbose Pixelformat this DDSFile for the pixelformat-code
	 *
	 * @param pixelformat
	 * @return String
	 */
	public static String verbosePixelformat(final int pixelformat) {
		// TODO get rid of such constructs
		switch (pixelformat) {
			default:
				return PixelFormat.Unknown.toString();
			case DDSImage.D3DFMT_A8R8G8B8:
				return PixelFormat.Unknown.toString();
			case DDSImage.D3DFMT_DXT1:
				return PixelFormat.DXT1.toString();
			case DDSImage.D3DFMT_DXT2:
				return PixelFormat.DXT2.toString();
			case DDSImage.D3DFMT_DXT3:
				return PixelFormat.DXT3.toString();
			case DDSImage.D3DFMT_DXT4:
				return PixelFormat.DXT4.toString();
			case DDSImage.D3DFMT_DXT5:
				return PixelFormat.DXT5.toString();
			case DDSImage.D3DFMT_R8G8B8:
				return PixelFormat.R8G8B8.toString();
			case DDSImage.D3DFMT_X8R8G8B8:
				return PixelFormat.X8R8G8B8.toString();
		}
	}

	/**
	 * Returns true if the pixelformat is compressed a kind of DXTn-Compression
	 * TODO The {@link DDSImage} specifies isCompressed even on D3DFMT_A8R8G8B8, D3DFMT_R8G8B8 and D3DFMT_X8R8G8B8
	 * this doesn't
	 *
	 * @param pixelformat DDSImage pixelformat
	 * @return boolean is compressed
	 */
	public static boolean isDXTCompressed(final int pixelformat) {
		switch (pixelformat) {
			default:
			case DDSImage.D3DFMT_A8R8G8B8:
			case DDSImage.D3DFMT_R8G8B8:
			case DDSImage.D3DFMT_X8R8G8B8:
				return false;
			case DDSImage.D3DFMT_DXT1:
			case DDSImage.D3DFMT_DXT2:
			case DDSImage.D3DFMT_DXT3:
			case DDSImage.D3DFMT_DXT4:
			case DDSImage.D3DFMT_DXT5:
				return true;
		}
	}
}
