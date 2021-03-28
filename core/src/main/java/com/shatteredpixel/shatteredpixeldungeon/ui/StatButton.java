package com.shatteredpixel.shatteredpixeldungeon.ui;


import com.watabou.noosa.Group;

public abstract class StatButton extends RedButton {
    private static final int BTN_WIDTH  = 12;
    private static final int BTN_HEIGHT	= 12;
    private static final int WIDTH  = 115;

    public StatButton(Group parent, float pos){
        super("+");
        setRect(WIDTH*0.8f, pos-BTN_HEIGHT-4, BTN_WIDTH, BTN_HEIGHT);
    }

    @Override
    protected void onClick(){
        incStat();
    }


    protected abstract void incStat();

}
