---
title: "How to Make a Stat"
sidebar_position: 2
---

A **stat** is a base attribute at the core of EcoSkills, like vanilla attributes but visible to the player. Its **effects** apply continuously based on the stat's **level**, which skills raise as rewards. This page takes you from an empty file to a working stat, then breaks down every part of the config.

## Quick start

1. Open the `/plugins/EcoSkills/stats/` folder.
2. Copy `_example.yml` and rename the copy to your stat's ID, e.g. `saturation.yml`. The file name is the ID.
3. Set the `name`, `placeholder`, and `description`.
4. Add the `effects` that make the stat do something.
5. Run `/ecoskills reload`.
6. Run `/ecoskills give <player> saturation 5`, then `/stats` to confirm the stat shows and its effect applies.

:::tip
`_example.yml` is included as a reference and is **never loaded**, so copy or rename it to make a real stat. You can also organise stats into subfolders inside `stats/`, and they'll still load.
:::

## Naming and IDs

The file name without `.yml` is the stat's ID. That ID is what you use in commands, rewards, and placeholders. For item textures and icons, see the [Item Lookup System](https://plugins.auxilor.io/the-item-lookup-system).

:::warning ID rules
IDs may only contain lowercase letters, numbers, and underscores (a-z, 0-9, _). No spaces, capitals, or hyphens, or the stat will not load.
:::

## The structure of a stat

| Part | What it controls |
| --- | --- |
| **Info** | The name, description, and value shown to players |
| **GUI** | How the stat appears in the `/stats` menu |
| **Effects** | What the stat does while levelled |
| **Conditions** | Requirements for the effects to apply |

```yaml
# === Info: name, value, description ===
name: "&#f5aa42🍖 Saturation" # Display name shown to players
placeholder: "%level% / 3" # Value shown in the description; supports expressions, e.g. %level% * 2
description: "&8Lose &a%placeholder%%&8 less hunger" # Description shown in lore and messages

# === GUI: how it appears in /stats ===
gui:
  enabled: false # Optional; whether the stat shows in the /stats menu
  icon: player_head texture:eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDMzZGRiOTJjYjZiM2E3OTI4MGI4YmRjZWQ4OTc2YWVhYjEzYTRiZmZlYWVmMmQ0NmQ4MjhiZDkxZGVlMGYzZSJ9fX0= # Icon item; see the Item Lookup System for the format
  position: # Slot in the /stats menu
    row: 5
    column: 5

# === Effects: what the stat does ===
effects:
  - id: hunger_multiplier
    args:
      multiplier: "1 - (%level% / 300)" # %level% is the stat level

# === Conditions: requirements for the effects ===
conditions: [ ] # Conditions that must pass for the effects to apply
```

### Info

The display fields and the value players see for the stat.

```yaml
name: "&#f5aa42🍖 Saturation" # Display name shown to players
placeholder: "%level% / 3" # Value shown in the description; supports expressions, e.g. %level% * 2
description: "&8Lose &a%placeholder%%&8 less hunger" # Description shown in lore and messages
```

### GUI

How the stat appears in the `/stats` menu.

```yaml
gui:
  enabled: false # Optional; whether the stat shows in the /stats menu
  icon: player_head texture:"..." # Icon item; see the Item Lookup System for the format
  position:
    row: 5
    column: 5
```

### Effects

What the stat does while the player has it levelled. Use `%level%` to scale with the stat level.

```yaml
effects:
  - id: hunger_multiplier
    args:
      multiplier: "1 - (%level% / 300)" # %level% is the stat level
```

:::danger Effects are their own system
The effects, conditions, filters, and mutators here are the shared libreforge system, documented in full elsewhere. Read these before going deep:

- [Configuring an Effect](https://plugins.auxilor.io/effects/configuring-an-effect)
- [Configuring an Effect Chain](https://plugins.auxilor.io/effects/configuring-a-chain)
:::

### Conditions

Conditions that must pass for the stat's effects to apply. You can use `%level%` here too.

```yaml
conditions: [ ] # Empty means the effects always apply
```

## Internal placeholders

| Placeholder | Value |
| --- | --- |
| `%level%` | The player's stat level, for scaling effects |
| `%level_numeral%` | The player's stat level as a Roman numeral |
| `%level_x%` | The stat level offset by a value, e.g. `%level_-1%` is the current level minus 1 |
| `%level_x_numeral%` | The offset stat level as a Roman numeral |

:::tip Troubleshooting
- **Stat not loading?** Check the file name: lowercase letters, numbers, and underscores only, and not prefixed with `_`.
- **Effect not applying?** Confirm the `effects` id exists and that `conditions` aren't blocking it.
- **Stat missing from `/stats`?** Set `gui.enabled: true` and give it a free `position`.
:::

<hr/>

## Where to go next

- **Default stats:** the shipped stat configs are [on GitHub](https://github.com/Auxilor/EcoSkills/tree/master/eco-core/core-plugin/src/main/resources/stats); community configs are on [lrcdb](https://lrcdb.auxilor.io/).
- **Grant stats from skills:** hand stats out as level rewards in [How to make a Skill](how-to-make-a-skill).
- **Configure effects:** the effects system is covered in [Configuring an Effect](https://plugins.auxilor.io/effects/configuring-an-effect).