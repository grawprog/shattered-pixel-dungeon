package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;

public class LightWeapon extends MeleeWeapon {
    {
        dexScaleFactor = 5;
        strScaleFactor = 1;
        baseStrReq = 9;
        baseDexReq = 11;
    }


    @Override
    public int getOffhandPenalty(Char owner){
        return owner.DEX() - this.DEXReq(this.level()) + super.getOffhandPenalty(owner);
    }
}
