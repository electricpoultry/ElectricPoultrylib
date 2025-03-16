package io.github.coolman4567.xboxmanlib.util;

import com.mojang.serialization.MapCodec;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.advancements.critereon.ItemSubPredicate;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.numbers.NumberFormatType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.StatType;
import net.minecraft.util.valueproviders.FloatProviderType;
import net.minecraft.util.valueproviders.IntProviderType;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.animal.CatVariant;
import net.minecraft.world.entity.animal.FrogVariant;
import net.minecraft.world.entity.animal.WolfVariant;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentLocationBasedEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.minecraft.world.item.enchantment.providers.EnchantmentProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.DecoratedPotPattern;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.PositionSourceType;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.featuresize.FeatureSizeType;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.rootplacers.RootPlacerType;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPreset;
import net.minecraft.world.level.levelgen.heightproviders.HeightProviderType;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasBinding;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosRuleTestType;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifierType;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.providers.nbt.LootNbtProviderType;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.score.LootScoreProviderType;

public class ModRegistries {
    public static final ModResourceLocation ROOT_REGISTRY_NAME = ModResourceLocation.withDefaultNamespace("root");
    public static final ModResourceKey<Registry<Activity>> ACTIVITY = createRegistryKey("activity");
    public static final ModResourceKey<Registry<Attribute>> ATTRIBUTE = createRegistryKey("attribute");
    public static final ModResourceKey<Registry<BannerPattern>> BANNER_PATTERN = createRegistryKey("banner_pattern");
    public static final ModResourceKey<Registry<MapCodec<? extends BiomeSource>>> BIOME_SOURCE = createRegistryKey("worldgen/biome_source");
    public static final ModResourceKey<Registry<Block>> BLOCK = createRegistryKey("block");
    public static final ModResourceKey<Registry<MapCodec<? extends Block>>> BLOCK_TYPE = createRegistryKey("block_type");
    public static final ModResourceKey<Registry<BlockEntityType<?>>> BLOCK_ENTITY_TYPE = createRegistryKey("block_entity_type");
    public static final ModResourceKey<Registry<BlockPredicateType<?>>> BLOCK_PREDICATE_TYPE = createRegistryKey("block_predicate_type");
    public static final ModResourceKey<Registry<BlockStateProviderType<?>>> BLOCK_STATE_PROVIDER_TYPE = createRegistryKey("worldgen/block_state_provider_type");
    public static final ModResourceKey<Registry<WorldCarver<?>>> CARVER = createRegistryKey("worldgen/carver");
    public static final ModResourceKey<Registry<CatVariant>> CAT_VARIANT = createRegistryKey("cat_variant");
    public static final ModResourceKey<Registry<WolfVariant>> WOLF_VARIANT = createRegistryKey("wolf_variant");
    public static final ModResourceKey<Registry<MapCodec<? extends ChunkGenerator>>> CHUNK_GENERATOR = createRegistryKey("worldgen/chunk_generator");
    public static final ModResourceKey<Registry<ChunkStatus>> CHUNK_STATUS = createRegistryKey("chunk_status");
    public static final ModResourceKey<Registry<ArgumentTypeInfo<?, ?>>> COMMAND_ARGUMENT_TYPE = createRegistryKey("command_argument_type");
    public static final ModResourceKey<Registry<CreativeModeTab>> CREATIVE_MODE_TAB = createRegistryKey("creative_mode_tab");
    public static final ModResourceKey<Registry<ModResourceLocation>> CUSTOM_STAT = createRegistryKey("custom_stat");
    public static final ModResourceKey<Registry<DamageType>> DAMAGE_TYPE = createRegistryKey("damage_type");
    public static final ModResourceKey<Registry<MapCodec<? extends DensityFunction>>> DENSITY_FUNCTION_TYPE = createRegistryKey("worldgen/density_function_type");
    public static final ModResourceKey<Registry<MapCodec<? extends EnchantmentEntityEffect>>> ENCHANTMENT_ENTITY_EFFECT_TYPE = createRegistryKey(
            "enchantment_entity_effect_type"
    );
    public static final ModResourceKey<Registry<MapCodec<? extends LevelBasedValue>>> ENCHANTMENT_LEVEL_BASED_VALUE_TYPE = createRegistryKey(
            "enchantment_level_based_value_type"
    );
    public static final ModResourceKey<Registry<MapCodec<? extends EnchantmentLocationBasedEffect>>> ENCHANTMENT_LOCATION_BASED_EFFECT_TYPE = createRegistryKey(
            "enchantment_location_based_effect_type"
    );
    public static final ModResourceKey<Registry<MapCodec<? extends EnchantmentProvider>>> ENCHANTMENT_PROVIDER_TYPE = createRegistryKey(
            "enchantment_provider_type"
    );
    public static final ModResourceKey<Registry<MapCodec<? extends EnchantmentValueEffect>>> ENCHANTMENT_VALUE_EFFECT_TYPE = createRegistryKey(
            "enchantment_value_effect_type"
    );
    public static final ModResourceKey<Registry<EntityType<?>>> ENTITY_TYPE = createRegistryKey("entity_type");
    public static final ModResourceKey<Registry<Feature<?>>> FEATURE = createRegistryKey("worldgen/feature");
    public static final ModResourceKey<Registry<FeatureSizeType<?>>> FEATURE_SIZE_TYPE = createRegistryKey("worldgen/feature_size_type");
    public static final ModResourceKey<Registry<FloatProviderType<?>>> FLOAT_PROVIDER_TYPE = createRegistryKey("float_provider_type");
    public static final ModResourceKey<Registry<Fluid>> FLUID = createRegistryKey("fluid");
    public static final ModResourceKey<Registry<FoliagePlacerType<?>>> FOLIAGE_PLACER_TYPE = createRegistryKey("worldgen/foliage_placer_type");
    public static final ModResourceKey<Registry<FrogVariant>> FROG_VARIANT = createRegistryKey("frog_variant");
    public static final ModResourceKey<Registry<GameEvent>> GAME_EVENT = createRegistryKey("game_event");
    public static final ModResourceKey<Registry<HeightProviderType<?>>> HEIGHT_PROVIDER_TYPE = createRegistryKey("height_provider_type");
    public static final ModResourceKey<Registry<Instrument>> INSTRUMENT = createRegistryKey("instrument");
    public static final ModResourceKey<Registry<IntProviderType<?>>> INT_PROVIDER_TYPE = createRegistryKey("int_provider_type");
    public static final ModResourceKey<Registry<Item>> ITEM = createRegistryKey("item");
    public static final ModResourceKey<Registry<JukeboxSong>> JUKEBOX_SONG = createRegistryKey("jukebox_song");
    public static final ModResourceKey<Registry<LootItemConditionType>> LOOT_CONDITION_TYPE = createRegistryKey("loot_condition_type");
    public static final ModResourceKey<Registry<LootItemFunctionType<?>>> LOOT_FUNCTION_TYPE = createRegistryKey("loot_function_type");
    public static final ModResourceKey<Registry<LootNbtProviderType>> LOOT_NBT_PROVIDER_TYPE = createRegistryKey("loot_nbt_provider_type");
    public static final ModResourceKey<Registry<LootNumberProviderType>> LOOT_NUMBER_PROVIDER_TYPE = createRegistryKey("loot_number_provider_type");
    public static final ModResourceKey<Registry<LootPoolEntryType>> LOOT_POOL_ENTRY_TYPE = createRegistryKey("loot_pool_entry_type");
    public static final ModResourceKey<Registry<LootScoreProviderType>> LOOT_SCORE_PROVIDER_TYPE = createRegistryKey("loot_score_provider_type");
    public static final ModResourceKey<Registry<MapCodec<? extends SurfaceRules.ConditionSource>>> MATERIAL_CONDITION = createRegistryKey(
            "worldgen/material_condition"
    );
    public static final ModResourceKey<Registry<MapCodec<? extends SurfaceRules.RuleSource>>> MATERIAL_RULE = createRegistryKey("worldgen/material_rule");
    public static final ModResourceKey<Registry<MemoryModuleType<?>>> MEMORY_MODULE_TYPE = createRegistryKey("memory_module_type");
    public static final ModResourceKey<Registry<MenuType<?>>> MENU = createRegistryKey("menu");
    public static final ModResourceKey<Registry<MobEffect>> MOB_EFFECT = createRegistryKey("mob_effect");
    public static final ModResourceKey<Registry<PaintingVariant>> PAINTING_VARIANT = createRegistryKey("painting_variant");
    public static final ModResourceKey<Registry<ParticleType<?>>> PARTICLE_TYPE = createRegistryKey("particle_type");
    public static final ModResourceKey<Registry<PlacementModifierType<?>>> PLACEMENT_MODIFIER_TYPE = createRegistryKey("worldgen/placement_modifier_type");
    public static final ModResourceKey<Registry<PoiType>> POINT_OF_INTEREST_TYPE = createRegistryKey("point_of_interest_type");
    public static final ModResourceKey<Registry<PositionSourceType<?>>> POSITION_SOURCE_TYPE = createRegistryKey("position_source_type");
    public static final ModResourceKey<Registry<PosRuleTestType<?>>> POS_RULE_TEST = createRegistryKey("pos_rule_test");
    public static final ModResourceKey<Registry<Potion>> POTION = createRegistryKey("potion");
    public static final ModResourceKey<Registry<RecipeSerializer<?>>> RECIPE_SERIALIZER = createRegistryKey("recipe_serializer");
    public static final ModResourceKey<Registry<RecipeType<?>>> RECIPE_TYPE = createRegistryKey("recipe_type");
    public static final ModResourceKey<Registry<RootPlacerType<?>>> ROOT_PLACER_TYPE = createRegistryKey("worldgen/root_placer_type");
    public static final ModResourceKey<Registry<RuleTestType<?>>> RULE_TEST = createRegistryKey("rule_test");
    public static final ModResourceKey<Registry<RuleBlockEntityModifierType<?>>> RULE_BLOCK_ENTITY_MODIFIER = createRegistryKey("rule_block_entity_modifier");
    public static final ModResourceKey<Registry<Schedule>> SCHEDULE = createRegistryKey("schedule");
    public static final ModResourceKey<Registry<SensorType<?>>> SENSOR_TYPE = createRegistryKey("sensor_type");
    public static final ModResourceKey<Registry<SoundEvent>> SOUND_EVENT = createRegistryKey("sound_event");
    public static final ModResourceKey<Registry<StatType<?>>> STAT_TYPE = createRegistryKey("stat_type");
    public static final ModResourceKey<Registry<StructurePieceType>> STRUCTURE_PIECE = createRegistryKey("worldgen/structure_piece");
    public static final ModResourceKey<Registry<StructurePlacementType<?>>> STRUCTURE_PLACEMENT = createRegistryKey("worldgen/structure_placement");
    public static final ModResourceKey<Registry<StructurePoolElementType<?>>> STRUCTURE_POOL_ELEMENT = createRegistryKey("worldgen/structure_pool_element");
    public static final ModResourceKey<Registry<MapCodec<? extends PoolAliasBinding>>> POOL_ALIAS_BINDING = createRegistryKey("worldgen/pool_alias_binding");
    public static final ModResourceKey<Registry<StructureProcessorType<?>>> STRUCTURE_PROCESSOR = createRegistryKey("worldgen/structure_processor");
    public static final ModResourceKey<Registry<StructureType<?>>> STRUCTURE_TYPE = createRegistryKey("worldgen/structure_type");
    public static final ModResourceKey<Registry<TreeDecoratorType<?>>> TREE_DECORATOR_TYPE = createRegistryKey("worldgen/tree_decorator_type");
    public static final ModResourceKey<Registry<TrunkPlacerType<?>>> TRUNK_PLACER_TYPE = createRegistryKey("worldgen/trunk_placer_type");
    public static final ModResourceKey<Registry<VillagerProfession>> VILLAGER_PROFESSION = createRegistryKey("villager_profession");
    public static final ModResourceKey<Registry<VillagerType>> VILLAGER_TYPE = createRegistryKey("villager_type");
    public static final ModResourceKey<Registry<DecoratedPotPattern>> DECORATED_POT_PATTERN = createRegistryKey("decorated_pot_pattern");
    public static final ModResourceKey<Registry<NumberFormatType<?>>> NUMBER_FORMAT_TYPE = createRegistryKey("number_format_type");
    public static final ModResourceKey<Registry<ArmorMaterial>> ARMOR_MATERIAL = createRegistryKey("armor_material");
    public static final ModResourceKey<Registry<DataComponentType<?>>> DATA_COMPONENT_TYPE = createRegistryKey("data_component_type");
    public static final ModResourceKey<Registry<MapCodec<? extends EntitySubPredicate>>> ENTITY_SUB_PREDICATE_TYPE = createRegistryKey("entity_sub_predicate_type");
    public static final ModResourceKey<Registry<ItemSubPredicate.Type<?>>> ITEM_SUB_PREDICATE_TYPE = createRegistryKey("item_sub_predicate_type");
    public static final ModResourceKey<Registry<MapDecorationType>> MAP_DECORATION_TYPE = createRegistryKey("map_decoration_type");
    public static final ModResourceKey<Registry<DataComponentType<?>>> ENCHANTMENT_EFFECT_COMPONENT_TYPE = createRegistryKey("enchantment_effect_component_type");
    public static final ModResourceKey<Registry<Biome>> BIOME = createRegistryKey("worldgen/biome");
    public static final ModResourceKey<Registry<ChatType>> CHAT_TYPE = createRegistryKey("chat_type");
    public static final ModResourceKey<Registry<ConfiguredWorldCarver<?>>> CONFIGURED_CARVER = createRegistryKey("worldgen/configured_carver");
    public static final ModResourceKey<Registry<ConfiguredFeature<?, ?>>> CONFIGURED_FEATURE = createRegistryKey("worldgen/configured_feature");
    public static final ModResourceKey<Registry<DensityFunction>> DENSITY_FUNCTION = createRegistryKey("worldgen/density_function");
    public static final ModResourceKey<Registry<DimensionType>> DIMENSION_TYPE = createRegistryKey("dimension_type");
    public static final ModResourceKey<Registry<Enchantment>> ENCHANTMENT = createRegistryKey("enchantment");
    public static final ModResourceKey<Registry<EnchantmentProvider>> ENCHANTMENT_PROVIDER = createRegistryKey("enchantment_provider");
    public static final ModResourceKey<Registry<FlatLevelGeneratorPreset>> FLAT_LEVEL_GENERATOR_PRESET = createRegistryKey("worldgen/flat_level_generator_preset");
    public static final ModResourceKey<Registry<NoiseGeneratorSettings>> NOISE_SETTINGS = createRegistryKey("worldgen/noise_settings");
    public static final ModResourceKey<Registry<NormalNoise.NoiseParameters>> NOISE = createRegistryKey("worldgen/noise");
    public static final ModResourceKey<Registry<PlacedFeature>> PLACED_FEATURE = createRegistryKey("worldgen/placed_feature");
    public static final ModResourceKey<Registry<Structure>> STRUCTURE = createRegistryKey("worldgen/structure");
    public static final ModResourceKey<Registry<StructureProcessorList>> PROCESSOR_LIST = createRegistryKey("worldgen/processor_list");
    public static final ModResourceKey<Registry<StructureSet>> STRUCTURE_SET = createRegistryKey("worldgen/structure_set");
    public static final ModResourceKey<Registry<StructureTemplatePool>> TEMPLATE_POOL = createRegistryKey("worldgen/template_pool");
    public static final ModResourceKey<Registry<CriterionTrigger<?>>> TRIGGER_TYPE = createRegistryKey("trigger_type");
    public static final ModResourceKey<Registry<TrimMaterial>> TRIM_MATERIAL = createRegistryKey("trim_material");
    public static final ModResourceKey<Registry<TrimPattern>> TRIM_PATTERN = createRegistryKey("trim_pattern");
    public static final ModResourceKey<Registry<WorldPreset>> WORLD_PRESET = createRegistryKey("worldgen/world_preset");
    public static final ModResourceKey<Registry<MultiNoiseBiomeSourceParameterList>> MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST = createRegistryKey(
            "worldgen/multi_noise_biome_source_parameter_list"
    );
    public static final ModResourceKey<Registry<Level>> DIMENSION = createRegistryKey("dimension");
    public static final ModResourceKey<Registry<LevelStem>> LEVEL_STEM = createRegistryKey("dimension");
    public static final ModResourceKey<Registry<LootTable>> LOOT_TABLE = createRegistryKey("loot_table");
    public static final ModResourceKey<Registry<LootItemFunction>> ITEM_MODIFIER = createRegistryKey("item_modifier");
    public static final ModResourceKey<Registry<LootItemCondition>> PREDICATE = createRegistryKey("predicate");
    public static final ModResourceKey<Registry<Advancement>> ADVANCEMENT = createRegistryKey("advancement");
    public static final ModResourceKey<Registry<Recipe<?>>> RECIPE = createRegistryKey("recipe");

    public static ModResourceKey<Level> levelStemToLevel(ModResourceKey<LevelStem> levelStem) {
        return ModResourceKey.create(DIMENSION, levelStem.location());
    }

    public static ModResourceKey<LevelStem> levelToLevelStem(ModResourceKey<Level> level) {
        return ModResourceKey.create(LEVEL_STEM, level.location());
    }

    private static <T> ModResourceKey<Registry<T>> createRegistryKey(String name) {
        return ModResourceKey.createRegistryKey(ModResourceLocation.withDefaultNamespace(name));
    }

    public static String elementsDirPath(ModResourceKey<? extends Registry<?>> registryKey) {
        return Basic.prefixNamespace(registryKey.location());
    }

    public static String tagsDirPath(ModResourceKey<? extends Registry<?>> registryKey) {
        return "tags/" + Basic.prefixNamespace(registryKey.location());
    }
}
