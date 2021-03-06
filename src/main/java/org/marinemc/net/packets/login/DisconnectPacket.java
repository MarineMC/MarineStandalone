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

package org.marinemc.net.packets.login;

import java.io.IOException;

import org.marinemc.game.chat.ChatColor;
import org.marinemc.game.chat.ChatMessage;
import org.marinemc.io.binary.ByteInput;
import org.marinemc.io.binary.ByteList;
import org.marinemc.net.Packet;
import org.marinemc.net.PacketOutputStream;
import org.marinemc.net.States;

/**
 * @author Fozie
 */
public class DisconnectPacket extends Packet {

	ChatMessage msg;

	public DisconnectPacket(final String msg) {
		this(new ChatMessage(msg).format(ChatColor.BOLD).color(ChatColor.WHITE));
	}

	public DisconnectPacket(final ChatMessage msg) {
		super(0x00, States.LOGIN);
		this.msg = msg;
	}

	@Override
	public void writeToStream(final PacketOutputStream stream)
			throws IOException {
		final ByteList data = new ByteList();
		data.writeUTF8(msg.toString());

		stream.write(getID(), data);
	}

	@Override
	public void readFromBytes(final ByteInput input) {
	}
}
