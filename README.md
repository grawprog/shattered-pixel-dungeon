# Shattered Pixel Dungeon Hack

A Roguelike RPG, with randomly generated levels, items, enemies, and traps! Based on the [source code of Pixel Dungeon](https://github.com/00-Evan/pixel-dungeon-gradle), by [Watabou](https://www.watabou.ru). This mod uses Shattered Pixel Dungeon 0.9.2b as a base. Check pdmod for details on changes to be made.

## Mod Goals and Changes

The overall goal of this mod is to increase the variety of strategies available to players in their journey the dungeon while increasing the uniqueness in play style of each character class. This should be achieved by adding more depth to both character progression and the item system. Increasing variety, utility and randomness of items should force players to play each game more situationally and adapt their character progression around the items found. There should be multiple viable strategies for any given situation, but the overall balance of the game should remain similar to shattered pixel dungeon.

This mod was inspired by changes I saw were possible in several other PD mods including [Powered Pixel Dungeon](https://github.com/Smujb/powered-pixel-dungeon), [ReMixed Dungeon](https://github.com/NYRDS/remixed-dungeon), [Re-ReMixed Dungen](https://github.com/QuasiStellar/Re-Remixed_Dungeon) and [Moonshine PD](https://bitbucket.org/juh9870/moonshine). It's also inspired by [Nethack](https://www.nethack.org/), hence the name of this mod. I want to try and recreate a bit of that depth, randomness and situational play style that makes NetHack so awesome and addictive after all this time within Pixel Dungeon. 

#### Partially Implemented Changes:
  * Working Stat system that affects hero progression, combat, and most aspects of the game.
  * An offhand weapon slot that allows for dual wielding weapons or using shields with weapons.
  * Rework of weapon system to work with stat system. Addition of hand system. Weapons are now either, small, one handed, two handed or come as a pair.
  * Equipable ranged weapons that use ammo
  * Magic system that uses MP.
  * Spells reworked to use MP. Spells now show up as randomized coloured orbs similar to potions and can be found in the dungeon.
  * Spells are not upgraded directly but by finding multiples of the same spell orb. Each increase in stack increases damage done with spells and increases MP cost or decreases MP cost for utility spells.

 *This is a work in progress not yet in a playable state.* 

Shattered Pixel Dungeon Hack currently compiles for Android and desktop platforms.

Note that **this repository does not accept pull requests!** The code here is provided in hopes that others may find it useful for their own projects, not to allow community contribution. Issue reports of all kinds (bug reports, feature requests, etc.) are welcome.

If you'd like to work with the code, you can find the following guides in `/docs`:
- [Compiling for Android.](docs/getting-started-android.md)
    - **[If you plan to distribute on Google Play please read the end of this guide.](docs/getting-started-android.md#distributing-your-apk)**
- [Compiling for desktop platforms.](docs/getting-started-desktop.md)
- [Recommended changes for making your own mod.](docs/recommended-changes.md)
