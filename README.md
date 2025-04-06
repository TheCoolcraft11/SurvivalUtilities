### Download [Here](https://modrinth.com/mod/screenshot-uploader) at Modrinth
##
**SurvivalUtilities** is a Paper plugin designed to enhance your Minecraft gameplay by adding several powerful features, including:

- Carrying Mobs, Players, or Blocks
Pick up and transport mobs, players, and blocks.

- Peaceful Mobs
Peaceful mobs will only attack you if you attack them.

- Individual KeepInventory for Players
Each player can have their own keepInventory setting.

- Blueprint / Schematic Saving and Rebuilding
Save blueprints and rebuild structures with ease.

- Anti-TNT Explosions
Prevent TNT explosions for certain players.

- Chunkbase Command
Command to open chunkbase

- @Mention Players in Chat
Mention players directly in chat for notifications.

- Jail Command
Trap specific players inside a glass box or a block of your choice

- Sudo Command
Execute commands as another player
 
- Permissions
Nearly everything can be allowed / disallowed by a permission plugin like [LuckPerms](https://luckperms.net/) or via the plugin command

Everything can be adjusted via the plugin config. 

### Permissions

- ```survivalutilities.carry.protected``` - Prevents the player from being carried away
- ```survivalutilities.carry.pickup.<ENTITY_TYPE>```- Allows a player to pickup the entity type
- ```survivalutilities.tnt.use``` - Disable TNT Explosions from a player (NOTE: Only works when its ignited by the player, by redstone it will still explode)
- ```survivalutilities.placeSchematic``` - Allows a player to place a saved schematic
- ```survivalutilities.saveSchematic``` - Allows a player to save a selected area to a schematic
- ```survivalutilities.keepInventory```- Allows a player to toggle their keepInventory setting
- ```survivalutilities.sudo``` - Allows a player to execute commands as another player
- ```survivalutilities.jail``` - Allows a player to trap other players inside a glass box
- ```survivalutilities.chunkbase``` - Allows player a player to do /cb to open Chunkbase

### Config

```
commands:
  keepInventory:
    ## Command to toggle keepInventory for the player
    enabled: false
  survivalutilities:
    ## Command to change plugin settings
    enabled: true
  jail:
    ## Command to jail a player
    enabled: true
  sudo:
    ## Command to execute a command as another player
    enabled: true
  placeSchematic:
    ## Command to place a saved schematic
    enabled: true
  saveSchematic:
    ## Command to save a selected area as a schematic
    enabled: true
  chunkbase:
    ## Command to open the chunkbase website
    enabled: false

functions:
  carryMobs:
    ## Allows players to carry mobs, players or blocks around
    enabled: true
    ##Protect OP players or players with permission 'survivalutlities.carry.protected'
    protectOP: true
    ##Allow the player to pick up multiple entities at once
    allowMultiPicking: false
    ##Protect tamed animals so only the owner can carry them around
    protectTamed: true
    ##Requieres the player the permission 'survivalutilities.carry.pickup.<ENTITY_TYPE>' to pick the  entity of that type up
    requirePermission: false
    ##Allow the player to pick up mobs that are stacked
    allowRiders: true
    ##World where players cant pick up mobs
    blacklistedWorlds:
    ## - world
    ## - world_nether
    ## - world_the_end
    ##Entites which cant be picked up
    blacklistedEntities:
      - MARKER
      - BLOCK_DISPLAY
      - ITEM_DISPLAY
      - TEXT_DISPLAY
      - ENDER_DRAGON
      ##- PIG
    ##Allow the player to carry blocks
    allowBlockCarry: true
    ##Use list below to blacklist blocks
    blacklistBlocks: false
    ##Use list to whitelist/blacklist blocks
    whitelistBlocks:
      - CHEST
      - DROPPER
      - DISPENSER
      - ENDER_CHEST
      - PLAYER_HEAD
    ##Disables Damage from the other entity while carrying
    disableAttacking: true
  monsters:
    ## Enabled peaceful monsters where they only attack when being attacked but all near monsters will attack
    enabled: false
    ##Make listed monster aggro normally
    allowed:
      - ZOMBIFIED_PIGLIN
  tntProtection:
    ## Cancel tnt explosion when player hasn't got permission 'survivalutilities.tnt.use'
    enabled: false
  chatMentions:
    ## Enable chat mentions so players can mention each other in chat by @<playername>, will play a sound when mentioned
    enabled: true
  keepInventory:
    ## Enable the individual keepInventory for players
    enabled: true
```
