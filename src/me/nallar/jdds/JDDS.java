package me.nallar.jdds;


import gr.zdimensions.jsquish.Squish;
import me.nallar.jdds.internal.compression.DXTBufferCompressor;
import me.nallar.jdds.internal.compression.DXTBufferDecompressor;
import me.nallar.jdds.internal.ddsutil.ByteBufferedImage;
import me.nallar.jdds.internal.ddsutil.PixelFormats;
import me.nallar.jdds.internal.ddsutil.TextureFactory;
import me.nallar.jdds.internal.jogl.DDSImage;
import me.nallar.jdds.internal.jogl.TEXImage;
import me.nallar.jdds.internal.model.TextureMap;

import javax.activation.UnsupportedDataTypeException;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;


/**
 * Easy loading, saving and manipulation of DDSImages and DXT-Compression.
 *
 * @author danielsenff
 */
@SuppressWarnings("unused")
public class JDDS {
	/**
	 * Create a {@link BufferedImage} from a DXT-compressed
	 * dds-texture {@link FileLockInterruptionException}.
	 * @throws IOException
	 */
	public static BufferedImage read(final File file) throws IOException {
		if (file.getName().endsWith(".dds"))
			return loadBufferedImage(DDSImage.read(file));
		else
			return loadBufferedImage(TEXImage.read(file));
	}

	public static BufferedImage readDDS(byte[] data) throws IOException {
		return readDDS(ByteBuffer.wrap(data));
	}

	public static BufferedImage readDDS(ByteBuffer data) throws IOException {
		return loadBufferedImage(DDSImage.read(data));
	}

	/**
	 * Create a {@link BufferedImage} from a DXT-compressed {@link DDSImage}
	 * @throws UnsupportedDataTypeException
	 */
	public static BufferedImage loadBufferedImage(final DDSImage image) throws UnsupportedDataTypeException {
		if (image.isCompressed())
			return decompressTexture(
					image.getMipMap(0).getData(),
					image.getWidth(),
					image.getHeight(),
					findCompressionFormat(image));
		else
			return loadBufferedImageFromByteBuffer(
					image.getMipMap(0).getData(),
					image.getWidth(),
					image.getHeight(),
					image);
	}

	public static BufferedImage loadBufferedImageFromByteBuffer(
			ByteBuffer data, int width, int height,
			DDSImage ddsimage) {

		// check pixelformat
		if (ddsimage.getPixelFormat() == DDSImage.D3DFMT_A8R8G8B8) {
			// data in buffer in 4 byte chunks ordered ARGB
			return new ByteBufferedImage(width, height, data);

		}

		throw new UnsupportedOperationException("Unknown pixel format: " + ddsimage.getPixelFormat());
	}

	/**
	 * Create a {@link BufferedImage} from a DXT-compressed {@link TEXImage}
	 * @throws UnsupportedDataTypeException
	 */
	public static BufferedImage loadBufferedImage(final TEXImage image) throws UnsupportedDataTypeException {
		return decompressTexture(
				image.getEmbeddedMaps(0).getMipMap(0).getData(),
				image.getWidth(),
				image.getHeight(),
				findCompressionFormat(image.getEmbeddedMaps(0)));
	}

	/**
	 * Create a {@link BufferedImage} from a DXT-compressed Byte-array.
	 */
	public static BufferedImage decompressTexture(final byte[] compressedData,
												  final int width,
												  final int height,
												  final Squish.CompressionType compressionType) {
		return new DXTBufferDecompressor(compressedData, width, height, compressionType).getImage();
	}

	/**
	 * Create a {@link BufferedImage} from a DXT-compressed ByteBuffer.
	 */
	public static BufferedImage decompressTexture(final ByteBuffer textureBuffer,
												  final int width,
												  final int height,
												  final Squish.CompressionType compressionType) {
		return new DXTBufferDecompressor(textureBuffer, width, height, compressionType).getImage();
	}

	/**
	 * Create a {@link BufferedImage} from a DXT-compressed ByteBuffer.
	 *
	 * @throws UnsupportedDataTypeException
	 */
	public static BufferedImage decompressTexture(final ByteBuffer textureBuffer,
												  final int width,
												  final int height,
												  final int pixelformat) throws UnsupportedDataTypeException {
		Squish.CompressionType compressionType = PixelFormats.getSquishCompressionFormat(pixelformat);
		return new DXTBufferDecompressor(textureBuffer, width, height, compressionType).getImage();
	}

