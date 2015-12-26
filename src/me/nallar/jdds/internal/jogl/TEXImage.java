/*
 * Copyright (c) 2005 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * - Redistribution of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 * 
 * - Redistribution in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
 * INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN
 * MICROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR
 * ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR
 * DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE
 * DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY,
 * ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF
 * SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that this software is not designed or intended for use
 * in the design, construction, operation or maintenance of any nuclear
 * facility.
 * 
 * Sun gratefully acknowledges that this software was originally authored
 * and developed by Kenneth Bradley Russell and Christopher John Kline.
 */

package me.nallar.jdds.internal.jogl;

import me.nallar.jdds.internal.jogl.TEXImage.Header.EmbeddedBuffer;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;


/**
 * A reader and writer for Tex (.tex) files, which are
 * used to describe textures. These files can contain multiple mipmap
 * levels in one file. This class is currently minimal and does not
 * support all of the possible file formats.
 * http://www.realgpx.com/partage/texformat.htm
 */

public class TEXImage {


	private Header header;
	private final Vector<DDSImage> embeddedMap;


	/**
	 * Reads a DirectDraw surface from the specified file, returning
	 * the resulting DDSImage.
	 *
	 * @param file File object
	 * @return DDS image object
	 * @throws java.io.IOException if an I/O exception occurred
	 */
	public static TEXImage read(File file) throws IOException {
		TEXImage image = new TEXImage();
		image.readFromFile(file);
		return image;
	}


	/**
	 * Width of the texture (or the top-most mipmap if mipmaps are
	 * present)
	 */
	public int getWidth() {
		return header.width;
	}

	/**
	 * Height of the texture (or the top-most mipmap if mipmaps are
	 * present)
	 */
	public int getHeight() {
		return header.height;
	}


	//----------------------------------------------------------------------
	// Internals only below this point
	//

	//  private static final int MAGIC = 0x20534444;
	private static final int MAGIC = 0x54455800;

	static class Header {
		int height;               // height of surface to be created
		int width;                // width of input surface
		int mipMapCountOrAux;     // number of mip-map levels requested (in this context), range of 1 to 8
		int alphaBitDepth;        // depth of alpha buffer requested
		Vector<EmbeddedBuffer> embeddedMap;

		void read(ByteBuffer buf) throws IOException {
			int magic = buf.getInt();
			if (magic != MAGIC) {
				throw new IOException("Incorrect magic number 0x" +
						Integer.toHexString(magic) +
						" (expected " + MAGIC + ")");
			}

			width = buf.getInt();
			height = buf.getInt();
			alphaBitDepth = buf.getInt();
			mipMapCountOrAux = buf.getInt();

			embeddedMap = readHeaderTable(buf); // header data for embedded dds

//			for (EmbeddedBuffer embBuffer : embeddedMap) {
//				DDSImage image = DDSImage.read(embBuffer.buffer);
//			}
		}

		private Vector<EmbeddedBuffer> readHeaderTable(ByteBuffer buf) {
			int offset, size, currentPos;
			Vector<EmbeddedBuffer> embeddedBuffer = new Vector<>();
			/*
			 *  iterate over 5 tables, mapping pixelformat
			 *  Table #1 : r8g8b8, x8r8g8b8, r5g6b5, x1r5g5b5 
			 *  Table #2 : a8r8g8b8, a4r4g4b4, DXT2, DXT3, DXT4 
			 *	Table #3 : a1r5g5b5 
			 * 	Table #4 : DXT1 
			 *  Table #5 : DXT5
			 */
			for (int t = 0; t < 5; t++) {
				// iterate over 8 maps
//				System.out.println("Table: "+t);
				for (int i = 0; i < 8; i++) {
					offset = buf.getInt();
					size = buf.getInt();
//					System.out.println("offset="+offset+" size="+size);
					EmbeddedBuffer embBuffer = new EmbeddedBuffer();
					if ((offset != -1) && (size != -1)) { // if not blank
						currentPos = buf.position();
						byte[] ddsbuffer = new byte[size];
						buf.position(offset);
						buf.get(ddsbuffer);
						embBuffer.buffer = ByteBuffer.wrap(ddsbuffer);
						embeddedBuffer.add(embBuffer);
						buf.position(currentPos);
					}
				}
			}
			for (EmbeddedBuffer embeddedBuffer2 : embeddedBuffer) {
				embeddedBuffer2.buffer.rewind();
				embeddedBuffer2.buffer.order(ByteOrder.LITTLE_ENDIAN);
			}

			return embeddedBuffer;
		}

		class EmbeddedBuffer {
			ByteBuffer buffer;
		}

		// buf must be in little-endian byte order
		void write(ByteBuffer buf) {
			buf.putInt(MAGIC);
			buf.putInt(width);
			buf.putInt(height);
			buf.putInt(alphaBitDepth);
			buf.putInt(mipMapCountOrAux);

			// header table


			// embedded buffer

		}

		private static int writtenSize() {
			return 340;
		}
	}

	private TEXImage() {
		embeddedMap = new Vector<>();
	}

	private void readFromFile(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		FileChannel chan = fis.getChannel();
		ByteBuffer buf = chan.map(FileChannel.MapMode.READ_ONLY,
				0, (int) file.length());
		readFromBuffer(buf);
	}

	private void readFromBuffer(ByteBuffer buf) throws IOException {
		ByteBuffer buf1 = buf;
		buf.order(ByteOrder.LITTLE_ENDIAN);
		header = new Header();
		header.read(buf);
		for (EmbeddedBuffer embBuffer : header.embeddedMap) {
			embBuffer.buffer.rewind();
			embeddedMap.add(DDSImage.read(embBuffer.buffer));
		}
	}

	/**
	 * Get a {@link DDSImage} embedded in this TEX-file.
	 *
	 * @param index
	 * @return
	 */
	public DDSImage getEmbeddedMaps(int index) {
		return embeddedMap.get(index);
	}

}
