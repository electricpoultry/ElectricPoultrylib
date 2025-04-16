package io.github.electricpoultry.electricpoultrylib.neoforge;

import net.neoforged.fml.common.Mod;

import io.github.electricpoultry.electricpoultrylib.ElectricPoultryLib;

@Mod(ElectricPoultryLib.MOD_ID)
public final class ElectricPoultryLibNeoForge {
    public ElectricPoultryLibNeoForge() {
        // Run our common setup.
        ElectricPoultryLib.init();
    }
}
