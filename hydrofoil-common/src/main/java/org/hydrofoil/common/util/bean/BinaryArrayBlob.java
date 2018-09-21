package org.hydrofoil.common.util.bean;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.hydrofoil.common.util.ParameterUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;

/**
 * GraphBlob
 * <p>
 * package org.hydrofoil.common.graph.expand
 *
 * @author xie_yh
 * @date 2018/8/25 16:45
 */
public class BinaryArrayBlob implements Blob {

    private byte[] chunk;

    public BinaryArrayBlob(byte[] data) throws IOException {
        this(new ByteArrayInputStream(data));
    }

    public BinaryArrayBlob(Blob blob) throws IOException, SQLException {
        this(blob.getBinaryStream());
    }

    public BinaryArrayBlob(InputStream dataStream) throws IOException {
        try {
            this.chunk = IOUtils.toByteArray(dataStream);
        }finally {
            IOUtils.closeQuietly(dataStream);
        }
    }

    @Override
    public long length() throws SQLException {
        return ArrayUtils.getLength(chunk);
    }

    @Override
    public byte[] getBytes(long pos, int length) throws SQLException {
        return ArrayUtils.subarray(chunk,(int)pos,((int)pos + length));
    }

    @Override
    public InputStream getBinaryStream() throws SQLException {
        return new ByteArrayInputStream(chunk);
    }

    @Override
    public long position(byte[] pattern, long start) throws SQLException {
        ParameterUtils.checkSupport(false,"");
        return 0;
    }

    @Override
    public long position(Blob pattern, long start) throws SQLException {
        ParameterUtils.checkSupport(false,"");
        return 0;
    }

    @Override
    public int setBytes(long pos, byte[] bytes) throws SQLException {
        ParameterUtils.checkSupport(false,"");
        return 0;
    }

    @Override
    public int setBytes(long pos, byte[] bytes, int offset, int len) throws SQLException {
        ParameterUtils.checkSupport(false,"");
        return 0;
    }

    @Override
    public OutputStream setBinaryStream(long pos) throws SQLException {
        ParameterUtils.checkSupport(false,"");
        return null;
    }

    @Override
    public void truncate(long len) throws SQLException {
        chunk = getBytes(0, (int) len);
    }

    @Override
    public void free() throws SQLException {
        chunk = null;
    }

    @Override
    public InputStream getBinaryStream(long pos, long length) throws SQLException {
        return new ByteArrayInputStream(getBytes(pos, (int) length));
    }
}
