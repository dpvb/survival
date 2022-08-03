package dev.dpvb.survival.chests;

import dev.dpvb.survival.Survival;
import dev.dpvb.survival.util.item.ItemGenerator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

public interface LootSource {
    @NotNull List<ItemStack> generateLoot();

    /**
     * Load loot to a collection from a configuration section.
     * <p>
     * Section formats vary; Loot tuples resemble the following format:
     * <pre>
     *     {item_material}:
     *       chance: {chance}
     *       amount: {amount}
     * </pre>
     * where amount is optional; if not specified, it will be 1.
     *
     * @param parent the parent section
     * @param subsection the subsection name
     * @param out the output collection
     */
    static void loadFrom(@NotNull ConfigurationSection parent, @NotNull String subsection, @NotNull Collection<Loot> out) {
        // (Try to) get section
        final var section = parent.getConfigurationSection(subsection);
        if (section == null) {
            // fail gracefully
            return;
        }
        // Iterate keys (material names)
        for (String materialName : section.getKeys(false)) {
            // Attempt to parse Material
            Material material;
            try {
                material = Material.valueOf(materialName.toUpperCase());
            } catch (IllegalArgumentException e) {
                Bukkit.getLogger().severe("An upgrade configuration is incorrect: " + materialName + " is not a valid Material.");
                continue; // skip item and continue parsing
            }

            // Read chance value
            double chance = section.getDouble(materialName + ".chance");

            // Try-read amount; default gracefully to 1
            int amount = section.getInt(materialName + ".amount", 1);

            // Create appropriate ItemStack
            final var item = new ItemGenerator()
                    .setAmount(amount)
                    .build(material);

            // Create Loot object and add it to the collection.
            out.add(new Loot(item, chance));
        }
    }

    /**
     * Load loot to a collection from a config.yml loot subsection.
     * <pre>
     * # in config.yml
     * loot:
     *   {subsection}:
     *     ...
     *     {item_material}:
     *       chance: {chance}
     *       amount: {amount}
     *     ...
     * </pre>
     * where amount is optional; if not specified, it will be 1.
     *
     * @param subsection the subsection name
     * @param out the output collection
     */
    static void loadFromLoot(@NotNull String subsection, @NotNull Collection<Loot> out) {
        loadFrom(Survival.Configuration.getLootSection(), subsection, out);
    }

    /**
     * Generate loot from a collection of Loot objects.
     */
    enum GenerationFormula {
        /**
         * The default formula.
         */
        DEFAULT {
            @Override
            public void generate(Collection<Loot> in, Collection<ItemStack> out) {
                for (Loot loot : in) {
                    double randDouble = ThreadLocalRandom.current().nextDouble();
                    if (randDouble <= loot.chance()) {
                        out.add(loot.item());
                    }
                }
            }
        },
        ;

        /**
         * Generate loot from a collection of Loot objects.
         * <p>
         * The resulting items are added to the output collection.
         *
         * @param in the input collection
         * @param out the output collection
         */
        public abstract void generate(Collection<Loot> in, Collection<ItemStack> out);

        /**
         * Generate a list of loot items from a collection of Loot objects.
         * <p>
         * The supplier is used to create or source the output list. The
         * resulting items are added to this list.
         *
         * @param in an input collection of Loot objects
         * @param listGenerator a list source
         * @return a list of loot items
         * @param <T> the type of the output list
         */
        public <T extends List<ItemStack>> T generateAs(Collection<Loot> in, Supplier<T> listGenerator) {
            T out = listGenerator.get();
            generate(in, out);
            return out;
        }
    }
}
