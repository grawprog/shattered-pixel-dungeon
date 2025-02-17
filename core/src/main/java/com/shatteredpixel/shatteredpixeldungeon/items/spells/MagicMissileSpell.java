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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfStormClouds;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class MagicMissileSpell extends TargetedSpell {
	
	{
		//image = ItemSpriteSheet.MAGIC_MISSILE;
		baseMin = 2;
		baseMax = 8;
		baseMPCost = 10;
	}
	@Override
	public int min(int lvl) {return baseMin + (lvl-1);}
	@Override
	public int max(int lvl){
		return baseMax + 2*lvl;
	}

	@Override
	protected void affectTarget(Ballistica bolt, Hero hero) {
		Char ch = Actor.findChar( bolt.collisionPos );
		if (ch != null) {
			ch.damage(damageRoll(quantity()), this);
			Sample.INSTANCE.play( Assets.Sounds.HIT_MAGIC, 1, Random.Float(0.87f, 1.15f) );
			ch.sprite.burst(0xFFFFFFFF, buffedLvl() / 2 + 2);
		} else {
			Dungeon.level.pressCell(bolt.collisionPos);
		}
	}
	
	@Override
	public int value() {
		//prices of ingredients, divided by output quantity
		return Math.round(quantity * ((60 + 40) / 12f));
	}
	
	public static class Recipe extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe.SimpleRecipe {
		
		{
			inputs =  new Class[]{PotionOfStormClouds.class, ArcaneCatalyst.class};
			inQuantity = new int[]{1, 1};
			
			cost = 4;
			
			output = MagicMissileSpell.class;
			outQuantity = 12;
		}
		
	}
}
