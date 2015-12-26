/**
 *
 */
package me.nallar.jdds.internal.compression;

import gr.zdimensions.jsquish.Squish;
import gr.zdimensions.jsquish.Squish.CompressionType;
import me.nallar.jdds.internal.ddsutil.ByteBufferedImage;

import java.awt.*;
import java.awt.image.*;
import java.nio.*;
import java.util.zip.*;


/**
 * Compressor for DXT-Compression
 *
 * @author danielsenff
 */
public class DXTBufferCompressor {

	//	byte[] compressedData;
	protected byte[] byteData;
	protected final Dimension dimension;
	protected final CompressionType compressionType;


	/**
	 * @param image
	 * @param compressionType
	 */
	public DXTBufferCompressor(final BufferedImage image,
							   final Squish.CompressionType compressionType) {

		this(ByteBufferedImage.convertBIintoARGBArray(image),
				new Dimension(image.getWidth(null), image.getHeight(null)),
				compressionType);
	}

	/**
	 * @param data            Byte-Array should store ARGB
	 * @param dimension
	 * @param compressionType
	 */
	public DXTBufferCompressor(final byte[] data,
							   final Dimension dimension,
							   final Squish.CompressionType compressionType) {
		this.byteData = data;
		this.dimension = dimension;
		this.compressionType = compressionType;
	}


	/**
	 * @return ByteBuffer
	 */
	public ByteBuffer getByteBuffer() {
		byte[] compressedData;
		try {

			// the data-Array given to the squishCompressToArray is expected to be
			// width * height * 4 -> with RGBA, which means, if we got RGB, we need to add A!
			if (byteData.length < dimension.height * dimension.width * 4) {
				System.out.println("blow up array from RGB to ARGB");
				byteData = convertRGBArraytiRGBAArray(byteData, dimension);
			}

			compressedData = squishCompressToArray(byteData, dimension.width, dimension.height, compressionType);
			return ByteBuffer.wrap(compressedData);
		} catch (DataFormatException e) {
			e.printStackTrace();
		}
		return null;

	}

	private byte[] convertRGBArraytiRGBAArray(byte[] data, final Dimension dimension) {

		int rgbLength = data.length;
		int rgbaLength = dimension.width * dimension.height * 4;

		byte[] rgbaBuffer = new byte[rgbaLength];

		// populate new array
		// we always copy 3 byte chunks, skip one byte, which we set to 255 and take the next 3 byte
		int loopN = 0;
		for (int i = 0; i < rgbLength; i = i + 3) {

			int destPos = i + loopN;

			System.arraycopy(data, i, rgbaBuffer, destPos, 3);
			loopN++;
		}


		return rgbaBuffer;
	}

	/**
	 * Get the Byte-array held by this object.
	 *
	 * @return
	 */
	public byte[] getArray() {
		try {
			return squishCompressToArray(byteData, dimension.width, dimension.height, compressionType);
		} catch (final DataFormatException e) {
			e.printStackTrace();
		}
		return byteData;
	}

	/**
	 * Compresses the RGBA-byte-array into a DXT-compressed {@link ByteBuffer}.
	 * @param rgba
	 * @param height
	 * @param width
	 * @param compressionType
	 * @return
	 */
//	private static ByteBuffer squishCompress(final byte[] rgba, 
//			final int width, 
//			final int height, 
//			final Squish.CompressionType compressionType) {
//		
//		
//		ByteBuffer buffer = ByteBuffer.wrap(squishCompressToArray(rgba, width, height, compressionType));
//		return buffer;
//	}


	/**
	 * Compresses the RGBA-byte-array into a DXT-compressed byte-array.
	 *
	 * @param rgba            Byte-Array needs to be in RGBA-order
	 * @param height
	 * @param width
	 * @param compressionType
	 * @return
	 * @throws DataFormatException
	 */
	private static byte[] squishCompressToArray(final byte[] rgba,
												final int width,
												final int height,
												final Squish.CompressionType compressionType) throws DataFormatException {

		// expected array length
		int length = width * height * 4;
		if (rgba.length != length) throw new DataFormatException("unexpected length:" +
				rgba.length + " instead of " + length);

		int storageRequirements = Squish.getStorageRequirements(width, height, compressionType);

		return Squish.compressImage(rgba,
				width,
				height,
				new byte[storageRequirements],
				compressionType,
				Squish.CompressionMethod.CLUSTER_FIT);
	}


}
