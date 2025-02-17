/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2021 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Sai extends MeleeWeapon {

	{
		image = ItemSpriteSheet.SAI;
		hitSound = Assets.Sounds.HIT_STAB;
		hitSoundPitch = 1.3f;
		handType = HandType.PAIR;
		baseMax = 4;
		baseDexReq = 12;
		strScaleFactor = 0.5f;
		dexScaleFactor = 2;

		tier = 1;
		DLY = 0.5f; //2x speed
		offhandPenalty = 0;
	}

	@Override
	public int max(int lvl) {
		return  Math.round(baseMax * dexScaleFactor + Dungeon.hero.DEX() - baseDexReq + Dungeon.hero.STR() - baseStrReq) +     //10 base, down from 20
				lvl*Math.round(lvlScaleFactor);  //+2 per level, down from +4
	}

}