	/**
	 * Compresses a {@link BufferedImage} into a {@link ByteBuffer}
	 */
	public static ByteBuffer compressTexture(final BufferedImage image,
											 final Squish.CompressionType compressionType) {
		return new DXTBufferCompressor(image, compressionType).getByteBuffer();
	}

	public static byte[] compressTextureToArray(final BufferedImage image,
												final Squish.CompressionType compressionType) {
		return new DXTBufferCompressor(image, compressionType).getArray();
	}

	/**
	 * Writes a DDS-Image file to disk.
	 */
	public static void write(final File destinationfile,
							 BufferedImage sourceImage,
							 final int pixelformat,
							 boolean generateMipMaps) throws IOException {

		int width = sourceImage.getWidth();
		int height = sourceImage.getHeight();

		//convert RGB to RGBA image
		if (!sourceImage.getColorModel().hasAlpha())
			sourceImage = convert(sourceImage, BufferedImage.TYPE_4BYTE_ABGR);

		TextureMap maps = TextureFactory.createTextureMap(generateMipMaps, sourceImage);

		ByteBuffer[] mipmapBuffer;

		if (PixelFormats.isDXTCompressed(pixelformat)) {
			mipmapBuffer = maps.getDXTCompressedBuffer(pixelformat);
		} else
			mipmapBuffer = maps.getUncompressedBuffer();

		writeDDSImage(destinationfile, mipmapBuffer, width, height, pixelformat);
	}

	/**
	 * Converts the {@link BufferedImage} type.
	 */
	public static BufferedImage convert(final BufferedImage srcImage, final int destImgType) {
		BufferedImage img = new BufferedImage(srcImage.getWidth(), srcImage.getHeight(), destImgType);
		Graphics2D g2d = img.createGraphics();
		g2d.drawImage(srcImage, 0, 0, null);
		g2d.dispose();
		return img;
	}

	/**
	 * TODO: what about DXT1?
	 * @throws IOException
	 */
	public void write(final File file,
					  final TextureMap map,
					  final int pixelformat) throws IOException {
		writeDDSImage(file, map.getDXTCompressedBuffer(pixelformat),
				map.getWidth(),
				map.getHeight(),
				pixelformat);
	}

	private static DDSImage writeDDSImage(final File file,
										  final ByteBuffer[] mipmapBuffer,
										  final int width,
										  final int height,
										  final int pixelformat) throws IllegalArgumentException, IOException {

		DDSImage writedds = DDSImage.createFromData(pixelformat, width, height, mipmapBuffer);
		writedds.write(file);
		return writedds;
	}

	/**
	 * Returns the PixelFormat of a {@link File}
	 * This makes file-IO, therefor handle with caution!
	 * @throws IOException
	 */
	public static int getCompressionType(final File file) throws IOException {
		return DDSImage.read(file).getPixelFormat();
	}

	private static Squish.CompressionType findCompressionFormat(DDSImage ddsimage) throws UnsupportedDataTypeException {
		int pixelFormat = ddsimage.getPixelFormat();
		return PixelFormats.getSquishCompressionFormat(pixelFormat);
	}

	/**
	 * Returns true for file formats supported by this library.
	 * Currently TEX and DDS reading is supported.
	 */
	public static boolean isReadSupported(final File file) {
		String fileSuffix = getFileSuffix(file.getName());
		return fileSuffix.endsWith("dds")
				|| fileSuffix.endsWith("tex");
	}

	/**
	 * Returns the lowercase suffix of the given file name (the text
	 * after the last '.' in the file name). Returns null if the file
	 * name has no suffix. Only operates on the given file name;
	 * performs no I/O operations.
	 *
	 * @param filename name of the file
	 * @return lowercase suffix of the file name
	 * @throws NullPointerException if filename is null
	 */
	private static String getFileSuffix(String filename) {
		int lastDot = filename.lastIndexOf('.');
		if (lastDot < 0) {
			return "";
		}
		return filename.substring(lastDot + 1).toLowerCase();
	}
}
