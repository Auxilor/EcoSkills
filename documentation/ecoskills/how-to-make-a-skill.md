---
title: How to make a Skill
sidebar_position: 1
---

## Skills
Skills are leveled up by completing certain tasks, and give effects, stats, and other bonuses when leveling up.

## How to add skills
Each skill is its own config file, placed in the `/skills/` folder, and you can add or remove them as you please. There's an example config called `_example.yml` to help you out!

The ID of the Skill is the file name. This is what you use in commands, effects and placeholders.
ID's must be lowercase letters, numbers, and underscores only.

## Example Skill Config

```yaml
name: Mining
description: Break blocks to earn XP
hide-before-level-1: true

gui:
  enabled: true
  icon: player_head texture:eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmIxYzI2OGVmZWM4ZDdkODhhMWNiODhjMmJmYTA5N2ZhNTcwMzc5NDIyOTlmN2QyMDIxNTlmYzkzY2QzMDM2ZCJ9fX0=
  lore:
    - "&fImproves Stats:"
    - "&8» &r%ecoskills_defense_name%"
    - "&8» &r%ecoskills_ferocity_name%"
    - "&f"
    - "&fEffects:"
    - "&8» &r&6%ecoskills_versatile_tools_name% %ecoskills_versatile_tools_numeral%"
    - "   %ecoskills_versatile_tools_description%"
    - "&8» &r&6%ecoskills_spelunking_name% %ecoskills_spelunking_numeral%"
    - "   %ecoskills_spelunking_description%"
    - "&8» &r&6%ecoskills_dynamic_mining_name% %ecoskills_dynamic_mining_numeral%"
    - "   %ecoskills_dynamic_mining_description%"
  position:
    row: 3
    column: 3

xp-requirements:
  - 50
  - 125
  - 200
  - 300
  - 500
  - 750
  - 1000
  - 1500
  - 2000
  - 3500
  - 5000
  - 7500
  - 10000

rewards:
  - reward: defense
    levels: 2

  - reward: ferocity
    levels: 1
    start-level: 15

  - reward: versatile_tools
    levels: 1

  - reward: spelunking
    levels: 1
    start-level: 10

  - reward: dynamic_mining
    levels: 1

level-up-effects:
  - id: give_money
    args:
      amount: 1000 * %level%
      require: "%level% > 50"

  - id: give_money
    args:
      amount: 3000 * %level%
      every: 2
      require: "%level% >= 50"

placeholders:
  money: "%level% * 0.4"
  blocks: "ceil(10 - %level% / 10)"

reward-messages:
  1:
    - " &8» &r&f+2 %ecoskills_defense_name%"
    - " &8» &r&6%ecoskills_versatile_tools_name% %ecoskills_versatile_tools_numeral%"
    - "    %ecoskills_versatile_tools_description%"
    - " &8» &r&6%ecoskills_dynamic_mining_name% %ecoskills_dynamic_mining_numeral%"
    - "    %ecoskills_dynamic_mining_description%"
  10:
    - " &8» &r&f+2 %ecoskills_defense_name%"
    - " &8» &r&6%ecoskills_versatile_tools_name% %ecoskills_versatile_tools_numeral%"
    - "    %ecoskills_versatile_tools_description%"
    - " &8» &r&6%ecoskills_spelunking_name% %ecoskills_spelunking_numeral%"
    - "    %ecoskills_spelunking_description%"
    - " &8» &r&6%ecoskills_dynamic_mining_name% %ecoskills_dynamic_mining_numeral%"
    - "    %ecoskills_dynamic_mining_description%"
  15:
    - " &8» &r&f+2 %ecoskills_defense_name%"
    - " &8» &r&f+1 %ecoskills_ferocity_name%"
    - " &8» &r&6%ecoskills_versatile_tools_name% %ecoskills_versatile_tools_numeral%"
    - "    %ecoskills_versatile_tools_description%"
    - " &8» &r&6%ecoskills_spelunking_name% %ecoskills_spelunking_numeral%"
    - "    %ecoskills_spelunking_description%"
    - " &8» &r&6%ecoskills_dynamic_mining_name% %ecoskills_dynamic_mining_numeral%"
    - "    %ecoskills_dynamic_mining_description%"

xp-gain-methods:
  - trigger: break_block
    multiplier: 0.5
    filters:
      blocks:
        - netherrack

  - trigger: break_block
    multiplier: 1
    filters:
      blocks:
        - stone
        - diorite
        - granite
        - andesite
        - cobblestone

conditions: [ ]
```

## Understanding all the sections

### The Skill Info Section

```yaml
name: Mining # The display name of the skill
description: Break blocks to earn XP # The description shown in GUIs
hide-before-level-1: true # Show this as "Unknown" before level 1
```

### The GUI Section

