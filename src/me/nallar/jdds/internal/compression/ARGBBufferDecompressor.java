package me.nallar.jdds.internal.compression;

import java.awt.*;
import java.nio.*;

public class ARGBBufferDecompressor extends BufferDecompressor {

	public ARGBBufferDecompressor(final ByteBuffer compressedBuffer,
								  final int width, final int height, int pixelformat) {
		this(compressedBuffer, new Dimension(width, height), pixelformat);
	}

	public ARGBBufferDecompressor(final ByteBuffer databuffer,
								  final Dimension dimension, int pixelformat) {
		this.uncompressedBuffer =
				decompressBuffer(databuffer, dimension.width, dimension.height, pixelformat);
		this.dimension = dimension;
	}


	private ByteBuffer decompressBuffer(ByteBuffer dataBuffer, int width,
										int height, Object pix) {

		return dataBuffer;
	}

}
