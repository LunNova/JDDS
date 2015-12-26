/**
 *
 */
package me.nallar.jdds.internal.compression;

import gr.zdimensions.jsquish.Squish;
import gr.zdimensions.jsquish.Squish.CompressionType;

import java.awt.*;
import java.nio.*;


/**
 * Decompressor for DXT-Compression
 *
 * @author danielsenff
 */
public class DXTBufferDecompressor extends BufferDecompressor {


	/**
	 * @param compressedBuffer
	 * @param width
	 * @param height
	 */
	public DXTBufferDecompressor(final ByteBuffer compressedBuffer,
								 final int width, final int height, CompressionType type) {
		this(compressedBuffer, new Dimension(width, height), type);
	}


	/**
	 * @param compressedData
	 * @param width
	 * @param height
	 * @param compressionType
	 */
	public DXTBufferDecompressor(byte[] compressedData, int width, int height,
								 CompressionType compressionType) {
		this(ByteBuffer.wrap(compressedData), new Dimension(width, height), compressionType);
	}

	/**
	 * @param compressedBuffer
	 * @param dimension
	 * @param type
	 */
	public DXTBufferDecompressor(final ByteBuffer compressedBuffer,
								 final Dimension dimension, CompressionType type) {
		this.uncompressedBuffer =
				squishDecompressBuffer(compressedBuffer, dimension.width, dimension.height, type);
		this.dimension = dimension;

	}


	/**
	 * Compresses a Byte-Array into a DXT-compressed {@link ByteBuffer}
	 * If the type is null, it returns the uncompressed ByteBuffer.
	 * <p>
	 * Decompresses a DXT-compressed Byte-Array and returns a byte-Array.
	 * If the {@link CompressionType} is null, it return the source data.
	 *
	 * @param compressedData
	 * @param width
	 * @param height
	 * @param type
	 * @return byte[]
	 * @throws OutOfMemoryError
	 */
	public static byte[] squishDecompressToArray(final byte[] compressedData, final int width, final int height,
												 final Squish.CompressionType type) throws OutOfMemoryError {

		//Use JSquish to decompress images. Then bind as normal. 
		if (type != null) {
			return Squish.decompressImage(null, width, height, compressedData, type);
		}

		return compressedData;

	}


	/**
	 * Decompresses a DXT-compressed Byte-Array and returns a ByteBuffer
	 * If the {@link CompressionType} is null, it return the source data
	 *
	 * @param compressedData
	 * @param width
	 * @param height
	 * @param type
	 * @return
	 * @throws OutOfMemoryError
	 */
	public static ByteBuffer squishDecompress(final byte[] compressedData, final int width, final int height,
											  final Squish.CompressionType type) throws OutOfMemoryError {

		return ByteBuffer.wrap(squishDecompressToArray(compressedData, width, height, type));
	}


	/**
	 * Decompresses a DXT-compressed {@link ByteBuffer}
	 *
	 * @param byteBuffer
	 * @param width
	 * @param height
	 * @param type
	 * @return
	 * @throws OutOfMemoryError
	 */
	private static ByteBuffer squishDecompressBuffer(final ByteBuffer byteBuffer,
													 final int width, final int height,
													 final Squish.CompressionType type) throws OutOfMemoryError {

		byte[] data = new byte[byteBuffer.capacity()];
		byteBuffer.get(data);

		return squishDecompress(data, width, height, type);
	}

}
