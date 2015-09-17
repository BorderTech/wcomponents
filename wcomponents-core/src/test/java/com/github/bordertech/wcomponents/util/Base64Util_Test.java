package com.github.bordertech.wcomponents.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test the functionality of the {@link Base64Util} class. The Base64Util class from an open source project, so only a
 * small amount of testing is performed here.
 *
 * @author Francis Naoum
 */
public class Base64Util_Test {

	@Test
	public void testEncodeBytes() {
		byte[] bytes = new byte[]{0x12, 0x23};
		String expected = "EiM=";

		String sfpEncoded = Base64Util.encode(bytes);
		Assert.assertEquals("Encoded String doesn't match expected String", expected, sfpEncoded);
	}

	@Test
	public void testEncodeString() {
		String initial = "Hello world!~@#$%^&*()";
		String encoded = Base64Util.encodeString(initial);
		String decoded = Base64Util.decodeString(encoded);
		Assert.assertEquals("Decoded string doesn't match initial string", initial, decoded);
	}

	@Test
	public void testDecode() throws Exception {
		// Some GIF file which was grabbed off the internet
		String strToDecode = "R0lGODlhZAAyALMAAIiIiLu7u2ZmZiIiIszMzO7u7hERETMzM0RERN3d3aqqqlVVVZmZmXd3dwAAAP////yH5BAAAAAAALAAAAABkADIAAATh8MlJq7046827/2AojmRpnmiqrmzrvnAsz3Rt33iu73zv/8CgcEgsGoIpHLJHAEcj2ezJp16BA6HAfAIOBbZg2LgGBCi0CrhkEUUrBWG49xwJLyHgtcRKAwQaIF+gAkHgHATT2cTXlwPBgsSAgOBUk9vDwpziBIJZAMCCV18EqCSlJZQWFmsAZwTDKsKXq4Ppg+TlaoGrxsGArSlAqe6aIu9Eg0GZ14MwbbDuKhpUAUGeQ8IBpicYFkNo7W3uakSa2G1yOrr7O3u7/Dx8vP09fb3+Pn6+/z9/v8AAwocaCICADsA";
		byte[] expected
				= {
					71, 73, 70, 56, 57, 97, 100, 0, 50, 0, -77, 0, 0, -120, -120, -120, -69, -69, -69, 102, 102,
					102, 34, 34, 34, -52, -52, -52, -18, -18, -18, 17, 17, 17, 51, 51, 51, 68, 68, 68, -35, -35,
					-35, -86, -86, -86, 85, 85, 85, -103, -103, -103, 119, 119, 119, 0, 0, 0, -1, -1, -1, -4, -121,
					-28, 16, 0, 0, 0, 0, 0, -80, 0, 0, 0, 1, -112, 0, -56, 0, 0, 19, -121, -61, 37, 38, -82, -12,
					-29, -81, 54, -17, -3, -128, -94, 57, -111, -90, 121, -94, -86, -71, -77, -82, -7, -64, -77,
					61, -47, -73, 125, -30, -69, -67, -13, -65, -1, 2, -127, -63, 32, -80, 106, 8, -92, 114, -55,
					28, 1, 28, -113, 103, -77, 38, -99, 122, 4, 14, -121, 1, -16, 8, 56, 22, -39, -125, 98, -32,
					24, 16, -94, -48, 42, -31, -112, 69, 20, -84, 21, -122, -29, -36, 112, 36, -68, -121, -126,
					-41, 17, 40, 12, 16, 104, -127, 126, -128, 9, 7, -128, 112, 19, 79, 103, 19, 94, 92, 15, 6,
					11, 18, 2, 3, -127, 82, 79, 111, 15, 10, 115, -120, 18, 9, 100, 3, 2, 9, 93, 124, 18, -96, -110,
					-108, -106, 80, 88, 89, -84, 1, -100, 19, 12, -85, 10, 94, -82, 15, -90, 15, -109, -107, -86, 6,
					-81, 27, 6, 2, -76, -91, 2, -89, -70, 104, -117, -67, 18, 13, 6, 103, 94, 12, -63, -74, -61, -72,
					-88, 105, 80, 5, 6, 121, 15, 8, 6, -104, -100, 96, 89, 13, -93, -75, -73, -71, -87, 18, 107, 97,
					-75, -56, -22, -21, -20, -19, -18, -17, -16, -15, -14, -13, -12, -11, -10, -9, -8, -7, -6, -5,
					-4, -3, -2, -1, 0, 3, 10, 28, 104, 34, 2, 0, 59, 0
				};

		byte[] decoded = Base64Util.decode(strToDecode);

		Assert.assertEquals("Decoded data is a different length to expected",
				expected.length, decoded.length);

		for (int i = 0; i < expected.length; ++i) {
			Assert.assertEquals("Decoded data differs from expected at byte " + i, expected[i],
					decoded[i]);
		}
	}
}
