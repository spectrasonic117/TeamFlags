package com.spectrasonic.TeamFlags.Commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.spectrasonic.TeamFlags.Menu.TeamMenu;
import com.spectrasonic.TeamFlags.Utils.MessageUtils;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@CommandAlias("teamflag|tf")
public class TeamCommand extends BaseCommand {

    private final JavaPlugin plugin;

    @Default
    @Syntax("<player>")
    @CommandCompletion("@players")
    public void onTeam(CommandSender sender, @Optional String targetPlayerName) {
        // Si es consola y no se especifica un jugador, mostrar error
        if (sender instanceof ConsoleCommandSender && targetPlayerName == null) {
            MessageUtils.sendMessage(sender, "<red>Debes especificar un jugador desde la consola.</red>");
            return;
        }
        
        // Si es jugador y no se especifica un objetivo, verificar permiso y abrir el menú
        if (sender instanceof Player && targetPlayerName == null) {
            Player player = (Player) sender;
            if (!player.hasPermission("teamflags.menu.self")) {
                MessageUtils.sendMessage(player, "<red>No tienes permiso para abrir el menú de equipos.</red>");
                return;
            }
            TeamMenu.openMenu(player);
            return;
        }
        
        // Si se especifica un jugador pero el ejecutor es un jugador sin OP, no permitir
        if (sender instanceof Player && !((Player) sender).isOp() && !((Player) sender).hasPermission("teamflags.menu.others")) {
            MessageUtils.sendMessage(sender, "<red>No tienes permiso para abrir el menú a otros jugadores.</red>");
            return;
        }
        
        // Buscar el jugador objetivo
        Player targetPlayer = Bukkit.getPlayer(targetPlayerName);
        if (targetPlayer == null) {
            MessageUtils.sendMessage(sender, "<red>El jugador " + targetPlayerName + " no está en línea.</red>");
            return;
        }
        
        // Abrir el menú para el jugador objetivo
        TeamMenu.openMenu(targetPlayer);
        MessageUtils.sendMessage(sender, "<green>Has abierto el menú de equipos para " + targetPlayer.getName() + ".</green>");
    }
    
    @Subcommand("creategroups")
    public void createGroups(CommandSender sender) {
        // Verificar si el remitente es operador o consola
        if (sender instanceof Player && !((Player) sender).isOp()) {
            MessageUtils.sendMessage(sender, "<red>No tienes permiso para ejecutar este comando.</red>");
            return;
        }
        
        // Obtener la instancia de LuckPerms
        LuckPerms luckPerms = LuckPermsProvider.get();
        String[] paises = TeamMenu.getPaises();
        int createdGroups = 0;
        int existingGroups = 0;
        
        // Crear grupos para cada país
        for (String pais : paises) {
            // Verificar si el grupo ya existe
            Group existingGroup = luckPerms.getGroupManager().getGroup(pais);
            if (existingGroup != null) {
                existingGroups++;
                continue;
            }
            
            // Crear el grupo si no existe
            luckPerms.getGroupManager().createAndLoadGroup(pais).thenAccept(success -> {
                if (success != null) {
                    MessageUtils.sendConsoleMessage("<green>Grupo creado: <gold>" + pais + "</gold></green>");
                } else {
                    MessageUtils.sendConsoleMessage("<red>Error al crear el grupo: <gold>" + pais + "</gold></red>");
                }
            });
            createdGroups++;
        }
        
        // Enviar mensaje de resumen al remitente
        MessageUtils.sendMessage(sender, "<green>Proceso de creación de grupos completado:</green>");
        MessageUtils.sendMessage(sender, "<yellow>Grupos creados: <gold>" + createdGroups + "</gold></yellow>");
        MessageUtils.sendMessage(sender, "<yellow>Grupos existentes: <gold>" + existingGroups + "</gold></yellow>");
        MessageUtils.sendMessage(sender, "<yellow>Total de países procesados: <gold>" + paises.length + "</gold></yellow>");
    }
}