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

package org.marinemc.game;

import org.marinemc.game.player.Player;
import org.marinemc.net.packets.player.PlayerListHeaderPacket;
import org.marinemc.net.packets.player.PlayerListItemPacket;
import org.marinemc.server.Marine;

/**
 * Created 2014-12-05 for MarineStandalone
 *
 * @author Citymonstret
 * @author Fozie
 */
public class TablistManager {

	private static TablistManager instance;

	public TablistManager() {
	}

	public static TablistManager getInstance() {
		if (instance == null)
			instance = new TablistManager();
		return instance;
	}

	public void setHeaderAndFooter(final String header, final String footer,
			final Player player) {
		player.getClient().sendPacket(
				new PlayerListHeaderPacket(header, footer));
	}

	public void addItem(final Player toAdd) {
		for (final Player affected : Marine.getPlayers())
			affected.getClient().sendPacket(
					new PlayerListItemPacket(
							PlayerListItemPacket.Action.ADD_PLAYER, toAdd));
	}

	public void removeItem(final Player toRemove) {
		for (final Player affected : Marine.getPlayers())
			affected.getClient()
					.sendPacket(
							new PlayerListItemPacket(
									PlayerListItemPacket.Action.REMOVE_PLAYER,
									toRemove));
	}

	public void joinList(final Player joined) {
		for (final Player player : Marine.getServer().getPlayers()) {
			if (player.getUUID().equals(joined.getUUID()))
				continue;
			joined.getClient().sendPacket(
					new PlayerListItemPacket(
							PlayerListItemPacket.Action.ADD_PLAYER, player));
		}
	}

	public void setDisplayName(final Player toChange) {
		for (final Player affected : Marine.getPlayers())
			affected.getClient().sendPacket(
					new PlayerListItemPacket(
							PlayerListItemPacket.Action.UPDATE_DISPLAY_NAME,
							toChange));
	}
}
