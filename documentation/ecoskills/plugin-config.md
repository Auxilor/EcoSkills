---
title: "Plugin Config"
sidebar_position: 8
---

This is the main config for EcoSkills, found at `/plugins/EcoSkills/config.yml`. It controls storage, the skills and stats GUIs, the level progression GUI, the persistent action bar, damage indicators, and how XP gain and level-ups are announced. Edit it, then run `/ecoskills reload` to apply most changes.

:::warning
Toggling the leaderboard (`leaderboard.enabled`) needs a full server restart, not a reload. A reload alone will leave the old state in place.
:::

## Default config.yml

```yaml
use-local-storage: false # Force local storage even when eco uses a database; disables cross-server sync

leaderboard:
  enabled: true # Whether the top leaderboard is active; toggling this needs a full server restart
  cache-lifetime: 60 # Lifetime of the leaderboard cache, in seconds

disabled-in-worlds: # Worlds EcoSkills is fully disabled in
  - world_1
  - world_2

gui:
  rows: 6 # Rows in the /skills GUI
  cache-ttl: 2000 # How long GUI icons are cached, in milliseconds; prevents GUI spam causing lag

  mask: # The background mask: a list of materials plus a pattern that places them
    # Each pattern line is 9 long; rows must match the GUI row count.
    # 0 is empty, 1 is the first material, 2 the second, and so on up to 9.
    materials:
      - gray_stained_glass_pane # Material 1
      - black_stained_glass_pane # Material 2
    pattern:
      - "211101112"
      - "211111112"
      - "210000012"
      - "210010012"
      - "211111112"
      - "211101112"

  player-info: # The player summary icon at the top of the GUI
    row: 1
    column: 5
    enabled: true # Whether the summary icon is shown
    name: "&f%player%&f's &fStats:" # Icon title
    lore: # Icon lore; uses ecoskills placeholders for live stat values
      - "&f"
      - " %ecoskills_defense_name%&f %ecoskills_defense_base% &e%ecoskills_defense_bonus%"
      - " %ecoskills_strength_name%&f %ecoskills_strength_base% &e%ecoskills_strength_bonus%"
      - " %ecoskills_crit_chance_name%&f %ecoskills_crit_chance_base%% &e%ecoskills_crit_chance_bonus%"
      - " %ecoskills_crit_damage_name%&f %ecoskills_crit_damage_base% &e%ecoskills_crit_damage_bonus%"
      - " %ecoskills_speed_name%&f %ecoskills_speed_base% &e%ecoskills_speed_bonus%"
      - " %ecoskills_wisdom_name%&f %ecoskills_wisdom_base% &e%ecoskills_wisdom_bonus%"
      - " %ecoskills_ferocity_name%&f %ecoskills_ferocity_base% &e%ecoskills_ferocity_bonus%"
      - "&f"
      - "&7Total Skill Level: &f%ecoskills_total_skill_level%"
      - "&7Average Skill Level: &f%ecoskills_average_skill_level%"
    view-more: # Appended to the lore when more detail is available
      - ""
      - "&eClick to view more!"

  skill-icon: # The per-skill icon in the /skills GUI
    name: "&#ff00ae%skill% &d%level_numeral%"
    lore:
      - "&8&o%description%"
      - "&f"
      - "%gui_lore%"
      - "&f"
      - "&fProgress to next level:"
      - "&8» &e%percentage_progress%%"
      - "&8» &e%current_xp%&8/&7%required_xp% &fXP"
      - "&f"
      - "&eClick to view Level Progression!"
    line-wrap: 32 # Wrap the description after this many characters

  unknown-skill-icon: # Shown for skills hidden before level 1
    name: "&#964b00Unknown Skill"
    icon: player_head texture:eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmFkYzA0OGE3Y2U3OGY3ZGFkNzJhMDdkYTI3ZDg1YzA5MTY4ODFlNTUyMmVlZWQxZTNkYWYyMTdhMzhjMWEifX19
    lore:
      - "&7Keep playing to learn about"
      - "&7and progress this skill!"

  close: # The close button
    enabled: true
    item: barrier
    name: "&cClose"
    location:
      row: 6
      column: 5

  custom-slots: [ ] # Extra GUI slots; see https://plugins.auxilor.io/all-plugins/custom-gui-slots

stats-gui:
  rows: 6 # Rows in the /stats GUI

  mask: # Background mask; same rules as the skills GUI mask above
    materials:
      - gray_stained_glass_pane
      - black_stained_glass_pane
    pattern:
      - "211101112"
      - "211111112"
      - "201010102"
      - "210101012"
      - "211111112"
      - "211101112"

  player-info:
    row: 1
    column: 5

  stat-icon: # The per-stat icon in the /stats GUI
    name: "%stat%"
    lore:
      - "&fLevel: &a%level%"
      - "&8&o%description%"
    line-wrap: 24 # Wrap the description after this many characters

  back: # Button returning to the skills GUI
    item: arrow
    name: "&eGo Back"
    location:
      row: 6
      column: 4

  close:
    enabled: true
    item: barrier
    name: "&cClose"
    location:
      row: 6
      column: 5

  custom-slots: [ ] # Extra GUI slots; see https://plugins.auxilor.io/all-plugins/custom-gui-slots

level-gui:
  title: "&7%skill%" # GUI title; %skill% is the skill being viewed
  rows: 6

  mask:
    materials:
      - black_stained_glass_pane
    pattern:
      - "111111111"
      - "111111111"
      - "111111111"
      - "111111111"
      - "111111111"
      - "111111111"

  progression-slots: # The level nodes shown in order
    pattern: # Order runs 1-9 then a-z; a follows 9
      - "109ab0jkl"
      - "2080c0i0m"
      - "3070d0h0n"
      - "4560efg0o"
      - "00000000p"
      - "00000000q"
    item-amount: "%level%" # Item stack size as a function of level; always rounded down, e.g. "ceil((%level% + 1) / 10)" to step every 10 levels

    prev-page:
      material: arrow
      name: "&fPrevious Page"
      location:
        row: 6
        column: 4

    next-page:
      material: arrow
      name: "&fNext Page"
      location:
        row: 6
        column: 6

    close:
      enabled: true
      material: barrier
      name: "&cClose"
      location:
        row: 6
        column: 5

    unlocked: # Node style for a reached level
      item: lime_stained_glass_pane
      name: "&a%skill% %level_numeral%"
      lore:
        - "&f"
        - "&fRewards:"
        - "%rewards%"
        - "&f"
        - "&aUNLOCKED"
    in-progress: # Node style for the level being worked towards
      item: yellow_stained_glass_pane
      name: "&e%skill% %level_numeral%"
      lore:
        - "&f"
        - "&fRewards:"
        - "%rewards%"
        - "&f"
        - "&fProgress:"
        - "&8» &e%percentage_progress%%"
        - "&8» &e%current_xp%&8/&7%required_xp% &fXP"
    locked: # Node style for a level not yet reached
      item: red_stained_glass_pane
      name: "&c%skill% %level_numeral%"
      lore:
        - "&f"
        - "&fRewards:"
        - "%rewards%"

  custom-slots: [ ] # Extra GUI slots; see https://plugins.auxilor.io/all-plugins/custom-gui-slots

persistent-action-bar: # A constant action bar showing health and stats to online players
  enabled: true # Whether the persistent action bar is shown
  require-permission: false # If true, only players with 'ecoskills.enable-persistent-action-bar' see it
  scale-health: true # Scale displayed health so it always reads as 10 hearts
  format: "&c❤ &f%health%&8/&f%max_health%                 &#e884b0🛡 &f%ecoskills_defense% &8| &#db0000🗡 &f%ecoskills_strength% &8| &#40ffe6✦ &f%ecoskills_speed%" # The action bar format; an alternative mana format is "&c❤ &f%health%&8/&f%max_health%                  &f%ecoskills_mana%&8/&f%ecoskills_mana_limit% &#40ffe6🌊"
  disabled-in-worlds: [ ] # Worlds the action bar is hidden in

damage-indicators: # Floating damage numbers; requires a compatible hologram plugin
  enabled: true # Whether damage indicators are shown
  final-damage: false # Use final damage with reductions applied, instead of raw damage
  disabled-for-entities: # Entity types that never show indicators
    - area_effect_cloud
    - armor_stand
    - arrow
    - block_display
    - dragon_fireball
    - dropped_item
    - egg
    - ender_crystal
    - ender_pearl
    - ender_signal
    - evoker_fangs
    - experience_orb
    - fireball
    - falling_block
    - firework_rocket
    - fishing_hook
    - glow_item_frame
    - interaction
    - item_display
    - item_frame
    - leash_hitch
    - lightning
    - llama_spit
    - marker
    - minecart
    - minecart_chest
    - minecart_command
    - minecart_furnace
    - minecart_hopper
    - minecart_mob_spawner
    - minecart_tnt
    - painting
    - primed_tnt
    - shulker_bullet
    - small_fireball
    - snowball
    - spectral_arrow
    - splash_potion
    - text_display
    - thrown_exp_bottle
    - trident
    - wind_charge
    - wither_skull
  format:
    normal: "&7%damage%" # Format for normal hits
    crit: "&f✧ <gradient:#f953c6>%damage%</gradient:#b91d73> &f✧" # Format for critical hits
  healing:
    enabled: true # Whether healing numbers are shown
    format: "&a+%damage%" # Format for healing
  max-x-offset: 0.6 # Random spawn offset on the X axis
  max-y-offset: 0.6 # Random spawn offset on the Y axis
  max-z-offset: 0.6 # Random spawn offset on the Z axis

skills:
  prevent-levelling-while-afk: true # Don't grant XP while the player is AFK

  gain-xp: # How skill XP gain is announced
    action-bar:
      enabled: true # Whether the action bar is used
      message: "&f%skill% &8| &9(%current_xp%/%required_xp%) &e+%gained_xp%" # The action bar message
    boss-bar:
      enabled: false # Whether the boss bar is used
      format: "&f%skill% &8| &9(%current_xp%/%required_xp%)" # The boss bar message
      color: blue # Bar color; see the Spigot BarColor enum
      style: solid # Bar style; see the Spigot BarStyle enum
      duration: 2500 # How long the boss bar stays, in milliseconds
    sound:
      enabled: true # Whether a sound is played
      sound: entity_experience_orb_pickup # The sound played
      pitch: 2 # Pitch, between 0.5 and 2
      volume: 0.1 # Volume
      category: PLAYER # Sound category

  level-up: # How skill level-ups are announced
    message:
      enabled: true # Whether a chat message is sent
      message:
        - "&f"
        - " &#ff00aeYou levelled up &d%skill%&#ff00ae to &eLevel %level_numeral%&#ff00ae!"
        - "&f"
        - " &#ff00ae&lREWARDS:"
        - "%rewards%"
        - "&f"
    title:
      enabled: false # Whether a title is shown
      fade-in: 0.5 # Fade-in time, in seconds
      stay: 2 # On-screen time, in seconds
      fade-out: 0.5 # Fade-out time, in seconds
      title: "&a%skill% &6levelled up!"
      subtitle: "&6%previous_level_numeral% &8» &6%level_numeral%"
    sound:
      enabled: true # Whether a sound is played
      sound: entity_player_levelup # The sound played
      pitch: 0.8 # Pitch, between 0.5 and 2
      volume: 1.0 # Volume
```

<hr/>

## Where to go next

- **Make a skill:** [How to make a Skill](how-to-make-a-skill) for the per-skill config files this GUI displays.
- **Custom GUI slots:** add your own buttons with [Custom GUI Slots](https://plugins.auxilor.io/all-plugins/custom-gui-slots).
- **Commands:** the reload and admin commands live in [Commands and Permissions](commands-and-permissions).