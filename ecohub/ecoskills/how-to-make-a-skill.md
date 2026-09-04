---
title: "How to Make a Skill"
sidebar_position: 1
---

A **skill** is something players level up by doing a task, like mining or fishing, earning **XP** until they hit the next level. Each level can hand out **rewards** (stats and effects) and run **effects**. This page takes you from an empty file to a working skill, then breaks down every part of the config.

## Quick start

1. Open the `/plugins/EcoSkills/skills/` folder.
2. Copy `_example.yml` and rename the copy to your skill's ID, e.g. `mining.yml`. The file name is the ID.
3. Set the `name`, `description`, and either `xp-requirements` or an `xp-formula`.
4. Add `xp-gain-methods` so players actually earn XP, and `rewards` for levelling up.
5. Run `/ecoskills reload`.
6. Run `/skills` to confirm the skill shows, then do its task (e.g. break a block) and watch the XP go up.

:::tip
`_example.yml` is included as a reference and is **never loaded**, so copy or rename it to make a real skill. You can also organise skills into subfolders inside `skills/`, and they'll still load.
:::

## Naming and IDs

The file name without `.yml` is the skill's ID. That ID is what you use in commands, rewards, and placeholders. For item textures and icons, see the [Item Lookup System](https://plugins.auxilor.io/the-item-lookup-system).

:::warning ID rules
IDs may only contain lowercase letters, numbers, and underscores (a-z, 0-9, _). No spaces, capitals, or hyphens, or the skill will not load.
:::

## The structure of a skill

| Part | What it controls |
| --- | --- |
| **Info** | The name, description, and visibility of the skill |
| **GUI** | How the skill appears in the `/skills` menu |
| **Progression** | The XP needed per level and how players earn it |
| **Rewards** | The stats and effects granted as the skill levels |
| **Level-up effects** | Effects that fire when the skill levels up |
| **Messages** | Custom placeholders and reward/level-up text |
| **Conditions** | Global requirements to gain any XP |

```yaml
# === Info: name, description, visibility ===
name: Mining # Display name shown in GUIs and messages
description: Break blocks to earn XP # Short description shown in GUIs
hide-before-level-1: true # Show the skill as "Unknown" until the player reaches level 1

# === GUI: how it appears in /skills ===
gui:
  enabled: true # Whether this skill shows in the /skills menu
  icon: player_head texture:eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmIxYzI2OGVmZWM4ZDdkODhhMWNiODhjMmJmYTA5N2ZhNTcwMzc5NDIyOTlmN2QyMDIxNTlmYzkzY2QzMDM2ZCJ9fX0= # Icon item; see the Item Lookup System for the format
  lore: # Lore lines; use ecoskills placeholders for live stat/effect values
    - "&fImproves Stats:"
    - "&8» &r%ecoskills_defense_name%"
  position: # Slot in the /skills menu
    row: 3
    column: 3

# === Progression: XP per level and how to earn it ===
xp-requirements: # XP to reach each level, starting at level 1
  - 50 # XP to reach level 1
  - 125 # XP to reach level 2
  - 200
xp-gain-methods: # How players earn XP for this skill
  - trigger: break_block # The trigger that grants XP
    multiplier: 1 # XP multiplier; use "value" instead for a fixed amount
    filters:
      blocks: # Only these blocks count
        - stone

# === Rewards: stats and effects granted on level up ===
rewards:
  - reward: defense # ID of the stat or effect to grant
    levels: 2 # Levels of that reward to add each time

# === Level-up effects: run when the skill levels up ===
level-up-effects:
  - id: give_money
    args:
      amount: 1000 * %level% # %level% is the level reached

# === Messages: custom placeholders and reward text ===
placeholders: # Custom placeholders for lore/messages; no % symbols in the keys
  money: "%level% * 0.4"
reward-messages: # Lore for %rewards%; keys are minimum levels, higher keys override lower
  1:
    - " &8» &r&f+2 %ecoskills_defense_name%"

# === Conditions: global requirements to gain XP ===
conditions: [ ] # Conditions that must pass for any XP to be granted
```

### Info

The display fields players see for the skill.

```yaml
name: Mining # Display name shown in GUIs and messages
description: Break blocks to earn XP # Short description shown in GUIs
hide-before-level-1: true # Show the skill as "Unknown" until the player reaches level 1
```

### GUI

