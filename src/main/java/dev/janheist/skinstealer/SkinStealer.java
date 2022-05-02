package dev.janheist.skinstealer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.labymod.api.LabyModAddon;
import net.labymod.api.events.UserMenuActionEvent;
import net.labymod.settings.elements.BooleanElement;
import net.labymod.settings.elements.ControlElement;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.user.User;
import net.labymod.user.util.UserActionEntry;
import net.labymod.utils.Material;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Base64;
import java.util.List;

public class SkinStealer extends LabyModAddon {

    public boolean active = true;

    @Override
    public void onEnable() {

        this.getApi().getEventManager().register(new UserMenuActionEvent() {
            @Override
            public void createActions(User user, EntityPlayer entityPlayer, NetworkPlayerInfo networkPlayerInfo, List<UserActionEntry> list) {
                if(active) {
                    if(entityPlayer instanceof AbstractClientPlayer) {
                        AbstractClientPlayer player = (AbstractClientPlayer) entityPlayer;
                        String base64 = player.getGameProfile().getProperties().get("textures").iterator().next().getValue();
                        byte[] decodedBytes = Base64.getDecoder().decode(base64);
                        String decodedString = new String(decodedBytes);

                        Gson g = new Gson();
                        JsonObject json = g.fromJson(decodedString, JsonObject.class);

                        String profileName = json.get("profileName").getAsString();
                        String skinUrl = json.get("textures").getAsJsonObject().get("SKIN").getAsJsonObject().get("url").getAsString();
                        String uuid = json.get("profileId").getAsString();

                        skinUrl += "?player=" + profileName + "&uuid=" + uuid;

                        list.add(new UserActionEntry("§bSkinStealer", UserActionEntry.EnumActionType.OPEN_BROWSER, skinUrl, null));
                    }
                }
            }
        });

    }

    @Override
    public void loadConfig() {
        this.active = !getConfig().has("active") || getConfig().get("active").getAsBoolean();

    }

    @Override
    protected void fillSettings(List<SettingsElement> list) {
        getSubSettings().add(new BooleanElement("Active?", this, new ControlElement.IconData(Material.LEVER), "active", this.active));
    }
}
