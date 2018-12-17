package eco.data.m3.routing.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import eco.data.m3.net.core.MId;

public class MZipContent extends MContent {

	public static final transient int TYPE = 0x07;

    public MZipContent(MId ownerId, String content)
    {
    	super(ownerId, content);
    }
    
	public MZipContent(MId ownerId, byte[] content) {
		super(ownerId, content);
	}

	public static byte[] compress(byte[] data) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ZipOutputStream zipOut = new ZipOutputStream(byteArrayOutputStream);
		zipOut.putNextEntry(new ZipEntry("zipfile"));
		zipOut.write(data);
		zipOut.close();
		return byteArrayOutputStream.toByteArray();

	}

	public static byte[] unCompress(byte[] data) throws IOException {
		ByteArrayInputStream input = new ByteArrayInputStream(data);
		ZipInputStream zis = new ZipInputStream(input);
		ZipEntry ze = null;
		while (((ze = zis.getNextEntry()) != null) && !ze.isDirectory()) {
			String name = ze.getName();
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[10240];
			int length = -1;
			while ((length = zis.read(buffer, 0, buffer.length)) > -1) {
				byteArrayOutputStream.write(buffer, 0, length);
			}
			byteArrayOutputStream.close();
			zis.close();
			return byteArrayOutputStream.toByteArray();
		}
		zis.close();
		return null;

	}

	@Override
	public byte[] toSerializedForm() {
		try {
			byte [] data = compress(this.getData());
			System.out.println("Compressed Data Size:" + data.length);
			return data;
		} catch (IOException e) {
			return null;
		}
	}

	public static MZipContent fromSerializedForm(byte[] data) {
		byte[] out = null;
		try {
			out = unCompress(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		MZipContent c = new MZipContent(new MId(), out);
		return c;
	}

}
