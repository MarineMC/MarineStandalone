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

package com.marine.net.play.clientbound.player;

import com.marine.io.data.ByteData;
import com.marine.net.Packet;
import com.marine.net.PacketOutputStream;
import com.marine.net.States;
import com.marine.net.play.serverbound.player.ServerboundPlayerLookPositionPacket;
import com.marine.util.Location;
import com.marine.util.Position;

import java.io.IOException;

public class ClientboundPlayerLookPositionPacket extends Packet { //TODO Relative positions

    final Location l;
    final Position p;

    public ClientboundPlayerLookPositionPacket(Location l) {
        this.l = l;
        p = null;
    }

    public ClientboundPlayerLookPositionPacket(Position p) {
        this.l = null;
        this.p = p;
    }

    public ClientboundPlayerLookPositionPacket(ServerboundPlayerLookPositionPacket l) {
        this(l.getLocation());
    }

    @Override
    public int getID() {
        return 0x08;
    }

    @Override
    public void writeToStream(PacketOutputStream stream) throws IOException {
        ByteData d = new ByteData();
        if (p == null) {
            d.writeDouble(l.getX());
            d.writeDouble(l.getY());
            d.writeDouble(l.getZ());

            d.writeFloat(l.getYaw());
            d.writeFloat(l.getPitch());
        } else {
            d.writeDouble(p.getX());
            d.writeDouble(p.getY());
            d.writeDouble(p.getZ());

            d.writeFloat(l.getYaw());
            d.writeFloat(l.getPitch());
        }

        d.writeByte((byte) 0);

        stream.write(getID(), d);
    }

    @Override
    public void readFromBytes(ByteData input) {

    }

    @Override
    public States getPacketState() {
        return States.INGAME;
    }
}
