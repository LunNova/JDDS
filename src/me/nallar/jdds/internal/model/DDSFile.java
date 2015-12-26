package me.nallar.jdds.internal.model;

/**
 *
 */

import gr.zdimensions.jsquish.Squish.CompressionType;
import me.nallar.jdds.internal.compression.ARGBBufferDecompressor;
import me.nallar.jdds.internal.compression.BufferDecompressor;
import me.nallar.jdds.internal.compression.DXTBufferDecompressor;
import me.nallar.jdds.internal.ddsutil.PixelFormats;
import me.nallar.jdds.internal.jogl.DDSImage;

import javax.activation.UnsupportedDataTypeException;
import java.io.*;
import java.nio.*;


/**
 * @author danielsenff
 */
public class DDSFile extends AbstractTextureImage {

	protected TextureType textureType;
	private DDSImage ddsimage;

	/**
	 * Constructs a DDSFile from a {@link File}
	 *
	 * @param file
	 */
	public DDSFile(final File file) {
		this.file = file;
		try {
			init(DDSImage.read(file));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Constructs a DDSFile from a {@link File} and a {@link DDSImage}
	 *
	 * @param file
	 * @param ddsimage
	 */
	public DDSFile(final File file, final DDSImage ddsimage) {
		this.file = file;
		init(ddsimage);
	}

	/**
	 * @param ddsimage
	 */
	protected void init(final DDSImage ddsimage) {
		this.ddsimage = ddsimage;
		this.width = ddsimage.getWidth();
		this.height = ddsimage.getHeight();
		this.pixelformat = ddsimage.getPixelFormat();
		this.textureType = getTextureType(ddsimage);
		this.numMipMaps = ddsimage.getNumMipMaps();
		this.mipMaps = new MipMaps(this.numMipMaps);
		this.hasMipMaps = (ddsimage.getNumMipMaps() > 1); // there is always at least the topmost MipMap
	}

	/**
	 * Load the ImageData for the specified MipMap from original {@link DDSImage}.
	 *
	 * @param mipmap
	 * @throws UnsupportedDataTypeException
	 */
	public void loadImageData(int mipmap) throws UnsupportedDataTypeException {
		if (mipmap <= this.numMipMaps) {

			int width = MipMaps.getMipMapSizeAtIndex(mipmap, ddsimage.getWidth());
			int height = MipMaps.getMipMapSizeAtIndex(mipmap, ddsimage.getHeight());
			ByteBuffer data = ddsimage.getMipMap(mipmap).getData();

			BufferDecompressor bufferDecompressor;
			if (isCompressed()) {
				CompressionType compressionType =
						PixelFormats.getSquishCompressionFormat(ddsimage.getPixelFormat());
				bufferDecompressor = new DXTBufferDecompressor(
						data,
						width,
						height,
						compressionType);
			} else {
				bufferDecompressor = new ARGBBufferDecompressor(
						data,
						width,
						height,
						this.pixelformat);
			}
			this.mipMaps.addMipMap(bufferDecompressor.getImage());
		}
	}

	@Override
	public String toString() {
		return this.file.getAbsolutePath() + PixelFormats.verbosePixelformat(this.pixelformat);
	}

	@Override
	public boolean equals(Object second) {
		if (second != null && second instanceof DDSFile) {
			DDSFile secondFile = (DDSFile) second;
			return (this.getFile().getAbsoluteFile().equals(secondFile.getFile().getAbsoluteFile()) &&
					this.hasMipMaps() == secondFile.hasMipMaps() &&
					this.getPixelformat() == secondFile.getPixelformat() &&
					this.getHeight() == secondFile.getHeight() &&
					this.getWidth() == secondFile.getWidth());
		}
		return false;
	}

//	public ByteBuffer[] getMipMapData() {
//		ByteBuffer[] buffer = new ByteBuffer[ddsimage.getNumMipMaps()+1];
//		for (int i = 0; i < buffer.length; i++) {
//			buffer[i] = ddsimage.getMipMap(i).getData();
//		}
//		return buffer;
//	}

	/**
	 * The DDS-Image can have different texture types.
	 * Regular Texture, Volume-Texture and CubeMap
	 * This returns the textureType from a {@link DDSImage}
	 *
	 * @param ddsimage
	 * @return
	 */
	public static TextureType getTextureType(final DDSImage ddsimage) {
		if (ddsimage.isCubemap()) {
			return TextureType.CUBEMAP;
		} else if (ddsimage.isVolume()) {
			return TextureType.VOLUME;
		} else {
			return TextureType.TEXTURE;
		}
	}

	public void write(final File targetFile) throws IOException {
		ByteBuffer[] mipmaps = new ByteBuffer[getNumMipMaps()];
		for (int i = 0; i < mipmaps.length; i++) {
			mipmaps[i] = DDSImage.read(this.file).getMipMap(i).getData();
		}

		DDSImage outputDDS = DDSImage.createFromData(this.pixelformat, width, height, mipmaps);
		outputDDS.write(file);
		outputDDS.close();
	}

}
