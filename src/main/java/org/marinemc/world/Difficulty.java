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

package org.marinemc.world;

/**
 * All posseble difficulties in minecraft as of 1.8.2
 * 
 * @author Fozie
 */
public enum Difficulty {
	PEACEFUL("Peaceful", 0), EASY("Easy", 1), NORMAL("Normal", 2), HARD("Hard",
			3);

	private final String name;
	private final byte id;

	private Difficulty(final String name, final int id) {
		this.name = name;
		this.id = (byte) id;
	}

	public byte getID() {
		return id;
	}
}
