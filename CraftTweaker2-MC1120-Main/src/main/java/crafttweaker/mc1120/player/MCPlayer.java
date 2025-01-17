package crafttweaker.mc1120.player;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.chat.IChatMessage;
import crafttweaker.api.data.IData;
import crafttweaker.api.formatting.IFormattedText;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.*;
import crafttweaker.api.util.Position3f;
import crafttweaker.mc1120.CraftTweaker;
import crafttweaker.mc1120.data.NBTConverter;
import crafttweaker.mc1120.entity.MCEntityLivingBase;
import crafttweaker.mc1120.network.*;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.*;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.*;

/**
 * @author Stan
 */
public class MCPlayer extends MCEntityLivingBase implements IPlayer {
    
    private final EntityPlayer player;
    
    public MCPlayer(EntityPlayer player) {
        super(player);
        this.player = player;
    }
    
    public EntityPlayer getInternal() {
        return player;
    }
    
    @Override
    public String getName() {
        return player.getName();
    }
    
    @Override
    public IData getData() {
        return NBTConverter.from(player.getEntityData(), true);
    }
    
    @Override
    public int getXP() {
        return player.experienceLevel;
    }
    
    @Override
    public void setXP(int xp) {
        player.addExperienceLevel(-player.experienceLevel);
        player.addExperienceLevel(xp);
    }
    
    @Override
    public void removeXP(int xp) {
        player.addExperience(-xp);
    }
    
    @Override
    public void update(IData data) {
        NBTConverter.updateMap(player.getEntityData(), data);
    }
    
    @Override
    public void sendChat(IChatMessage message) {
        Object internal = message.getInternal();
        if(!(internal instanceof ITextComponent)) {
            CraftTweakerAPI.logError("not a valid chat message");
            return;
        }
        ITextComponent text = (ITextComponent) internal;
        if(text.getUnformattedText().length() > 30000) {
            // TODO: Split them instead, somehow.
            CraftTweakerAPI.logError("Message too long, suppressing:");
            CraftTweakerAPI.logError(text.getFormattedText());
        } else {
            player.sendMessage(text);
        }
    }
    
    @Override
    public void sendChat(String message) {
        String[] words = message.split(" ");
        StringBuilder out = new StringBuilder();
        for(int i = 0, wordsLength = words.length; i < wordsLength; i++) {
            String word = words[i];
            out.append(word);
            if(i < wordsLength - 1) {
                out.append(' ');
            }
            if(out.length() > 25000) {
                player.sendMessage(new TextComponentString(out.toString()));
                out = new StringBuilder();
            }
        }
        if(out.length() > 0) {
            player.sendMessage(new TextComponentString(out.toString()));
        }
    }
    
    @Override
    public void sendStatusMessage(String message, boolean hotBar) {
        this.player.sendStatusMessage(new TextComponentString(message), hotBar);
    }
    
    @Override
    public void sendStatusMessage(IFormattedText message, boolean hotBar) {
        this.player.sendStatusMessage(new TextComponentString(message.getText()), hotBar);
    }
    
    @Override
    public int getHotbarSize() {
        return 9;
    }
    
    @Override
    public IItemStack getHotbarStack(int i) {
        return i < 0 || i >= 9 ? null : CraftTweakerMC.getIItemStack(player.inventory.getStackInSlot(i));
    }
    
    @Override
    public int getInventorySize() {
        return player.inventory.getSizeInventory();
    }
    
    @Override
    public IItemStack getInventoryStack(int i) {
        return CraftTweakerMC.getIItemStack(player.inventory.getStackInSlot(i));
    }
    
    @Override
    public IItemStack getCurrentItem() {
        return CraftTweakerMC.getIItemStack(player.inventory.getCurrentItem());
    }
    
    @Override
    public boolean isCreative() {
        return player.capabilities.isCreativeMode;
    }
    
    @Override
    public boolean isAdventure() {
        return !player.capabilities.allowEdit;
    }
    
    @Override
    public void openBrowser(String url) {
        if(player instanceof EntityPlayerMP) {
            CraftTweaker.NETWORK.sendTo(new MessageOpenBrowser(url), (EntityPlayerMP) player);
        }
    }
    
    @Override
    public void copyToClipboard(String value) {
        if(player instanceof EntityPlayerMP) {
            CraftTweaker.NETWORK.sendTo(new MessageCopyClipboard(value), (EntityPlayerMP) player);
        }
    }
    
    @Override
    public boolean equals(Object other) {
        return other != null && other.getClass() == this.getClass() && ((MCPlayer) other).player == player;
        
    }
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + (this.player != null ? this.player.hashCode() : 0);
        return hash;
    }
    
    @Override
    public void give(IItemStack stack) {
        ItemStack itemstack = CraftTweakerMC.getItemStack(stack).copy();
        boolean flag = player.inventory.addItemStackToInventory(itemstack);
        
        if(flag) {
            player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
            player.inventoryContainer.detectAndSendChanges();
        }
        
        if(flag && itemstack.isEmpty()) {
            itemstack.setCount(1);
            EntityItem entityitem1 = player.dropItem(itemstack, false);
            if(entityitem1 != null) {
                entityitem1.makeFakeItem();
            }
        } else {
            EntityItem entityitem = player.dropItem(itemstack, false);
            
            if(entityitem != null) {
                entityitem.setNoPickupDelay();
                entityitem.setOwner(player.getName());
            }
        }
    }
    
    @Override
    public void teleport(Position3f pos) {
        player.setPosition(pos.getX(), pos.getY(), pos.getZ());
    }
    
    @Override
    public int getScore() {
        return player.getScore();
    }
    
    @Override
    public void addScore(int amount) {
        player.addScore(amount);
    }
    
    @Override
    public void setScore(int amount) {
        player.setScore(amount);
    }
    
    @Override
    public IFoodStats getFoodStats() {
        return new MCFoodStats(player.getFoodStats());
    }
    
    @Override
    public void executeCommand(String rawCommand) {
        MinecraftServer server = player.getServer();
        if(server != null)
            server.getCommandManager().executeCommand(player, rawCommand);
    }
    
    @Override
    public boolean isDamageDisabled() {
        return player.capabilities.disableDamage;
    }
    
    @Override
    public void setDamageDisabled(boolean disabled) {
        player.capabilities.disableDamage = disabled;
    }
    
    @Override
    public boolean canFly() {
        return player.capabilities.allowFlying;
    }
    
    @Override
    public void setCanFly(boolean canFly) {
        player.capabilities.allowFlying = canFly;
    }
    
    @Override
    public boolean canEdit() {
        return player.capabilities.allowEdit;
    }
    
    @Override
    public void setCanEdit(boolean canEdit) {
        player.capabilities.allowEdit = canEdit;
    }
}
