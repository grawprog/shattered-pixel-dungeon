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


import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.ItemStatusHandler;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public abstract class Spell extends Item {

	public static final String AC_CAST = "CAST";

	private static final HashMap<String, Integer> colors = new HashMap<String, Integer>() {
		{
			put("red", ItemSpriteSheet.SPELL_ORB_RED);
			put("orange",ItemSpriteSheet.SPELL_ORB_ORANGE);
			put("yellow",ItemSpriteSheet.SPELL_ORB_YELLOW);
			put("chartreuse",ItemSpriteSheet.SPELL_ORB_CHARTREUSE);
			put("cyan",ItemSpriteSheet.SPELL_ORB_CYAN);
			put("blue",ItemSpriteSheet.SPELL_ORB_BLUE);
			put("navy blue",ItemSpriteSheet.SPELL_ORB_NAVY);
			put("violet",ItemSpriteSheet.SPELL_ORB_VIOLET);
			put("burgundy",ItemSpriteSheet.SPELL_ORB_BURGUNDY);
			put("black",ItemSpriteSheet.SPELL_ORB_BLACK);
			put("grey",ItemSpriteSheet.SPELL_ORB_GREY);
			put("white",ItemSpriteSheet.SPELL_ORB_WHITE);
			put("green",ItemSpriteSheet.SPELL_ORB_GREEN);
			put("scarlet",ItemSpriteSheet.SPELL_ORB_SCARLET);

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

	protected static ItemStatusHandler<Spell> handler;

	protected String color;

	@SuppressWarnings("unchecked")
	public static void initColors() {
		handler = new ItemStatusHandler<>( (Class<? extends Spell>[])Generator.Category.SPELL.classes, colors );
	}

	public static void save( Bundle bundle ) {
		handler.save( bundle );
	}

	public static void saveSelectively( Bundle bundle, ArrayList<Item> items ) {
		ArrayList<Class<?extends Item>> classes = new ArrayList<>();
		for (Item i : items){
			if (i instanceof Spell){
				if (!classes.contains(i.getClass())){
					classes.add(i.getClass());
				}
			}
		}
		handler.saveClassesSelectively( bundle, classes );
	}

	@SuppressWarnings("unchecked")
	public static void restore( Bundle bundle ) {
		handler = new ItemStatusHandler<>( (Class<? extends Spell>[]) Generator.Category.SPELL.classes, colors, bundle );
	}

	public Spell() {
		super();
		reset();
	}

	//anonymous Spells are always IDed, do not affect ID status,
	//and their sprite is replaced by a placeholder if they are not known,
	//useful for items that appear in UIs, or which are only spawned for their effects
	protected boolean anonymous = false;
	public void anonymize(){
		if (!isKnown()) image = ItemSpriteSheet.SPELL_HOLDER;
		anonymous = true;
	}

	@Override
	public void reset(){
		super.reset();
		if (handler != null && handler.contains(this)) {
			image = handler.image(this);
			color = handler.label(this);
		}

	}

	protected abstract void onCast(Hero hero );

	public boolean isKnown() {
		return anonymous || (handler != null && handler.isKnown( this ));
	}

	public void setKnown() {
		if (!anonymous) {
			if (!isKnown()) {
				handler.know(this);
				updateQuickslot();

			}

			if (Dungeon.hero.isAlive()) {
				Catalog.setSeen(getClass());
			}
		}
	}

	@Override
	public Item identify() {
		super.identify();

		if (!isKnown()) {
			setKnown();
		}
		return this;
	}

	@Override
	public String name() {
		return isKnown() ? super.name() : Messages.get(this, color);
	}

	@Override
	public String info() {
		return isKnown() ? desc() : Messages.get(this, "unknown_desc");
	}

	@Override
	public boolean isIdentified() {
		return isKnown();
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	public static HashSet<Class<? extends Spell>> getKnown() {
		return handler.known();
	}

	public static HashSet<Class<? extends Spell>> getUnknown() {
		return handler.unknown();
	}

	public static boolean allKnown() {
		return handler.known().size() == Generator.Category.POTION.classes.length;
	}


}
