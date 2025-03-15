package com.spectrasonic.TeamFlags.Commands;

import com.spectrasonic.TeamFlags.Utils.MessageUtils;
import com.spectrasonic.TeamFlags.Menu.TeamMenu;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.types.PrefixNode;
import org.bukkit.command.ConsoleCommandSender;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.command.CommandSender;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import lombok.RequiredArgsConstructor;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@CommandAlias("teamflag|tf")
public class TeamCommand extends BaseCommand {

    private final JavaPlugin plugin;

    @Default
    @Syntax("<player>")
    @CommandCompletion("@players")
    public void onTeam(CommandSender sender, @Optional String targetPlayerName) {
        if (sender instanceof ConsoleCommandSender && targetPlayerName == null) {
            MessageUtils.sendMessage(sender, "<red>Debes especificar un jugador desde la consola.</red>");
            return;
        }
        if (sender instanceof Player && targetPlayerName == null) {
            Player player = (Player) sender;
            if (!player.hasPermission("teamflags.menu.self")) {
                MessageUtils.sendMessage(player, "<red>No tienes permiso para abrir el menú de equipos.</red>");
                return;
            }
            TeamMenu.openMenu(player);
            return;
        }
        if (sender instanceof Player && !((Player) sender).isOp() && !((Player) sender).hasPermission("teamflags.menu.others")) {
            MessageUtils.sendMessage(sender, "<red>No tienes permiso para abrir el menú a otros jugadores.</red>");
            return;
        }
        Player targetPlayer = Bukkit.getPlayer(targetPlayerName);
        if (targetPlayer == null) {
            MessageUtils.sendMessage(sender, "<red>El jugador " + targetPlayerName + " no está en línea.</red>");
            return;
        }
        TeamMenu.openMenu(targetPlayer);
        MessageUtils.sendMessage(sender, "<green>Has abierto el menú de equipos para " + targetPlayer.getName() + ".</green>");
    }
    
    @Subcommand("creategroups")
    public void createGroups(CommandSender sender) {

        final String[] CHAR_PAISES = {"","","","","","","","","","","","","","","","","","","","","","","","","","",""};

        if (sender instanceof Player && !((Player) sender).isOp()) {
            MessageUtils.sendMessage(sender, "<red>No tienes permiso para ejecutar este comando.</red>");
            return;
        }
        
        LuckPerms luckPerms = LuckPermsProvider.get();
        String[] paises = TeamMenu.getPaises();
        int createdGroups = 0;
        int existingGroups = 0;
        
        for (int i = 0; i < paises.length; i++) {
            String pais = paises[i];
            String emoji = CHAR_PAISES[i];
            
            Group existingGroup = luckPerms.getGroupManager().getGroup(pais);
            if (existingGroup != null) {
                existingGroups++;
                continue;
            }
            
            luckPerms.getGroupManager().createAndLoadGroup(pais).thenAccept(group -> {
                if (group != null) {
                    // Crear el nodo de prefijo con prioridad 100 y texto formado con el emoji del país
                    PrefixNode prefixNode = PrefixNode.builder(emoji + " &8&l• &r", 100).build();
                    group.data().add(prefixNode);
                    // Guardar los cambios en el grupo
                    luckPerms.getGroupManager().saveGroup(group);
                    MessageUtils.sendConsoleMessage("<green>Grupo creado: <gold>" + pais + "</gold> con prefijo <gold>" 
                        + emoji + "</gold></green>");
                } else {
                    MessageUtils.sendConsoleMessage("<red>Error al crear el grupo: <gold>" + pais + "</gold></red>");
                }
            });
            createdGroups++;
        }
        
        MessageUtils.sendMessage(sender, "<green>Proceso de creación de grupos completado:</green>");
        MessageUtils.sendMessage(sender, "<yellow>Grupos creados: <gold>" + createdGroups + "</gold></yellow>");
        MessageUtils.sendMessage(sender, "<yellow>Grupos existentes: <gold>" + existingGroups + "</gold></yellow>");
        MessageUtils.sendMessage(sender, "<yellow>Total de países procesados: <gold>" + paises.length + "</gold></yellow>");
    }
    
    @Subcommand("deletegroups")
    public void deleteGroups(CommandSender sender) {
        if (sender instanceof Player && !((Player) sender).isOp()) {
            MessageUtils.sendMessage(sender, "<red>No tienes permiso para ejecutar este comando.</red>");
            return;
        }
        
        LuckPerms luckPerms = LuckPermsProvider.get();
        String[] paises = TeamMenu.getPaises();
        int deletedGroups = 0;
        
        // Primero mover a todos los jugadores al grupo default eliminando la herencia de los grupos de países
        luckPerms.getUserManager().getLoadedUsers().forEach(user -> {
            for (String pais : paises) {
                // Se recogen los nodos de herencia correspondientes al grupo "pais"
                user.data().toCollection().stream()
                    .filter(n -> n instanceof InheritanceNode &&
                                ((InheritanceNode) n).getGroupName().equalsIgnoreCase(pais))
                    .collect(Collectors.toSet())
                    .forEach(user.data()::remove);
            }
            
            // Asignar el grupo "default" si no lo tiene ya
            if (!user.getPrimaryGroup().equalsIgnoreCase("default")) {
                user.setPrimaryGroup("default");
            }
            
            // Guardar los cambios en el usuario
            luckPerms.getUserManager().saveUser(user);
        });
        
        MessageUtils.sendMessage(sender, "<yellow>Todos los jugadores han sido movidos al grupo default.</yellow>");
        
        // Ahora eliminar los grupos de los países
        for (String pais : paises) {
            Group existingGroup = luckPerms.getGroupManager().getGroup(pais);
            if (existingGroup != null) {
                luckPerms.getGroupManager().deleteGroup(existingGroup);
                MessageUtils.sendConsoleMessage("<red>Grupo eliminado: <gold>" + pais + "</gold></red>");
                deletedGroups++;
            }
        }
        
        MessageUtils.sendMessage(sender, "<green>Proceso de eliminación de grupos completado:</green>");
        MessageUtils.sendMessage(sender, "<yellow>Grupos eliminados: <gold>" + deletedGroups + "</gold></yellow>");
        MessageUtils.sendMessage(sender, "<yellow>Total de países procesados: <gold>" + paises.length + "</gold></yellow>");
    }

    @Subcommand("reload")
    public void reloadConfig(CommandSender sender) {
        if (sender instanceof Player && !((Player) sender).isOp()) {
            MessageUtils.sendMessage(sender, "<red>No tienes permiso para ejecutar este comando.</red>");
            return;
        }
        
        plugin.reloadConfig();
        MessageUtils.sendMessage(sender, "<green>Configuración recargada correctamente.</green>");
    }
}
