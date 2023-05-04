package com.lightdev6.computing.block.computer;

import com.lightdev6.computing.Computing;
import com.lightdev6.computing.AllTileEntities;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.contraptions.relays.elementary.ICogWheel;
import com.simibubi.create.foundation.block.ITE;
import com.simibubi.create.foundation.gui.ScreenOpener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class Computer extends Block implements EntityBlock, ICogWheel, ITE<ComputerBlockEntity> {


    public Computer(Properties properties) {
        super(properties);
    }


    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult result) {
        //called on right click
        if(!level.isClientSide && hand.equals(InteractionHand.MAIN_HAND)){
            ItemStack item = player.getMainHandItem();
            String itemName = item.getHoverName().getString();

            //Computing.runProgram(itemName, Vec3.atCenterOf(blockPos), player);
            if (level.getBlockEntity(blockPos) instanceof ComputerBlockEntity computer){
                if(item.getItem().equals(Items.STICK)){
                    Computing.runProgram(computer.getScript(), Vec3.atCenterOf(blockPos), player);
                } else if(item.getItem().equals(Items.QUARTZ)){
                    Computing.runFunctionProgram("f", Arrays.asList(13.0),computer.getScript(), Vec3.atCenterOf(blockPos), player);
                } else if (item.getItem().equals(Items.PAPER)){
                    computer.setScript(itemName);
                    player.sendSystemMessage(Component.literal("Set script"));
                }
            }


        }

        ItemStack held = player.getMainHandItem();
        if (AllItems.WRENCH.isIn(held))
            return InteractionResult.PASS;
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> withTileEntityDo(level, blockPos, te -> this.displayScreen(te, player)));


        return InteractionResult.SUCCESS;
    }

    @OnlyIn(value = Dist.CLIENT)
    protected void displayScreen(ComputerBlockEntity computer, Player player){
        if (player instanceof LocalPlayer)
            ScreenOpener.open(new ComputerScreen(computer));
    }


    @Override
    public Class<ComputerBlockEntity> getTileEntityClass() {
        return ComputerBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ComputerBlockEntity> getTileEntityType() {
        return AllTileEntities.COMPUTER.get();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return AllTileEntities.COMPUTER.get().create(blockPos, blockState);
    }


    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return false;
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return Direction.Axis.Y;
    }

    @Override
    public SpeedLevel getMinimumRequiredSpeedLevel() {
        return SpeedLevel.MEDIUM;
    }

}
