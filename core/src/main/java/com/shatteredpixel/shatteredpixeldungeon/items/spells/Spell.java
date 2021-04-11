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

package com.shatteredpixel.shatteredpixeldungeon.items.spells;


import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class Spell extends Item {

	public static final String AC_CAST = "CAST";

	private static final HashMap<String, Integer> colors = new HashMap<String, Integer>() {
		{
			put("red", ItemSpriteSheet.POTION_CRIMSON);
			put("orange",ItemSpriteSheet.POTION_AMBER);
			put("yellow",ItemSpriteSheet.POTION_GOLDEN);
			put("lime",ItemSpriteSheet.POTION_JADE);
			put("aquamarine",ItemSpriteSheet.POTION_TURQUOISE);
			put("blue",ItemSpriteSheet.POTION_AZURE);
			put("navy",ItemSpriteSheet.POTION_INDIGO);
			put("violet",ItemSpriteSheet.POTION_MAGENTA);
			put("ochre",ItemSpriteSheet.POTION_BISTRE);
			put("black",ItemSpriteSheet.POTION_CHARCOAL);
			put("grey",ItemSpriteSheet.POTION_SILVER);
			put("white",ItemSpriteSheet.POTION_IVORY);
			put("green",ItemSpriteSheet.POTION_EMERALD);

		}
	};
	
	{
		stackable = true;
		defaultAction = AC_CAST;
	}
	protected static int baseMPCost = 1;

	@Override
	public ArrayList<String> actions(Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add( AC_CAST );
		return actions;
	}
	
	@Override
	public void execute( final Hero hero, String action ) {
		
		super.execute( hero, action );
		
		if (action.equals( AC_CAST )) {
			
			if (curUser.buff(MagicImmune.class) != null){
				GLog.w( Messages.get(this, "no_magic") );
				return;
			}
			
			onCast( hero );
			
		}
	}
	
	@Override
	public boolean isIdentified() {
		return true;
	}
	
	@Override
	public boolean isUpgradable() {
		return false;
	}

	public int getMPCost(Hero hero){
		return baseMPCost + (quantity()-1) - (hero.WIS() - 10);
	}

	public boolean checkMP(Hero hero){
		if(hero.MP - getMPCost(hero) >= 0){
			return true;
		}
		else{
			return false;
		}
	}

	public void drainMP(Hero hero){
			hero.decMP(getMPCost(hero));
	}



	protected abstract void onCast(Hero hero );
	
}
