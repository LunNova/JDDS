/**
 *
 */
package me.nallar.jdds.internal.ddsutil;

/**
 * @author danielsenff
 */
public class NonCubicDimensionException extends IllegalArgumentException {

	/**
	 *
	 */
	public NonCubicDimensionException() {
		super("MipMaps can not be generated, The image dimensions must be a power of 2");
	}

}
