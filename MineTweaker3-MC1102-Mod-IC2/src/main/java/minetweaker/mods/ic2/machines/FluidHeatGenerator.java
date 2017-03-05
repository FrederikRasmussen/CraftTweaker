package minetweaker.mods.ic2.machines;

import ic2.api.recipe.Recipes;
import minetweaker.*;
import minetweaker.annotations.ModOnly;
import minetweaker.api.liquid.ILiquidStack;
import net.minecraftforge.fluids.FluidRegistry;
import stanhebben.zenscript.annotations.*;

/**
 * Add a fluid to the fluid heat generator
 *
 * @author Stan
 */
@ZenClass("mods.ic2.FluidHeatGenerator")
@ModOnly("IC2")
public class FluidHeatGenerator {
    
    @ZenMethod
    public static void addFluid(ILiquidStack liquidPerTick, int heatPerTick) {
        MineTweakerAPI.apply(new AddFluidAction(liquidPerTick.getName(), liquidPerTick.getAmount(), heatPerTick));
    }
    
    @ZenMethod
    public static boolean accepts(ILiquidStack liquid) {
        return Recipes.fluidHeatGenerator.acceptsFluid(FluidRegistry.getFluid(liquid.getName()));
    }
    
    private static class AddFluidAction extends OneWayAction {
        
        private final String name;
        private final int liquidPerTick;
        private final int heatPerTick;
        
        public AddFluidAction(String name, int liquidPerTick, int heatPerTick) {
            this.name = name;
            this.liquidPerTick = liquidPerTick;
            this.heatPerTick = heatPerTick;
        }
        
        @Override
        public void apply() {
            Recipes.fluidHeatGenerator.addFluid(name, liquidPerTick, heatPerTick);
        }
        
        @Override
        public String describe() {
            return "Adding liquid " + name + " as fluid heat generator fuel";
        }
        
        @Override
        public Object getOverrideKey() {
            return null;
        }
        
        @Override
        public int hashCode() {
            int hash = 7;
            hash = 47 * hash + (this.name != null ? this.name.hashCode() : 0);
            hash = 47 * hash + this.liquidPerTick;
            hash = 47 * hash + this.heatPerTick;
            return hash;
        }
        
        @Override
        public boolean equals(Object obj) {
            if(obj == null) {
                return false;
            }
            if(getClass() != obj.getClass()) {
                return false;
            }
            final AddFluidAction other = (AddFluidAction) obj;
            return (this.name == null) ? other.name == null : this.name.equals(other.name) && this.liquidPerTick == other.liquidPerTick && this.heatPerTick == other.heatPerTick;
        }
    }
}
