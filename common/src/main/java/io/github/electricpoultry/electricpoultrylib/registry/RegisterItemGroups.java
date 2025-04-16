package io.github.electricpoultry.electricpoultrylib.registry;

import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class RegisterItemGroups {
    private final DeferredRegister<CreativeModeTab> TABS;
    private final List<Supplier<Item>> items = new ArrayList<>();
    private final String tabName;
    private final String modId;
    private final Supplier<ItemStack> icon;

    public RegisterItemGroups(String modId, String tabName, Supplier<ItemStack> icon) {
        this.modId = modId;
        this.tabName = tabName;
        this.icon = icon;
        this.TABS = DeferredRegister.create(modId, Registries.CREATIVE_MODE_TAB);
    }

    public void addItem(Supplier<Item> item) {
        items.add(item);
    }

    public void register() {
        TABS.register(tabName, () -> CreativeModeTab.builder(
                        CreativeModeTab.Row.TOP,
                        0)
                .title(Component.translatable("creativetab." + modId + "." + tabName))
                .icon(icon)
                .displayItems((parameters, output) -> {
                    items.forEach(item -> output.accept(item.get()));
                })
                .build());
    }

    public DeferredRegister<CreativeModeTab> getTabs() {
        return TABS;
    }
}
