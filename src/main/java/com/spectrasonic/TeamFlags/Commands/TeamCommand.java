package com.spectrasonic.TeamFlags.Commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Syntax;
import com.spectrasonic.TeamFlags.Menu.TeamMenu;
import com.spectrasonic.TeamFlags.Utils.MessageUtils;
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
    public void onTeam(Player player, @Optional String targetPlayerName) {
        // Si no se especifica un jugador, abre el menú para el ejecutor
        if (targetPlayerName == null) {
            TeamMenu.openMenu(player);
            return;
        }
        
        // Si se especifica un jugador pero el ejecutor no es OP, no permitir
        if (!player.isOp()) {
            MessageUtils.sendMessage(player, "<red>No tienes permiso para abrir el menú a otros jugadores.</red>");
            return;
        }
        
        // Buscar el jugador objetivo
        Player targetPlayer = Bukkit.getPlayer(targetPlayerName);
        if (targetPlayer == null) {
            MessageUtils.sendMessage(player, "<red>El jugador " + targetPlayerName + " no está en línea.</red>");
            return;
        }
        
        // Abrir el menú para el jugador objetivo
        TeamMenu.openMenu(targetPlayer);
        MessageUtils.sendMessage(player, "<green>Has abierto el menú de equipos para " + targetPlayer.getName() + ".</green>");
    }
    
    // Método para permitir que la consola ejecute el comando
    @Default
    @Syntax("<player>")
    @CommandCompletion("@players")
    public void onTeamConsole(org.bukkit.command.ConsoleCommandSender sender, String targetPlayerName) {
        if (targetPlayerName == null) {
            MessageUtils.sendMessage(sender, "<red>Debes especificar un jugador.</red>");
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
}