package crafttweaker.api.world;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.*;
import crafttweaker.api.data.IData;
import crafttweaker.api.entity.IEntity;
import crafttweaker.api.util.Position3f;
import stanhebben.zenscript.annotations.*;

@ZenClass("crafttweaker.world.IWorld")
@ZenRegister
public interface IWorld extends IBlockAccess {
    
    @ZenMethod
    int getBrightness(IBlockPos position);
    
    @ZenMethod
    int getBrightness(int x, int y, int z);
    
    @ZenMethod
    IBiome getBiome(Position3f position);
    
    @ZenMethod
    IBiome getBiome(IBlockPos position);
    
    @ZenGetter("worldInfo")
    @ZenMethod
    IWorldInfo getWorldInfo();
    
    @ZenGetter("remote")
    @ZenMethod
    boolean isRemote();
    
    @ZenGetter("raining")
    @ZenMethod
    boolean isRaining();
    
    @ZenGetter("dayTime")
    @ZenMethod
    boolean isDayTime();
    
    @ZenGetter("time")
    @ZenMethod
    long getWorldTime();
    
    @ZenGetter("surfaceWorld")
    @ZenMethod
    boolean isSurfaceWorld();
    
    @ZenGetter("moonPhase")
    @ZenMethod
    int getMoonPhase();
    
    @ZenGetter("dimension")
    @ZenMethod
    int getDimension();
    
    @ZenGetter("dimensionType")
    @ZenMethod
    String getDimensionType();
    
    @ZenMethod
    IBlock getBlock(int x, int y, int z);
    
    @ZenMethod
    IBlock getBlock(IBlockPos pos);
    
    @ZenGetter("worldType")
    @ZenMethod
    String getWorldType();
    
    @ZenMethod
    boolean setBlockState(IBlockState state, IBlockPos pos);

    @ZenMethod
    boolean setBlockState(IBlockState state, IData tileEntityData, IBlockPos pos);
    
    @ZenGetter("provider")
    @ZenMethod
    IWorldProvider getProvider();
    
    Object getInternal();
    
    @ZenMethod
    boolean spawnEntity(IEntity entity);
    
    @ZenMethod
    default IRayTraceResult rayTraceBlocks(IVector3d begin, IVector3d ray, @Optional boolean stopOnLiquid, @Optional boolean ignoreBlockWithoutBoundingBox, @Optional(valueBoolean = true) boolean returnLastUncollidableBlock) {
        CraftTweakerAPI.logError(this.getClass().getName() + " does not override IWorld.getRayTrace, tell the author to fix that.");
        return null;
    }
}
