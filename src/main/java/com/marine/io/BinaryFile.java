///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// MarineStandalone is a minecraft server software and API.
// Copyright (C) IntellectualSites (marine.intellectualsites.com)
//
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License along
// with this program; if not, write to the Free Software Foundation, Inc.,
// 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package com.marine.io;

import com.marine.io.data.ByteData;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class BinaryFile {
    File file;

    ByteData data;

    public BinaryFile(File f) {
        this.file = f;
        this.data = new ByteData();
    }

    public BinaryFile(File f, ByteData v) {
        this.data = v;
        this.file = f;
    }

    public static InputStream decompressStream(InputStream input) throws IOException {
        PushbackInputStream pb = new PushbackInputStream(input, 2); //we need a pushbackstream to look ahead
        byte[] signature = new byte[2];
        pb.read(signature); //read the signature
        pb.unread(signature); //push back the signature to the stream
        if (signature[0] == (byte) 0x1f && signature[1] == (byte) 0x8b) //check if matches standard gzip magic number
            return new GZIPInputStream(pb);
        else
            return pb;
    }

    @SuppressWarnings("resource")
    public BinaryFile readBinary() throws IOException {
        if (!file.canRead()) throw new IOException("Can't read file: " + file.getName());
        if (!file.exists()) throw new FileNotFoundException("File not found: " + file.getName());

        byte[] r = new byte[(int) file.length()];
        InputStream input = new BufferedInputStream(new FileInputStream(file));
        input.read(r);
        data = new ByteData(r);
        return this;
    }

    public BinaryFile readGZIPBinary() throws IOException {
        if (!file.canRead()) throw new IOException("Can't read file: " + file.getName());
        if (!file.exists()) throw new FileNotFoundException("File not found: " + file.getName());

        byte[] r = new byte[(int) file.length()];
        InputStream input = decompressStream(new BufferedInputStream(new FileInputStream(file)));
        input.read(r);
        data = new ByteData(r);
        return this;
    }

    public void writeBinary() throws IOException, FileNotFoundException {
        if (!file.exists())
            file.createNewFile();
        OutputStream output = null;
        output = new BufferedOutputStream(new FileOutputStream(file));
        output.write(data.getBytes());
        output.close();
    }

    public void writeGZIPBinary() throws IOException, FileNotFoundException {
        if (!file.exists())
            file.createNewFile();
        GZIPOutputStream output = new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
        output.write(data.getBytes());
        output.close();
    }

    public ByteData getData() {
        if (data == null)
            return null;
        return data;
    }
}
