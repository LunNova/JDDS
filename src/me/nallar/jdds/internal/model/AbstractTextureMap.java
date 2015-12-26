/**
 *
 */
package me.nallar.jdds.internal.model;

import gr.zdimensions.jsquish.Squish;
import gr.zdimensions.jsquish.Squish.CompressionType;
import me.nallar.jdds.internal.compression.DXTBufferCompressor;
import me.nallar.jdds.internal.ddsutil.PixelFormats;

import javax.activation.UnsupportedDataTypeException;
import java.awt.image.*;
import java.nio.*;


/**
 * Abstract TextureMap
 *
 * @author danielsenff
 */
public abstract class AbstractTextureMap implements TextureMap {

	public AbstractTextureMap() {
	}

	@Override
	public ByteBuffer[] getDXTCompressedBuffer(final int pixelformat)
			throws UnsupportedDataTypeException {
		CompressionType compressionType = PixelFormats.getSquishCompressionFormat(pixelformat);
		return this.getDXTCompressedBuffer(compressionType);
	}

	/**
	 * @param bi
	 * @param compressionType
	 * @return
	 */
	public ByteBuffer compress(final BufferedImage bi,
							   final Squish.CompressionType compressionType) {
		DXTBufferCompressor compi = new DXTBufferCompressor(bi, compressionType);
		return compi.getByteBuffer();
	}

}
