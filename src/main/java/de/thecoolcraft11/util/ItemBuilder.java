package de.thecoolcraft11.util;

import de.thecoolcraft11.SurvivalUtilities;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;

public class ItemBuilder {
    private final ItemMeta itemMeta;
    private final ItemStack itemStack;

    public ItemBuilder(Material mat) {
        itemStack = new ItemStack(mat);
        itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder setDisplayname(String s) {
        itemMeta.setDisplayName(s);
        return this;
    }

    @SuppressWarnings("unchecked")
    public ItemBuilder setPersistentData(String key, PersistentDataType persistentDataType, Object value) {
        itemMeta.getPersistentDataContainer().set(new NamespacedKey(SurvivalUtilities.getPlugin(SurvivalUtilities.class), key), persistentDataType, value);
        return this;
    }

    public ItemBuilder setItemName(String s) {
        itemMeta.setItemName(s);
        return this;
    }

    public ItemBuilder setDisplayName(Component c) {
        itemMeta.displayName(c);
        return this;
    }

    public ItemBuilder setCount(int count) {
        itemStack.setAmount(count);
        return this;
    }


    public ItemBuilder setLore(String... s) {
        itemMeta.setLore(Arrays.asList(s));
        return this;
    }

    public ItemBuilder setUnbreakable(boolean s) {
        itemMeta.setUnbreakable(s);
        return this;
    }

    public ItemBuilder addItemFlags(ItemFlag... s) {
        itemMeta.addItemFlags(s);
        return this;
    }

    public ItemBuilder setSkullOwner(Player player) {
        SkullMeta skullMeta = (SkullMeta) itemMeta;
        skullMeta.setOwnerProfile(player.getPlayerProfile());
        return this;
    }


    @Override
    public String toString() {
        return "ItemBuilder{" +
                "itemMeta=" + itemMeta +
                ", itemStack=" + itemStack +
                '}';
    }

    public ItemStack build() {
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
