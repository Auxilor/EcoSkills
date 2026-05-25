---
title: How to make a Stat
sidebar_position: 2
---

## Stats
Stats are the base values that lie at the core of EcoSkills. Think of them like attributes in vanilla, but they're shown to the player.

## How to add stats
Each stat is its own config file, placed in the `/stats/` folder, and you can add or remove them as you please. There's an example config called `_example.yml` to help you out!

The ID of the Stat is the file name. This is what you use in commands, effects and placeholders.
ID's must be lowercase letters, numbers, and underscores only.

## Example Stat Config

```yaml
name: "&#f5aa42🍖 Saturation" # The name of the stat, shown to players
placeholder: "%level% / 3" # The placeholder to be shown in the description, you can use expressions - eg %level% * 2
description: "&8Lose &a%placeholder%%&8 less hunger" # The description to be shown in lore and messages

# Options for the stat in the GUI
gui:
  enabled: false # (Optional) If the stat should show up in the GUI
  icon: player_head texture:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDMzZGRiOTJjYjZiM2E3OTI4MGI4YmRjZWQ4OTc2YWVhYjEzYTRiZmZlYWVmMmQ0NmQ4MjhiZDkxZGVlMGYzZSJ9fX0="
  position:
    row: 5
    column: 5

# The effects of the stat (i.e. the functionality)
# See here: https://plugins.auxilor.io/effects/configuring-an-effect
# Use %level% as a placeholder for the stat level
effects:
  - id: hunger_multiplier
    args:
      multiplier: "1 - (%level% / 300)"

# The conditions required for the effects to activate,
# you can use %level% as a placeholder here too
conditions: [ ]
```

## Understanding all the sections

### The Stat Info Section

```yaml
name: "&#f5aa42🍖 Saturation" # The name shown to players
placeholder: "%level% / 3" # Value used in the description
description: "&8Lose &a%placeholder%%&8 less hunger" # Lore/message description
```

### The GUI Section

```yaml
# Options for the /stats GUI
gui:
  enabled: false # If this stat should show in /stats
  # Icon format: https://plugins.auxilor.io/the-item-lookup-system
  icon: player_head texture:"..."
  position:
    row: 5
    column: 5
```

### The Effects Section

```yaml
# The functionality of the stat.
# %level% is the level of this stat.
effects:
  - id: hunger_multiplier
    args:
      multiplier: "1 - (%level% / 300)"
```

### The Conditions Section

```yaml
# Conditions that must be met for this stat to activate.
conditions: [ ]
```

The effects section is the core functionality of the stat. You can configure effects, conditions, filters, and mutators in this section to run while this stat is levelled and active.

Check out [Configuring an Effect](https://plugins.auxilor.io/effects/configuring-an-effect) to understand how to configure this section correctly.

For more advanced users or setups, you can configure chains in this section to string together different effects under one trigger. Check out [Configuring an Effect Chain](https://plugins.auxilor.io/effects/configuring-a-chain) for more info.

## Internal Placeholders

| Placeholder       | Value                                                        |
| ----------------- | ------------------------------------------------------------ |
| `%level%`         | The player's stat level. Useful for creating scaling effects |
| `%level_numeral%` | The player's stat level shown in Roman Numerals              |
| `%level_x%`       | The player's stat level, +/- a value. eg. `%level_-1%`      |
| `%level_x_numeral%` | The player's stat level, +/- a value, shown as Numerals    |

<hr/>

## Default configs
The default configs can be found [here](https://github.com/Auxilor/EcoSkills/tree/master/eco-core/core-plugin/src/main/resources/stats). <br/>
You can find additional user-created configs on [lrcdb](https://lrcdb.auxilor.io/).

<hr/>

## Default Stats

| Stat         | Description                                         |
|--------------|-----------------------------------------------------|
| Defense      | Reduces Incoming Damage                             |
| Strength     | Increases Outgoing Damage                           |
| Crit Chance  | Increases the chance to deal a crit                 |
| Crit Damage  | Increases crit damage                               |
| Speed        | Increases movement speed                            |
| Wisdom       | Increases experience gained (and max mana if using) |
| Health       | Increases max health                                |
| Ferocity     | Chance to hit twice                                 |
| Attack Speed | Increases your attack speed                         |
