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

package org.marinemc.events;

import org.marinemc.player.Player;

/**
 * Created 2014-12-16 for MarineStandalone
 *
 * @author Citymonstret
 */
public abstract class PlayerEvent extends Event {

    private final Player player;

    public PlayerEvent(final Player player, final String name) {
        this(player, name, false);
    }

    public PlayerEvent(final Player player, final String name, final boolean async) {
        super("player_event:" + name, async);
        this.player = player;
    }

    public Player getPlayer() {
        return this.player;
    }
}