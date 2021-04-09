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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfFuror;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfSharpshooting;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.watabou.utils.Random;

import java.util.ArrayList;

public abstract class RangedWeapon extends MeleeWeapon {

	public static final String AC_AMMO = "CHOOSE AMMO";
	public static final String AC_UNAMMO = "REMOVE AMMO";
	
	public static final String AC_SHOOT		= "SHOOT";
	
	{


		defaultAction = AC_EQUIP;


		handType = HandType.TWOHAND;

		usesTargeting = true;

	}
	
	public boolean sniperSpecial = false;
	public float sniperSpecialBonusDamage = 0f;
	public MissileWeapon ammo;
	public MissileWeapon.AmmoType ammoType = MissileWeapon.AmmoType.ARROW;

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if(handType == HandType.TWOHAND) {
			actions.remove(AC_OFFHAND);
		}
		if(isEquipped(hero)) {
			actions.remove(AC_EQUIP);
			actions.add(AC_SHOOT);
			//actions.add(AC_UNEQUIP);
		}
		actions.add(AC_AMMO);
		return actions;
	}
	
	@Override
	public void execute(Hero hero, String action) {
		
		super.execute(hero, action);
		
		if (action.equals(AC_SHOOT)) {
			
			curUser = hero;
			curItem = this;
			GameScene.selectCell( shooter );
			
		}
		if (action.equals(AC_EQUIP)) {
			defaultAction = AC_SHOOT;
		}
		if (action.equals(AC_UNEQUIP)) {
			defaultAction = AC_EQUIP;
		}
		if (action.equals(AC_AMMO)){
			doAmmo();
		}
		if (action.equals(AC_UNAMMO)){
			ammo = null;
		}
	}

	protected void doAmmo(){
		String inventoryTitle = Messages.get(this, "inv_title");
		WndBag.Mode mode = WndBag.Mode.AMMO_NONE;
		switch (ammoType){
			case NONE:
				mode = WndBag.Mode.AMMO_NONE;
				break;
			case STONE:
				mode = WndBag.Mode.AMMO_STONE;
				break;
			case SPEAR:
				mode = WndBag.Mode.AMMO_SPEAR;
				break;
			case DART:
				mode = WndBag.Mode.AMMO_DART;
				break;
			case ARROW:
				mode = WndBag.Mode.AMMO_ARROW;
				break;
			case BOLT:
				mode = WndBag.Mode.AMMO_BOLT;
				break;
			case BULLET:
				mode = WndBag.Mode.AMMO_BULLET;
				break;
			case SHELL:
				mode = WndBag.Mode.AMMO_SHELL;
				break;
		}
		GameScene.selectItem(itemSelector, mode, inventoryTitle);
	}

	protected abstract void onItemSelected( MissileWeapon item );

	protected static WndBag.Listener itemSelector = new WndBag.Listener() {
		@Override
		public void onSelect( Item item ) {

			//FIXME this safety check shouldn't be necessary
			//it would be better to eliminate the curItem static variable.
			if (!(curItem instanceof RangedWeapon)){
				return;
			}

			if (item instanceof MissileWeapon && item !=null) {

				((RangedWeapon) curItem).ammo = ((MissileWeapon) item);

			} else{
				curItem.collect( curUser.belongings.backpack );
			}
		}
	};

	@Override
	public String info() {
		String info = desc();
		
		info += "\n\n" + Messages.get( RangedWeapon.class, "stats",
				Math.round(augment.damageFactor(min())),
				Math.round(augment.damageFactor(max())),
				STRReq());
		
		if (STRReq() > Dungeon.hero.STR()) {
			info += " " + Messages.get(Weapon.class, "too_heavy");
		} else if (Dungeon.hero.STR() > STRReq()){
			info += " " + Messages.get(Weapon.class, "excess_str", Dungeon.hero.STR() - STRReq());
		}
		
		switch (augment) {
			case SPEED:
				info += "\n\n" + Messages.get(Weapon.class, "faster");
				break;
			case DAMAGE:
				info += "\n\n" + Messages.get(Weapon.class, "stronger");
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
		
		info += "\n\n" + Messages.get(MissileWeapon.class, "distance");
		
		return info;
	}
	
	@Override
	public int STRReq(int lvl) {
		return STRReq(1, lvl); //tier 1
	}

	@Override
	public int DEXReq(int lvl){
		return DEXReq(1, lvl); //1 less str than normal for their tier
	}
	
	@Override
	public int min(int lvl) {
		return 1 + Dungeon.hero.lvl/5
				+ RingOfSharpshooting.levelDamageBonus(Dungeon.hero)
				+ (curseInfusionBonus ? 1 : 0);
	}
	
	@Override
	public int max(int lvl) {
		return 6 + (int)(Dungeon.hero.lvl/2.5f)
				+ 2*RingOfSharpshooting.levelDamageBonus(Dungeon.hero)
				+ (curseInfusionBonus ? 2 : 0);
	}

	//no penalty for offhand bows as bows can't be wielded offhand
	@Override
	public int offMin(Char owner) {
		return min();
	}

	@Override
	public int offMin(Char owner, int lvl) {
		return  min(lvl);
	}

	@Override
	public int offMax(Char owner) {
		return max();
	}

	@Override
	public int offMax(Char owner, int lvl) {
		return  max(lvl);
	}

	@Override
	public int targetingPos(Hero user, int dst) {
		return 1;//knockArrow().targetingPos(user, dst);
	}
	
	private int targetPos;
	
	@Override
	public int damageRoll(Char owner) {
		int damage = augment.damageFactor(super.damageRoll(owner));
		
		if (owner instanceof Hero) {
			int exStr = ((Hero)owner).STR() - STRReq();
			if (exStr > 0) {
				damage += Random.IntRange( 0, exStr );
			}
		}

		if (sniperSpecial){
			damage = Math.round(damage * (1f + sniperSpecialBonusDamage));

			switch (augment){
				case NONE:
					damage = Math.round(damage * 0.667f);
					break;
				case SPEED:
					damage = Math.round(damage * 0.5f);
					break;
				case DAMAGE:
					//as distance increases so does damage, capping at 3x:
					//1.20x|1.35x|1.52x|1.71x|1.92x|2.16x|2.43x|2.74x|3.00x
					int distance = Dungeon.level.distance(owner.pos, targetPos) - 1;
					float multiplier = Math.min(3f, 1.2f * (float)Math.pow(1.125f, distance));
					damage = Math.round(damage * multiplier);
					break;
			}
		}
		
		return damage;
	}
	
	@Override
	public float speedFactor(Char owner) {
		if (sniperSpecial){
			switch (augment){
				case NONE: default:
					return 0f;
				case SPEED:
					return 1f * RingOfFuror.attackDelayMultiplier(owner);
				case DAMAGE:
					return 2f * RingOfFuror.attackDelayMultiplier(owner);
			}
		} else {
			return super.speedFactor(owner);
		}
	}
	
	@Override
	public int level() {
		return (Dungeon.hero == null ? 0 : Dungeon.hero.lvl/5) + (curseInfusionBonus ? 1 : 0);
	}

	@Override
	public int buffedLvl() {
		//level isn't affected by buffs/debuffs
		return level();
	}

	
	protected CellSelector.Listener shooter = new CellSelector.Listener() {
		@Override
		public void onSelect( Integer target ) {
			if (target != null) {
				//knockArrow().cast(curUser, target);
			}
		}
		@Override
		public String prompt() {
			return Messages.get(RangedWeapon.class, "prompt");
		}
	};
}
