package me.nallar.jdds.internal.model;

import gr.zdimensions.jsquish.Squish;

import javax.activation.UnsupportedDataTypeException;
import java.awt.image.*;
import java.nio.*;

/**
 * Interface for TextureMaps.
 *
 * @author danielsenff
 */
public interface TextureMap {


	/**
	 * Height of the topmost MipMap.
	 *
	 * @return
	 */
	int getHeight();

	/**
	 * Width of the topmost MipMap.
	 *
	 * @return
	 */
	int getWidth();


	/**
	 * All contained MipMaps compressed with DXT in {@link ByteBuffer}
	 *
	 * @param compressionType
	 * @return
	 */
	ByteBuffer[] getDXTCompressedBuffer(final Squish.CompressionType compressionType);

	/**
	 * All contained MipMaps as {@link ByteBuffer}
	 *
	 * @return
	 */
	ByteBuffer[] getUncompressedBuffer();

	/**
	 * Returns a ByteBuffer for each MipMap.
	 *
	 * @param pixelformat
	 * @return
	 * @throws UnsupportedDataTypeException
	 */
	ByteBuffer[] getDXTCompressedBuffer(final int pixelformat)
			throws UnsupportedDataTypeException;

}
