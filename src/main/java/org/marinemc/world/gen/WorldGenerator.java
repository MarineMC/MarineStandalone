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

package org.marinemc.world.gen;

import java.util.Random;

import org.marinemc.util.Position;
import org.marinemc.world.Dimension;
import org.marinemc.world.World;
import org.marinemc.world.chunk.Chunk;
import org.marinemc.world.chunk.ChunkPos;

/**
 * @author Fozie
 */
public abstract class WorldGenerator {

	public final static ChunkPopulator[] NO_POPULATION = new ChunkPopulator[] { new ChunkPopulator() {
		@Override
		public void populate(final Chunk c) {
		}
	} };

	protected World world;

	private ChunkPopulator[] populators;

	public WorldGenerator(final ChunkPopulator[] populators) {
		if (populators != null)
			this.populators = populators;
		else
			this.populators = NO_POPULATION.clone();
	}

	public void setGenerationWorld(final World w) {
		world = w;
	}

	public Chunk[] generateRegion(final int x, final int y) {
		return generateRegion(x, y, 16, 16);
	}

	public abstract LevelType getLevelType();

	public Chunk[] generateRegion(final int x, final int y, final int width,
			final int height) {
		final Chunk[] r = new Chunk[width * height];
		int i = 0;
		for (int xx = -(width / 2); xx < width / 2; xx++)
			for (int yy = -(width / 2); yy < width / 2; yy++) {
				r[i] = generateChunk(new ChunkPos(x + xx, y + yy));
				i++;
			}
		return r;
	}

	public abstract Dimension getDimension();

	public void populateChunk(final Chunk c) {
		if (populators == null)
			return;
		for (final ChunkPopulator pop : populators)
			pop.populate(c);
	}

	public Chunk generateChunk(final ChunkPos pos) {
		final Chunk c = generateChunkTerrain(pos);
		populateChunk(c);
		return c;
	}

	public abstract Chunk generateChunkTerrain(final ChunkPos pos);

	public abstract Position getSafeSpawnPoint();

	public Random getRandom() {
		if (world != null)
			return world.getRandom();
		return new Random();
	}

}
