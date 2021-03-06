///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// MarineStandalone is a minecraft server software and API.
// Copyright (C) MarineMC (marinemc.org)
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

package org.marinemc.io.binary;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.zip.DataFormatException;

import org.marinemc.io.ByteCompressor;
import org.marinemc.io.ByteCompressor.EncodingUseless;
import org.marinemc.logging.Logging;
import org.marinemc.util.Position;

/**
 * (Used to be called ByteData)
 * 
 * @author Fozie
 */
@Deprecated
public class ByteHolder implements Iterable<Byte>, ByteDataOutput,
		ByteDataInput {

	protected List<Byte> bytes;

	protected int readerPos;

	public ByteHolder(final List<Byte> list) {
		bytes = list;
	}

	public ByteHolder(final byte[] bytes) {
		this(wrap(bytes));
	}

	public ByteHolder() {
		bytes = new ArrayList<Byte>();
	}

	public ByteHolder(final Byte[] b) {
		bytes = new ArrayList<>(Arrays.asList(b));
	}

	public static ByteHolder getDataDecompressed(final byte[] data)
			throws DataFormatException {
		return new ByteHolder(ByteCompressor.instance().decode(data));
	}

	public final static Byte[] wrap(final byte[] array) {
		if (array == null)
			return null;
		else if (array.length == 0)
			return new Byte[] {};

		final Byte[] result = new Byte[array.length];
		for (int i = 0; i < array.length; i++)
			result[i] = new Byte(array[i]);
		return result;
	}

	public static byte[] unwrap(final Byte[] array) {
		if (array == null)
			return null;
		else if (array.length == 0)
			return new byte[] {};

		final byte[] result = new byte[array.length];
		for (int i = 0; i < array.length; i++) {
			final Byte b = array[i];
			result[i] = b == null ? 0 : b.byteValue();
		}
		return result;
	}

	public byte[] compress() throws EncodingUseless {
		return ByteCompressor.instance().encode(getBytes());
	}

	public void writeByte(final byte... b) {
		for (final byte by : b)
			bytes.add(by);
	}

	@Override
	public boolean readBoolean() {
		if (readByte() == 0)
			return false;
		else
			return true;
	}

	@Override
	public byte readByte() {

		if (!hasBytes()) {
			Logging.getLogger().error("ByteHolder object is empty");
			return 0;
		}

		if (!canReadAnother()) {
			Logging.getLogger().error("ByteHolder ran out of bytes");
			return 0;
		}

		final byte b = bytes.get(readerPos);
		readerPos++;
		return b;
	}

	@Override
	public short readShort() {
		if (bytes.size() < 2)
			return 0;

		return (short) (readByte() << 8 | readByte() & 0xff);
	}

	@Override
	public int readUnsignedShort() {
		if (bytes.size() < 2)
			return 0;

		return (readByte() & 0xff) << 8 | readByte() & 0xff;
	}

	@Override
	public int readInt() {

		if (bytes.size() < 4)
			return 0;

		return (readByte() & 0xff) << 24 | (readByte() & 0xff) << 16
				| (readByte() & 0xff) << 8 | readByte() & 0xff;

	}

	@Override
	public long readLong() {
		if (bytes.size() < 8)
			return 0;
		return (long) (readByte() & 0xff) << 56
				| (long) (readByte() & 0xff) << 48
				| (long) (readByte() & 0xff) << 40
				| (long) (readByte() & 0xff) << 32
				| (long) (readByte() & 0xff) << 24
				| (long) (readByte() & 0xff) << 16
				| (long) (readByte() & 0xff) << 8 | readByte() & 0xff;
	}

	@Override
	public float readFloat() {
		return Float.intBitsToFloat(readInt());
	}

	@Override
	public double readDouble() {
		return Double.longBitsToDouble(readLong());
	}

	public char readChar() { // To give control of character readings in updates
		return readCharacter();
	}

	private char readCharacter() {
		if (bytes.size() < 2)
			return 0;

		return (char) (readByte() << 8 | readByte() & 0xff);
	}

	public byte[] readAllBytes() {
		final byte[] a = new byte[bytes.size()];
		int i = 0;
		for (final byte b : bytes) {
			a[i] = b;
			readerPos++;
			i++;
		}

		return a;
	}

	public byte[] getBytes() {
		final int size = bytes.size();
		final byte[] out = new byte[size];
		for (int i = 0; i < size; i++)
			out[i] = bytes.get(i).byteValue();
		return out;
	}

	@Override
	public int readVarInt() {
		int out = 0;
		int bytes = 0;
		byte in;
		while (true) {
			in = readByte();
			out |= (in & 0x7F) << bytes++ * 7;
			if ((in & 0x80) != 0x80)
				break;
		}
		return out;
	}

	public ByteHolder subData(final int a, final int b) {
		return new ByteHolder(bytes.subList(a, b));
	}

	public ByteHolder readData(final int l) {
		int x = 0;
		final ByteHolder data = new ByteHolder();

		while (l > x) {
			data.writeByte(readByte());
			x++;
		}

		return data;
	}

	@Override
	public byte[] read(final byte... input) {
		int i = 0;
		while (i < input.length) {
			input[i] = readByte();
			i++;
		}
		return input;
	}

	@Override
	public byte[] readBytes(final int amt) {
		final byte[] r = new byte[amt];
		final int i = 0;
		while (amt > i)
			r[i] = readByte();

		return r;
	}

	public boolean hasBytes() {
		return !bytes.isEmpty();
	}

	public boolean canReadAnother() {
		return getRemainingBytes() > 0;
	}

	public int remainingBytes() {
		return bytes.size() - readerPos;
	}

	public String readUTF8() {
		final int l = readVarInt();
		if (l >= Short.MAX_VALUE)
			Logging.getLogger().error(
					"Tried to read String greater then max value!");
		byte[] data = new byte[l];
		data = read(data);
		return new String(data, StandardCharsets.UTF_8);
	}

	public String readUTF8Short() {
		final int l = readShort();
		byte[] data = new byte[l];
		data = read(data);
		return new String(data, StandardCharsets.UTF_8);
	}

	public void writeToStream(final OutputStream stream) {
		try {
			for (final byte b : bytes)
				stream.write(b);
		} catch (final IOException e) {
		}
	}

	public void backReader(final int amt) {
		readerPos = -amt;
		if (readerPos < 0)
			readerPos = 0;
	}

	public int getReaderPos() {
		return readerPos;
	}

	public int getRemainingBytes() {
		return bytes.size() - readerPos;
	}

	public int getLength() {
		return bytes.size();
	}

	public void writeUTF8(final String v) {
		final byte[] bytes = v.getBytes(StandardCharsets.UTF_8);
		if (bytes.length >= Short.MAX_VALUE)
			Logging.getLogger().error(
					"Tried to write String greater then max value!");
		// Write the string's length
		writeVarInt(bytes.length);
		writeend(bytes);
	}

	public void writeUTF16(final String v) {
		final byte[] bytes = v.getBytes(StandardCharsets.UTF_16);
		if (bytes.length >= Short.MAX_VALUE)
			Logging.getLogger().error(
					"Tried to write String greater then max value!");
		// Write the string's length
		writeVarInt(bytes.length);
		writeend(bytes);
	}

	public void writeASCII(final String v) {
		final byte[] bytes = v.getBytes(StandardCharsets.US_ASCII);
		if (bytes.length >= Short.MAX_VALUE)
			Logging.getLogger().error(
					"Tried to write String greater then max value!");
		// Write the string's length
		writeVarInt(bytes.length);
		writeend(bytes);
	}

	public void writeUTF8Short(final String v) {
		final byte[] bytes = v.getBytes(StandardCharsets.UTF_8);
		writeShort((short) bytes.length);
		writeend(bytes);
	}

	public void writeend(final byte... v) {
		for (final byte b : v)
			bytes.add(b);
	}

	public void write(final int pos, final byte... v) {
		bytes.addAll(pos, Arrays.asList(wrap(v)));
	}

	@Override
	public void writeBoolean(final boolean v) {
		writeend(ByteEncoder.writeBoolean(v));
	}

	@Override
	public void writeShort(final short v) {
		writeend(ByteEncoder.writeShort(v));
	}

	@Override
	public void writeInt(final int v) {
		writeend(ByteEncoder.writeInt(v));
	}

	@Override
	public void writeLong(final long v) {
		writeend(ByteEncoder.writeLong(v));
	}

	@Override
	public void writeFloat(final float v) {
		writeend(ByteEncoder.writeFloat(v));
	}

	@Override
	public void writeDouble(final double v) {
		writeend(ByteEncoder.writeDouble(v));
	}

	@Override
	public void writeVarInt(final int v) {
		writeend(ByteEncoder.writeVarInt(v));
	}

	public boolean writeObj(final Object obj) {
		if (obj instanceof Byte) {
			writeByte((byte) obj);
			return true;
		} else if (obj instanceof Short) {
			writeShort((short) obj);
			return true;
		} else if (obj instanceof Integer) {
			writeInt((int) obj);
			return true;
		} else if (obj instanceof Long) {
			writeLong((long) obj);
			return true;
		} else if (obj instanceof Float) {
			writeFloat((float) obj);
			return true;
		} else if (obj instanceof Double) {
			writeDouble((double) obj);
			return true;
		} else
			return false;

	}

	public void writeVarInt(final int pos, final int v) {
		write(pos, ByteEncoder.writeVarInt(v));
	}

	public void writePosition(final Position pos) {
		writeend(ByteEncoder.writeLong(pos.encode()));
	}

	public Position readPosition() {
		return Position.Decode(readLong());
	}

	public void writePacketPrefix() {
		write(0, ByteEncoder.writeVarInt(bytes.size()));
	}

	@Override
	public Iterator<Byte> iterator() {
		return bytes.iterator();
	}

	public void writeUUID(final UUID uuid) {
		writeLong(uuid.getMostSignificantBits());
		writeLong(uuid.getLeastSignificantBits());
	}

	public List<Byte> getByteList() {
		return bytes;
	}

	public void writeData(final ByteHolder data) {
		bytes.addAll(data.getByteList());
	}

	public void writeArray(final Byte[] array) {
		bytes.addAll(Arrays.asList(array));
	}

	public void writeList(final List<Byte> data) {
		bytes.addAll(data);
	}

	@Override
	public byte[] toBytes() {
		return unwrap((Byte[]) bytes.toArray());
	}

	@Override
	public void writeByte(final byte v) {
		bytes.add(v);
	}

	@Override
	public void writeVarLong(final long v) {

	}

	@Override
	public void writeString(final String s, final Charset set) {
		writeend(s.getBytes(set));
	}

	@Override
	public void write(final byte... input) {
		writeend(input);
	}

	@Override
	public int size() {
		return bytes.size();
	}

	@Override
	public long readVarLong() {
		return 0;
	}

	@Override
	public String readString(final int size, final Charset charset) {
		// TODO Auto-generated method stub
		return null;
	}

}