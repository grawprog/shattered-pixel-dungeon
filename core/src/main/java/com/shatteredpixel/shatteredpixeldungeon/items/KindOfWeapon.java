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

package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

abstract public class KindOfWeapon extends EquipableItem {
	
	protected static final float TIME_TO_EQUIP = 1f;

	public enum HandType{
		SMALL, //one hand only, offhand weapon can be equipped
		ONEHAND, //can be used on handed or two handed for bonus dmg, offhand weapon can be equipped
		TWOHAND, //can only be used two handed. equipping offhand weapon unequips
		PAIR, //can only be used two handed. Does 2x min-max dmg with no offhand penalty. unequipped if offhand weapon equipped
		RANGED, //can only be used two handed. can only shoot if appropiate ammo equipped offhand
		AMMO; //can only be equipped in offhand slot. consumed when firing ranged weapons
	}

	public HandType handType = HandType.ONEHAND;

	protected String hitSound = Assets.Sounds.HIT;
	protected float hitSoundPitch = 1f;
	//public boolean offhand = false;
	protected int offhandPenalty = 2;

	@Override
	public ArrayList<String> actions(Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		//actions.add( isEquipped( hero ) ? AC_UNEQUIP : AC_EQUIP);
		if (!isEquipped(hero)){
			if(handType != HandType.PAIR || handType != HandType.TWOHAND) {
				actions.add(AC_OFFHAND);
			}
			if(handType == HandType.PAIR || handType == HandType.TWOHAND){
				actions.remove(AC_OFFHAND);
			}
		}
		return actions;
	}

	public int getOffhandPenalty(Char owner){
		return offhandPenalty;
	}

	@Override
	public void execute( Hero hero, String action ) {

		super.execute( hero, action );
		if (action.equals( AC_OFFHAND )) {
			//In addition to equipping itself, item reassigns itself to the quickslot
			//This is a special case as the item is being removed from inventory, but is staying with the hero.
			int slot = Dungeon.quickslot.getSlot( this );
			doOffhand(hero);
			if (slot != -1) {
				Dungeon.quickslot.setSlot( slot, this );
				updateQuickslot();
			}
		}
	}
	
	@Override
	public boolean isEquipped( Hero hero ) {
		return hero.belongings.weapon == this || hero.belongings.offhand == this || hero.belongings.stashedWeapon == this;
	}
	
	@Override
	public boolean doEquip( Hero hero ) {

		detachAll( hero.belongings.backpack );
		
		if (hero.belongings.weapon == null || hero.belongings.weapon.doUnequip( hero, true )) {
			
			hero.belongings.weapon = this;
			activate( hero );
			Talent.onItemEquipped(hero, this);
			updateQuickslot();
			
			cursedKnown = true;
			if (cursed) {
				equipCursed( hero );
				GLog.n( Messages.get(KindOfWeapon.class, "equip_cursed") );
			}
			
			hero.spendAndNext( TIME_TO_EQUIP );
			return true;
			
		} else {
			
			collect( hero.belongings.backpack );
			return false;
		}
	}

	public boolean doOffhand( Hero hero ) {

		detachAll( hero.belongings.backpack );

		if (hero.belongings.offhand == null || hero.belongings.offhand.doUnequip( hero, true )) {

			hero.belongings.offhand = this;
			if(hero.belongings.weapon.handType == HandType.PAIR || hero.belongings.weapon.handType == HandType.TWOHAND){
				hero.belongings.weapon = null;
			}
			activate( hero );
			Talent.onItemEquipped(hero, this);
			updateQuickslot();

			cursedKnown = true;
			if (cursed) {
				equipCursed( hero );
				GLog.n( Messages.get(KindOfWeapon.class, "equip_cursed") );
			}
		//	this.offhand = true;
			hero.spendAndNext( TIME_TO_EQUIP );
			return true;

		} else {

			collect( hero.belongings.backpack );
			return false;
		}
	}

	@Override
	public boolean doUnequip( Hero hero, boolean collect, boolean single ) {
		if (super.doUnequip( hero, collect, single )) {
			if (this == hero.belongings.offhand){
				hero.belongings.offhand = null;
		//		this.offhand = false;
			}
			else {
				hero.belongings.weapon = null;

			}
			return true;
		} else {

			return false;

		}
	}

	public int min(){
		return min(buffedLvl());
	}

	public int max(){
		return max(buffedLvl());
	}

	abstract public int min(int lvl);
	abstract public int max(int lvl);

	public int offMin(Char owner){
		return offMin(owner, buffedLvl());
	}

	public int offMax(Char owner){
		return offMax(owner, buffedLvl());
	}

	abstract public int offMin(Char owner, int lvl);
	abstract public int offMax(Char owner, int lvl);

	public int damageRoll( Char owner ) {
		if (this == owner.belongings.offhand){
			return Random.NormalIntRange( offMin(owner), offMax(owner) );
		}
		return Random.NormalIntRange( min(), max() );
	}
	
	public float accuracyFactor( Char owner ) {
		return 1f;
	}
	
	public float speedFactor( Char owner ) {
		return 1f;
	}

	public int reachFactor( Char owner ){
		return 1;
	}
	
	public boolean canReach( Char owner, int target){
		if (Dungeon.level.distance( owner.pos, target ) > reachFactor(owner)){
			return false;
		} else {
			boolean[] passable = BArray.not(Dungeon.level.solid, null);
			for (Char ch : Actor.chars()) {
				if (ch != owner) passable[ch.pos] = false;
			}
			
			PathFinder.buildDistanceMap(target, passable, reachFactor(owner));
			
			return PathFinder.distance[owner.pos] <= reachFactor(owner);
		}
	}

	public int defenseFactor( Char owner ) {
		return 0;
	}
	
	public int proc( Char attacker, Char defender, int damage ) {
		return damage;
	}

	public void hitSound( float pitch ){
		Sample.INSTANCE.play(hitSound, 1, pitch * hitSoundPitch);
	}
	
}
