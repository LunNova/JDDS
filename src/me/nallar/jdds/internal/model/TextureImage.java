package me.nallar.jdds.internal.model;

import java.io.*;

public interface TextureImage {

	/**
	 * TextureType describes what kind of Texture the DDS is. Regular 2D-Texture, Volume or Cubemap.
	 */
	enum TextureType {
		/**
		 * Regular texture (plus MipMaps) with one slice.
		 */
		TEXTURE,
		/**
		 * Cubemaps contain 6 slices (including MipMaps) for 6 sides of a cube.
		 */
		CUBEMAP,
		/**
		 * Volume-textures contain many slices (including MipMaps).
		 */
		VOLUME
	}

	/**
	 * Pixelformat describes the way pixels are stored in the DDS.
	 * Either uncompressed or with a special me.nallar.jdds.internal.compression format.
	 */
	enum PixelFormat {
		DXT5, DXT4, DXT3, DXT2, DXT1,
		X8R8G8B8, R8G8B8,
		Unknown
	}


	/**
	 * Write this Image to disk.
	 *
	 * @param file
	 * @throws IOException
	 */
	void write(final File file) throws IOException;


}