How the skill appears in the `/skills` menu.

```yaml
gui:
  enabled: true # Whether this skill shows in the /skills menu
  icon: player_head texture:"..." # Icon item; see the Item Lookup System for the format
  lore:
    - "&fImproves Stats:"
    - "&8» &r%ecoskills_defense_name%"
  position:
    row: 3
    column: 3
```

### Progression

XP comes in two flavours: a fixed list per level, or a formula for unlimited levels. Use one or the other.

```yaml
xp-requirements: # Option 1: explicit XP to reach each level
  - 50 # XP to reach level 1
  - 125 # XP to reach level 2
  - 200
```

```yaml
xp-formula: (2 ^ %level%) * 25 # Option 2: XP per level from a formula; see https://plugins.auxilor.io/all-plugins/math
max-level: 100 # Optional; with a formula there is no max level unless you set one
```

Players earn that XP through `xp-gain-methods`, each tied to a trigger.

```yaml
xp-gain-methods:
  - trigger: break_block # The trigger that grants XP
    multiplier: 0.5 # XP multiplier; use "value" instead for a fixed amount
    args:
      chance: 50 # Optional trigger args
    filters:
      blocks: # Only these blocks count
        - netherrack
```

### Rewards

Each level can grant levels of a stat or effect. The `reward` is the ID of an existing stat or effect.

```yaml
rewards:
  - reward: defense # ID of the stat or effect to grant
    levels: 2 # Levels of that reward to add
  - reward: ferocity
    levels: 1
    start-level: 15 # Optional; first level this reward applies (inclusive)
    end-level: 50 # Optional; last level this reward applies (inclusive)
    every: 2 # Optional; apply only every X levels
```

### Level-up effects

Effects that fire the moment the skill levels up. Triggers are not required here; these run on level up.

```yaml
level-up-effects:
  - id: run_command
    args:
      command: "give %player% diamond 1"
      require: "%level% < 10" # %level% is the level reached after levelling up
```

:::danger Effects are their own system
The effects, conditions, filters, and mutators here are the shared libreforge system, documented in full elsewhere. Read these before going deep:

- [Configuring an Effect](https://plugins.auxilor.io/effects/configuring-an-effect)
- [Configuring an Effect Chain](https://plugins.auxilor.io/effects/configuring-a-chain)
:::

### Messages

Custom placeholders and the per-level reward text shown for `%rewards%`.

```yaml
placeholders: # Reusable expressions for lore/messages; keys carry no % symbols
  money: "%level% * 0.4"
  blocks: "ceil(10 - %level% / 10)"
reward-messages: # Keys are minimum levels; a higher key overrides a lower one
  1:
    - " &8» &r&f+2 %ecoskills_defense_name%"
```

### Conditions

Global conditions that must pass before the skill grants any XP.

```yaml
conditions: [ ] # Empty means no restriction
```

## Internal placeholders

| Placeholder | Value |
| --- | --- |
| `%level%` | The player's skill level, for scaling effects |
| `%level_numeral%` | The player's skill level as a Roman numeral |
| `%level_x%` | The skill level offset by a value, e.g. `%level_-1%` is the current level minus 1 |
| `%level_x_numeral%` | The offset skill level as a Roman numeral |
| `%previous_level%` | The player's previous skill level |
| `%previous_level_numeral%` | The previous skill level as a Roman numeral |

:::tip Troubleshooting
- **Skill not loading?** Check the file name: lowercase letters, numbers, and underscores only, and not prefixed with `_`.
- **Players not gaining XP?** Make sure `xp-gain-methods` has a trigger that matches the task, and that any `filters` aren't excluding it.
- **Skill missing from `/skills`?** Set `gui.enabled: true`, and remember it stays "Unknown" while `hide-before-level-1` is on and the player is below level 1.
:::

<hr/>

## Where to go next

- **Default skills:** the shipped skill configs are [on GitHub](https://github.com/Auxilor/EcoSkills/tree/master/eco-core/core-plugin/src/main/resources/skills); community configs are on [lrcdb](https://lrcdb.auxilor.io/).
- **Grant stats and effects:** define what your rewards point to in [How to make a Stat](how-to-make-a-stat) and [How to make an Effect](how-to-make-an-effect).
- **Configure effects:** the effects system is covered in [Configuring an Effect](https://plugins.auxilor.io/effects/configuring-an-effect).