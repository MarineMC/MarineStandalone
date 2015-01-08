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

package org.marinemc.io.nbt;

import java.util.ArrayList;
import java.util.List;

import org.marinemc.io.binary.ByteInput;
import org.marinemc.io.binary.ByteList;

/**
 * @author Fozie
 */
public class NBTCompound extends NBTTag {

	List<NBTTag> data;

	public NBTCompound(final String name, final ByteInput data) {
		this(name);
		byte id;
		while ((id = data.readByte()) != 0)
			this.data.add(NBT.parse(data, id));
	}

	public NBTCompound(final String name) {
		super(name, 10);
		data = new ArrayList<>();
	}

	@Override
	public byte[] toByteArray() {
		final ByteList d = new ByteList();
		d.writeByte(getTagID()); // Write start ID
		d.writeUTF8Short(name);
		for (final NBTTag tag : data)
			d.write(tag.toByteArray());

		d.writeByte((byte) 0); // Write the TAG_END tag to tell that the
								// compound have ended.

		return d.toBytes();
	}

	public void addTag(final NBTTag tag) {
		data.add(tag);
	}

	public List<NBTTag> getTags() {
		return data;
	}

	@Override
	public byte[] toNonPrefixedByteArray() {
		final ByteList data = new ByteList();
		for (final NBTTag tag : this.data)
			data.write(tag.toByteArray());
		return data.toBytes();
	}
}
