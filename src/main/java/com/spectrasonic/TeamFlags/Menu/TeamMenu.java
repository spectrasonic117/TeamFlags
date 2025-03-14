package com.spectrasonic.TeamFlags.Menu;

import com.spectrasonic.TeamFlags.Utils.ItemBuilder;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;


public class TeamMenu {

    private static final int MENU_SIZE = 54;
    private static final int LEAVE_SLOT = 53;

    // Lista de países – ya en mayúsculas y con espacios reemplazados por guión bajo (si aplica)
    private static final String[] PAISES = {
            "MEXICO", "BELICE", "GUATEMALA", "EL_SALVADOR", "HONDURAS", "NICARAGUA", "COSTA_RICA", "PANAMA",
            "CUBA", "REPUBLICA_DOMINICANA", "HAITI", "PUERTO_RICO", "COLOMBIA", "VENEZUELA", "ECUADOR", "PERU",
            "BOLIVIA", "PARAGUAY", "CHILE", "ARGENTINA", "URUGUAY", "BRASIL", "USA", "PORTUGAL", "REINO_UNIDO",
            "ESPANA", "FRANCIA"
    };

    public static void openMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, MENU_SIZE, MiniMessage.miniMessage().deserialize("<gold>Seleccione un equipo"));
        
        for (int i = 0; i < PAISES.length && i < LEAVE_SLOT; i++) {
            ItemStack item = ItemBuilder.setMaterial("CHARCOAL")
                    .setCustomModelData(100 + i)
                    .setName("<green>Unirse a equipo " + PAISES[i])
                    .setLore("<gray>Click para unirte a este Pais")
                    .build();
            
            inv.setItem(i, item);
        }
        
        ItemStack exitItem = ItemBuilder.setMaterial("CHARCOAL")
                .setCustomModelData(200)
                .setName("<red>Salir del Equipo")
                .setLore("<gray>Click para salir del equipo")
                .build();
                
        inv.setItem(LEAVE_SLOT, exitItem);
        player.openInventory(inv);
    }

    public static String[] getPaises() {
        return PAISES;
    }
}