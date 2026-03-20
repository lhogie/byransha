package byransha.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZip {
	public static byte[] gzip(byte[] data) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			GZIPOutputStream gos = new GZIPOutputStream(bos);
			gos.write(data);
			gos.close();
			return bos.toByteArray();
		} catch (IOException ex) {
			throw new IllegalStateException();
		}
	}

	public static byte[] gunzip(byte[] data) {
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(data);
			GZIPInputStream gis = new GZIPInputStream(bis);
			byte[] uncompressedData = gis.readAllBytes();
			return uncompressedData;
		} catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
	}

}
