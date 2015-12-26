package me.nallar.jdds.internal.compression;

import me.nallar.jdds.internal.ddsutil.ByteBufferedImage;

import java.awt.*;
import java.awt.image.*;
import java.nio.*;

public abstract class BufferDecompressor {


	protected ByteBuffer uncompressedBuffer;
	protected Dimension dimension;


	/**
	 * @return
	 */
	public BufferedImage getImage() {

		return new ByteBufferedImage(
				this.dimension.width,
				this.dimension.height,
				this.uncompressedBuffer);
	}
}
