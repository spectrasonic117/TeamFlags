package com.spectrasonic.TeamFlags.Events;

import com.spectrasonic.TeamFlags.Menu.TeamMenu;
import com.spectrasonic.TeamFlags.Utils.MessageUtils;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InventoryListener implements Listener {

    private final JavaPlugin plugin;

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getView().getTitle().contains("Seleccione un equipo")) {
            e.setCancelled(true);
            Player player = (Player) e.getWhoClicked();
            ItemStack clicked = e.getCurrentItem();
            if (clicked == null || !clicked.hasItemMeta() || !clicked.getItemMeta().hasCustomModelData())
                return;
            Integer cmd = clicked.getItemMeta().getCustomModelData();

            // Si se hizo clic en el item de salida (custom model data 200)
            if (cmd == 200) {
                removePlayerFromGroups(player);
                MessageUtils.sendMessage(player, "<yellow>[!] Has salido del equipo</yellow>");
                player.closeInventory();
                return;
            }

            // Si se hizo clic en un item de unión (custom model data entre 100 y 199)
            if (cmd >= 100 && cmd < 200) {
                String[] paises = TeamMenu.getPaises();
                int index = cmd - 100;
                if (index >= paises.length) return;
                String groupName = paises[index];
                joinTeam(player, groupName);
                MessageUtils.sendMessage(player, "<green>Te has unido al equipo <gold>" + groupName + "</gold></green>");
                player.closeInventory();
            }
        }
    }

    private void removePlayerFromGroups(Player player) {
        LuckPerms luckPerms = LuckPermsProvider.get();
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user == null) return;
        user.data().clear(node -> node instanceof InheritanceNode);
        luckPerms.getUserManager().saveUser(user);
    }

    private void joinTeam(Player player, String groupName) {
        removePlayerFromGroups(player);
        LuckPerms luckPerms = LuckPermsProvider.get();
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user == null) return;
        // Se asume que el nombre interno del grupo es exactamente el mismo (en mayúsculas)
        InheritanceNode node = InheritanceNode.builder(groupName).build();
        user.data().add(node);
        luckPerms.getUserManager().saveUser(user);
    }
}