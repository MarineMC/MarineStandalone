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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.marinemc.util.vectors.Vector3f;
import org.marinemc.util.vectors.Vector3i;

/**
 * @author Fozie
 */
public final class ByteEncoder {
	public static byte[] writeByte(final byte b) {
		return new byte[] { b };
	}

	public static byte[] writeShort(final short v) {
		return new byte[] { (byte) (0xff & v >> 8), (byte) (0xff & v) };
	}

	public static byte[] writeBoolean(final boolean b) {

		byte a;
		if (b)
			a = 1;
		else
			a = 0;

		return new byte[] { a };
	}

	public static byte[] writeInt(final int v) {
		return new byte[] { (byte) (0xff & v >> 24), (byte) (0xff & v >> 16),
				(byte) (0xff & v >> 8), (byte) (0xff & v) };
	}

	public static byte[] writeLong(final long v) {
		return new byte[] { (byte) (0xff & v >> 56), (byte) (0xff & v >> 48),
				(byte) (0xff & v >> 40), (byte) (0xff & v >> 32),
				(byte) (0xff & v >> 24), (byte) (0xff & v >> 16),
				(byte) (0xff & v >> 8), (byte) (0xff & v) };
	}

	public static byte[] writeFloat(final float v) {
		return writeInt(Float.floatToIntBits(v));
	}

	public static byte[] writeDouble(final double v) {
		return writeLong(Double.doubleToLongBits(v));
	}

	public static byte[] writeBytes(final byte[][] da) {

		int size = 0;

		for (final byte[] a : da)
			size += a.length;

		final byte[] r = new byte[size];

		int i = 0;
		for (final byte[] a : da)
			for (final byte b : a) {
				r[i] = b;
				i++;
			}
		return r;
	}

	public static byte[] writeList(final List<Byte> v) {
		final byte[] r = new byte[v.size()];
		for (final byte b : v)
			r[v.indexOf(b)] = b;
		return r;
	}

	public static byte[] writeUTF8(final String s) {
		final List<Byte> data = new ArrayList<Byte>();

		int v = s.length();
		byte part;
		while (true) {
			part = (byte) (v & 0x7F);
			v >>>= 7;
			if (v != 0)
				part |= 0x80;
			data.add(part);
			if (v == 0)
				break;
		}

		for (final byte b : s.getBytes(StandardCharsets.UTF_8))
			data.add(b);

		return ByteUtils.unwrap(data.toArray(new Byte[data.size()]));
	}

	public static Collection<Byte> writeListVarInt(int v) {
		final List<Byte> r = new ArrayList<Byte>();

		byte part;
		while (true) {
			part = (byte) (v & 0x7F);
			v >>>= 7;
			if (v != 0)
				part |= 0x80;
			r.add(part);
			if (v == 0)
				break;
		}
		return r;
	}

	public static byte[] writeVarInt(int v) {
		final List<Byte> r = new ArrayList<Byte>();

		byte part;
		while (true) {
			part = (byte) (v & 0x7F);
			v >>>= 7;
			if (v != 0)
				part |= 0x80;
			r.add(part);
			if (v == 0)
				break;
		}

		return ByteUtils.unwrap((Byte[]) r.toArray());
	}

	public static byte[] writeVector3i(final Vector3i vec) {
		return writeBytes(new byte[][] { writeInt(vec.x), writeInt(vec.y),
				writeInt(vec.z) });
	}

	public static byte[] writeVector3f(final Vector3f vec) {
		return writeBytes(new byte[][] { writeFloat(vec.x), writeFloat(vec.y),
				writeFloat(vec.z) });
	}

	public static byte[] writeUnsignedVarInt(int v) {

		final ArrayList<Byte> out = new ArrayList<Byte>();

		while ((v & 0xFFFFFF80) != 0L) {
			out.add((byte) (v & 0x7F | 0x80));
			v >>>= 7;
		}
		out.add((byte) (v & 0x7F));

		return writeList(out);
	}

	public static byte[] writeSignedVarInt(final int v) {
		return writeUnsignedVarInt(v << 1 ^ v >> 31);
	}
}
