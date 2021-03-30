package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

public class LightWeapon extends MeleeWeapon {
    {
        dexScaleFactor = 5;
        strScaleFactor = 1;
    }

    /* this is not correct need to figure out what's up when more sober
    @Override
    public int getOffhandPenalty(){
        return hero.DEX() - this.DEXReq(this.level()) + super.getOffhandPenalty(this.);
    }*/
}
