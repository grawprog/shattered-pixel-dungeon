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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.utils.Random;

public class MeleeWeapon extends Weapon {

	protected int baseMin = 1;
	protected int baseMax = 6;

	public int tier;


	@Override
	public int min(int lvl) {
		return  baseMin +  //base
				lvl;    //level scaling
	}
	public int getOffhandPenalty(Char owner){
		return super.getOffhandPenalty(owner);
	}

	@Override
	public int offMin(Char owner, int lvl) {
		int offmin = this.min(lvl);
		offmin -= getOffhandPenalty(owner);
		if (offmin < 0) {
			offmin = 0;
		}
		return offmin;
	}
	@Override
	public int max(int lvl) {
		return  baseMax +    //base
				lvl*lvlScaleFactor;   //level scaling
	}
	@Override
	public int offMax(Char owner, int lvl) {
		int offmax = this.max(lvl);
		offmax -= getOffhandPenalty(owner)*strScaleFactor-getOffhandPenalty(owner);
		if (offmax < 0) {
			offmax = 0;
		}
		return offmax;
	}
	public int STRReq(int lvl){
		return STRReq(baseStrReq, lvl);
	}
	public int DEXReq(int lvl){
		return DEXReq(baseDexReq, lvl);
	}
	@Override
	public int damageRoll(Char owner) {
		int damage = augment.damageFactor(super.damageRoll( owner ));
		if (owner instanceof Hero) {
			int exStr = ((Hero)owner).STR() - STRReq();
			if (exStr > 0) {
				damage += Random.IntRange( 0, exStr );
			}
		}
		
		return damage;
	}
	
	@Override
	public String info() {

		String info = desc();

		if (levelKnown) {
			if (this == Dungeon.hero.belongings.offhand) {
				info += "\n\n" + Messages.get(MeleeWeapon.class, "offhand_stats_known",  augment.damageFactor(offMin(Dungeon.hero)), augment.damageFactor(offMax(Dungeon.hero)), STRReq()+getOffhandPenalty(Dungeon.hero), DEXReq()+getOffhandPenalty(Dungeon.hero));
				if (STRReq() > Dungeon.hero.STR()+offhandPenalty) {
					info += " " + Messages.get(Weapon.class, "offhand_too_heavy");
				} else if (Dungeon.hero.STR() > STRReq()+offhandPenalty){
					info += " " + Messages.get(Weapon.class, "offhand_excess_str", Dungeon.hero.STR() - (STRReq()+getOffhandPenalty(Dungeon.hero)));
				}
				if (DEXReq() > Dungeon.hero.DEX()+offhandPenalty) {
					info += " " + Messages.get(Weapon.class, "offhand_too_slow");
				} else if (Dungeon.hero.DEX() > DEXReq()+offhandPenalty){
					info += " " + Messages.get(Weapon.class, "offhand_excess_dex", Dungeon.hero.DEX() - (DEXReq()+getOffhandPenalty(Dungeon.hero)));
				}
			}
			else {
				info += "\n\n" + Messages.get(MeleeWeapon.class, "stats_known", augment.damageFactor(min()), augment.damageFactor(max()), STRReq(), DEXReq());
				if (STRReq() > Dungeon.hero.STR()) {
					info += " " + Messages.get(Weapon.class, "too_heavy");
				} else if (Dungeon.hero.STR() > STRReq()) {
					info += " " + Messages.get(Weapon.class, "excess_str", Dungeon.hero.STR() - STRReq());
				}
				if (DEXReq() > Dungeon.hero.DEX()) {
					info += " " + Messages.get(Weapon.class, "too_slow");
				} else if (Dungeon.hero.DEX() > DEXReq()) {
					info += " " + Messages.get(Weapon.class, "excess_dex", Dungeon.hero.DEX() - DEXReq());
				}
			}
		} else {
			if (this == Dungeon.hero.belongings.offhand){
				info += "\n\n" + Messages.get(MeleeWeapon.class, "offhand_stats_unknown", offMin(Dungeon.hero, 0), offMax(Dungeon.hero, 0), STRReq(0)+getOffhandPenalty(Dungeon.hero), DEXReq(0)+getOffhandPenalty(Dungeon.hero));
				if (STRReq(0) > Dungeon.hero.STR()) {
					info += " " + Messages.get(MeleeWeapon.class, "offhand_probably_too_heavy");
				}
				if (DEXReq(0) > Dungeon.hero.DEX()) {
					info += " " + Messages.get(MeleeWeapon.class, "offhand_probably_too_slow");
				}
			}
			else {
				info += "\n\n" + Messages.get(MeleeWeapon.class, "stats_unknown", min(0), max(0), STRReq(0), DEXReq(0));
				if (STRReq(0) > Dungeon.hero.STR()) {
					info += " " + Messages.get(MeleeWeapon.class, "probably_too_heavy");
				}
				if (DEXReq(0) > Dungeon.hero.DEX()) {
					info += " " + Messages.get(MeleeWeapon.class, "probably_too_slow");
				}
			}
		}

		String statsInfo = statsInfo();
		if (!statsInfo.equals("")) info += "\n\n" + statsInfo;

		switch (augment) {
			case SPEED:
				info += " " + Messages.get(Weapon.class, "faster");
				break;
			case DAMAGE:
				info += " " + Messages.get(Weapon.class, "stronger");
				break;
			case NONE:
		}

		if (enchantment != null && (cursedKnown || !enchantment.curse())){
			info += "\n\n" + Messages.get(Weapon.class, "enchanted", enchantment.name());
			info += " " + Messages.get(enchantment, "desc");
		}

		if (cursed && isEquipped( Dungeon.hero )) {
			info += "\n\n" + Messages.get(Weapon.class, "cursed_worn");
		} else if (cursedKnown && cursed) {
			info += "\n\n" + Messages.get(Weapon.class, "cursed");
		} else if (!isIdentified() && cursedKnown){
			info += "\n\n" + Messages.get(Weapon.class, "not_cursed");
		}
		
		return info;
	}
	
	public String statsInfo(){
		return Messages.get(this, "stats_desc");
	}
	
	@Override
	public int value() {
		int price = 20 * tier;
		if (hasGoodEnchant()) {
			price *= 1.5;
		}
		if (cursedKnown && (cursed || hasCurseEnchant())) {
			price /= 2;
		}
		if (levelKnown && level() > 0) {
			price *= (level() + 1);
		}
		if (price < 1) {
			price = 1;
		}
		return price;
	}

}
