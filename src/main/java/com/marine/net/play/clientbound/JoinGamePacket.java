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

package com.marine.net.play.clientbound;

import com.marine.io.data.ByteData;
import com.marine.net.Packet;
import com.marine.net.PacketOutputStream;
import com.marine.net.States;
import com.marine.player.Player;
import com.marine.server.Marine;

import java.io.IOException;

public class JoinGamePacket extends Packet {

    final Player p;

    public JoinGamePacket(Player p) {
        this.p = p;
    }

    @Override
    public int getID() {
        return 0x01;
    }

    @Override
    public void writeToStream(PacketOutputStream stream) throws IOException {
        ByteData d = new ByteData();

        d.writeInt(p.getEntityID());

        d.writeByte(p.getGamemode().getID()); // Gamemode

        d.writeByte(p.getWorld().getDimension().getID()); // Dimension
        d.writeByte(p.getPlayerManager().getServer().getDifficulty().getID()); // Difficulty

        d.writeByte((byte) Marine.getMaxPlayers()); // MaxPlayers
        d.writeUTF8("flat");
        d.writeBoolean(false);

        stream.write(getID(), d);
    }

    @Override
    public void readFromBytes(ByteData input) {
    } //Client Side Only

    @Override
    public States getPacketState() {
        return States.INGAME;
    }
}