```yaml
# Options for the /skills GUI
gui:
  enabled: true # If this skill should show in /skills
  # Icon format: https://plugins.auxilor.io/the-item-lookup-system
  icon: player_head texture:"..."
  lore:
    - "&fImproves Stats:"
  position:
    row: 3
    column: 3
```

### The Progression Section

#### XP Requirements

There are two ways to specify level XP requirements:
1. A formula to calculate for infinite levels

```yaml
xp-formula: (2 ^ %level%) * 25 # The formula for each level's XP requirement. See math docs: https://plugins.auxilor.io/all-plugins/math
max-level: 100 # (Optional) If not set, there is no max level
```

2. A list of XP requirements for each level

```yaml
xp-requirements: # XP required to reach each level, from level 1
  - 50 # XP required to reach level 1
  - 125 # XP required to reach level 2
  - 200
```

#### XP Gain Methods

```yaml
# An XP gain method takes a trigger, a multiplier/value, optional args,
# optional local conditions, and optional filters.
xp-gain-methods:
  - trigger: mine_block
    multiplier: 0.5 # You can also use "value" here for fixed XP
    args:
      chance: 50
    filters:
      blocks:
        - netherrack
```

### The Rewards Section

```yaml
# Rewards must be a stat or effect id.
# Stats: https://plugins.auxilor.io/ecoskills/how-to-make-a-stat
# Effects: https://plugins.auxilor.io/ecoskills/how-to-make-an-effect
rewards:
  - reward: defense
    levels: 2

  - reward: ferocity
    levels: 1
    start-level: 15

  - reward: dynamic_mining
    levels: 1
    every: 2
```

- `reward`: The ID of the stat/effect reward.
- `levels`: The amount of levels to grant for that reward.
- `start-level`: (Optional) Level where this reward starts (inclusive).
- `end-level`: (Optional) Level where this reward stops (inclusive).
- `every`: (Optional) Apply this reward every x levels.

### The Additional Options Section

```yaml
# Custom placeholders used in lore/messages.
# IDs should not include % symbols.
placeholders:
  money: "%level% * 0.4"
  blocks: "ceil(10 - %level% / 10)"

# Lore/messages shown for %rewards% and level-up text.
# Keys are minimum levels; higher keys override lower ones.
reward-messages:
  1:
    - " &8» &r&f+2 %ecoskills_defense_name%"
```

### The Level Up Section
:::danger Effects Section

The effects section is the core functionality of the skill. You can configure effects, conditions, filters, and mutators in this section to run when the skill levels up.

Check out [Configuring an Effect](https://plugins.auxilor.io/effects/configuring-an-effect) to understand how to configure this section correctly.

For more advanced users or setups, you can configure chains in this section to string together different effects under one trigger. Check out [Configuring an Effect Chain](https://plugins.auxilor.io/effects/configuring-a-chain) for more info.

:::
```yaml
# Effects that run when the skill levels up.
# %level% is the level reached after levelling up.
level-up-effects: # Triggers are *not required* here, these effects run on level up.
  - id: run_command
    args:
      command: "give %player% diamond 1"
      require: "%level% < 10"
```

### The Global Conditions Section

```yaml
# Global conditions that must be met to gain skill XP.
conditions: [ ]
```

See [Configuring a Condition](https://plugins.auxilor.io/effects/configuring-a-condition).

## Internal Placeholders

| Placeholder       | Value                                                                      |
| ----------------- |----------------------------------------------------------------------------|
| `%level%`         | The player's skill level. Useful for creating scaling effects              |
| `%level_numeral%` | The player's skill level shown in Roman Numerals                           |
| `%level_x%`         | The player's skill level, +/- a value. eg. `%level_-1%` is current level-1 |
| `%level_x_numeral%` | The player's skill level, +/- a value, shown as Numerals                   |
| `%previous_level%` | The player's previous skill level                                          |
| `%previous_level_numeral%` | The player's previous skill level shown in Roman Numerals          |

<hr/>

## Default configs
The default configs can be found [here](https://github.com/Auxilor/EcoSkills/tree/master/eco-core/core-plugin/src/main/resources/skills). <br/>
You can find additional user-created configs on [lrcdb](https://lrcdb.auxilor.io/).

<hr/>

## Default Skills

| Skill       | Task                      |
|-------------|---------------------------|
| Mining      | Break blocks to earn XP   |
| Combat      | Kill mobs to earn XP      |
| Enchanting  | Enchant items to earn XP  |
| Farming     | Harvest crops to earn XP  |
| Woodcutting | Cut down trees to earn XP |
| Fishing     | Fish to earn XP           |
| Alchemy     | Brew potions to earn XP   |
| Armory      | Take damage to earn XP    |
| Exploration | Move to earn XP           |