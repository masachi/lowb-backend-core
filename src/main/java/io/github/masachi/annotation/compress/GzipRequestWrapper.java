package io.github.masachi.annotation.compress;

import io.github.masachi.utils.BaseUtil;
import io.github.masachi.utils.GzipUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

@Log4j2
public class GzipRequestWrapper extends HttpServletRequestWrapper {
    private byte[] bytes;

    public GzipRequestWrapper(HttpServletRequest request) {
        super(request);
        setBody(request);
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() {
        return new GzipServletInputStream(bytes);
    }

    private void setBody(HttpServletRequest request) {
        String contentEncoding = request.getHeader("Content-Encoding");
        if (BaseUtil.isNotEmpty(contentEncoding) && contentEncoding.equals("gzip")) {
            // 获取输入流
            BufferedReader reader;
            try {
                reader = request.getReader();
                // 将输入流中的请求实体转换为 byte 数组, 进行 gzip 解压
                bytes = IOUtils.toByteArray(reader, "ISO-8859-1");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class GzipServletInputStream extends ServletInputStream {

        private final ByteArrayInputStream buffer;

        public GzipServletInputStream(byte[] contents) {
            this.buffer = new ByteArrayInputStream(GzipUtil.uncompressToByte(contents));
        }

        @Override
        public int read() throws IOException {
            return buffer.read();
        }

        @Override
        public boolean isFinished() {
            return buffer.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            throw new RuntimeException("Not implemented");
        }
    }
}
