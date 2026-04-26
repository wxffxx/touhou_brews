package com.wxffxx.touhoubrews.block.entity;

import com.wxffxx.touhoubrews.config.BrewConfigManager;
import com.wxffxx.touhoubrews.menu.InfusionJarMenu;
import com.wxffxx.touhoubrews.registry.ModBlockEntities;
import com.wxffxx.touhoubrews.registry.ModItems;
import com.wxffxx.touhoubrews.util.MachineInputRules;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class InfusionJarBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, Container {
    private static final int PROCESS_TIME = 1200;

    private final NonNullList<ItemStack> inventory = NonNullList.withSize(4, ItemStack.EMPTY);
    private int progress = 0;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> progress;
                case 1 -> PROCESS_TIME;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            if (index == 0) progress = value;
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    public InfusionJarBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.INFUSION_JAR, pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, InfusionJarBlockEntity entity) {
        if (level.isClientSide) return;

        ItemStack base = entity.inventory.get(0);
        ItemStack fruit = entity.inventory.get(1);
        ItemStack sugar = entity.inventory.get(2);
        ItemStack output = entity.inventory.get(3);

        if (base.isEmpty() || fruit.isEmpty() || sugar.isEmpty()
                || !MachineInputRules.isInfusionBaseInput(base)
                || !MachineInputRules.isInfusionFruitInput(fruit)
                || !MachineInputRules.isInfusionSweetenerInput(sugar)) {
            entity.resetProgress();
            return;
        }

        com.wxffxx.touhoubrews.config.BrewRecipeEntry matched = null;
        for (com.wxffxx.touhoubrews.config.BrewRecipeEntry recipe
                : BrewConfigManager.recipes().infusion_jar) {
            if (matchesInfusionRecipe(recipe, base, fruit, sugar)) {
                matched = recipe;
                break;
            }
        }

        if (matched == null) {
            entity.resetProgress();
            return;
        }

        ItemStack resultStack = ModItems.resolveBrewOutput(matched.output_brew_type, matched.output_quality);
        boolean outputFree = output.isEmpty()
                || (ItemStack.isSameItemSameTags(output, resultStack) && output.getCount() < output.getMaxStackSize());
        if (!outputFree) {
            entity.resetProgress();
            return;
        }

        entity.progress++;
        entity.setChanged();

        if (entity.progress % 40 == 0 && level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                    pos.getX() + 0.5, pos.getY() + 0.85, pos.getZ() + 0.5,
                    2, 0.18, 0.12, 0.18, 0.0);
        }

        if (entity.progress >= PROCESS_TIME) {
            entity.progress = 0;
            base.shrink(1);
            fruit.shrink(1);
            sugar.shrink(1);

            if (output.isEmpty()) {
                entity.inventory.set(3, resultStack);
            } else {
                output.grow(1);
            }
            entity.setChanged();

            if (level instanceof ServerLevel serverLevel) {
                serverLevel.playSound(null, pos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 0.9f, 1.1f);
            }
        }
    }

    private static boolean matchesInfusionRecipe(
            com.wxffxx.touhoubrews.config.BrewRecipeEntry recipe,
            ItemStack base, ItemStack fruit, ItemStack sugar) {
        return itemMatches(base, recipe.base)
                && itemMatches(fruit, recipe.fruit)
                && itemMatches(sugar, recipe.sweetener);
    }

    private static boolean itemMatches(ItemStack stack, String itemId) {
        if (itemId == null) return false;
        try {
            ResourceLocation rl = new ResourceLocation(itemId);
            return stack.is(net.minecraft.core.registries.BuiltInRegistries.ITEM.get(rl));
        } catch (Exception e) { return false; }
    }

    @Override public int getContainerSize() { return inventory.size(); }
    @Override public boolean isEmpty() { return inventory.stream().allMatch(ItemStack::isEmpty); }
    @Override public ItemStack getItem(int slot) { return inventory.get(slot); }
    @Override public ItemStack removeItem(int slot, int amount) { ItemStack result = ContainerHelper.removeItem(inventory, slot, amount); setChanged(); return result; }
    @Override public ItemStack removeItemNoUpdate(int slot) { return ContainerHelper.takeItem(inventory, slot); }
    @Override public void setItem(int slot, ItemStack stack) { inventory.set(slot, stack); if (stack.getCount() > getMaxStackSize()) stack.setCount(getMaxStackSize()); setChanged(); }
    @Override public boolean canPlaceItem(int slot, ItemStack stack) {
        return switch (slot) {
            case 0 -> MachineInputRules.isInfusionBaseInput(stack);
            case 1 -> MachineInputRules.isInfusionFruitInput(stack);
            case 2 -> MachineInputRules.isInfusionSweetenerInput(stack);
            default -> false;
        };
    }
    @Override public boolean stillValid(Player player) { return level != null && level.getBlockEntity(worldPosition) == this && player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5) <= 64.0; }
    @Override public void clearContent() { inventory.clear(); }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.touhou_brews.infusion_jar");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInv, Player player) {
        return new InfusionJarMenu(syncId, playerInv, this, data);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, inventory);
        tag.putInt("Progress", progress);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        inventory.clear();
        ContainerHelper.loadAllItems(tag, inventory);
        progress = tag.getInt("Progress");
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
        buf.writeBlockPos(worldPosition);
    }

    private void resetProgress() {
        if (progress != 0) {
            progress = 0;
            setChanged();
        }
    }
}
