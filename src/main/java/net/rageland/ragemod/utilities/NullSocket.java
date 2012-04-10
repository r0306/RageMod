package net.rageland.ragemod.utilities;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 *
 * @author martin
 * 
 * Added to RageMod because it may be necessary. Full credit to him.
 * This may be removed.
 * 
 */
public class NullSocket extends Socket {

    @Override
    public InputStream getInputStream() {
        byte[] buf = new byte[1];
        return new ByteArrayInputStream(buf);
    }

    @Override
    public OutputStream getOutputStream() {
        return new ByteArrayOutputStream();
    }
}