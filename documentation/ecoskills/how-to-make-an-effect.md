---
title: "How to Make an Effect"
sidebar_position: 3
---

An **effect** is a special ability granted to a player and levelled up through skills. Its **effects** block runs while the ability is active, scaling with its **level**. This page takes you from an empty file to a working effect, then breaks down every part of the config.

## Quick start

1. Open the `/plugins/EcoSkills/effects/` folder.
2. Copy `_example.yml` and rename the copy to your effect's ID, e.g. `midas_touch.yml`. The file name is the ID.
3. Set the `name`, `placeholder`, and `description`.
4. Add the `effects` that make the ability do something.
5. Run `/ecoskills reload`.
6. Run `/ecoskills give <player> midas_touch 5`, then trigger the ability (e.g. mine a block) and watch it fire.

:::tip
`_example.yml` is included as a reference and is **never loaded**, so copy or rename it to make a real effect. You can also organise effects into subfolders inside `effects/`, and they'll still load.
:::

## Naming and IDs

The file name without `.yml` is the effect's ID. That ID is what you use in commands, skill rewards, and placeholders. For item textures and icons, see the [Item Lookup System](https://plugins.auxilor.io/the-item-lookup-system).

:::warning ID rules
IDs may only contain lowercase letters, numbers, and underscores (a-z, 0-9, _). No spaces, capitals, or hyphens, or the effect will not load.
:::

## The structure of an effect

| Part | What it controls |
| --- | --- |
| **Info** | The name, description, and value shown to players |
| **Effects** | What the ability does while active |
| **Conditions** | Requirements for the ability to activate |

```yaml
# === Info: name, value, description ===
name: "Midas Touch" # Display name shown to players
placeholder: "%level% / 50" # Value shown in the description; supports expressions
description: "&a%placeholder%%&8 chance to get $50 every time you mine a block" # Description shown in lore and messages

# === Effects: what the ability does ===
effects:
  - id: give_money
    args:
      chance: "%level% / 50" # %level% is the effect level
      amount: 50
    triggers:
      - mine_block # The trigger that fires the effect

# === Conditions: requirements to activate ===
conditions: [ ] # Conditions that must pass for the effect to activate
```

### Info

The display fields and the value players see for the ability.

```yaml
name: "Midas Touch" # Display name shown to players
placeholder: "%level% / 50" # Value shown in the description; supports expressions
description: "&a%placeholder%%&8 chance to get $50 every time you mine a block" # Description shown in lore and messages
```

### Effects

What the ability does while it is active. Use `%level%` to scale with the effect level.

```yaml
effects:
  - id: give_money
    args:
      chance: "%level% / 50" # %level% is the effect level
      amount: 50
    triggers:
      - mine_block # The trigger that fires the effect
```

:::danger Effects are their own system
The effects, conditions, filters, and mutators here are the shared libreforge system, documented in full elsewhere. Read these before going deep:

- [Configuring an Effect](https://plugins.auxilor.io/effects/configuring-an-effect)
- [Configuring an Effect Chain](https://plugins.auxilor.io/effects/configuring-a-chain)
:::

### Conditions

Conditions that must pass for the ability to activate.

```yaml
conditions: [ ] # Empty means the ability always activates
```

## Internal placeholders

| Placeholder | Value |
| --- | --- |
| `%level%` | The player's effect level, for scaling effects |
| `%level_numeral%` | The player's effect level as a Roman numeral |
| `%level_x%` | The effect level offset by a value, e.g. `%level_-1%` is the current level minus 1 |
| `%level_x_numeral%` | The offset effect level as a Roman numeral |

:::tip Troubleshooting
- **Effect not loading?** Check the file name: lowercase letters, numbers, and underscores only, and not prefixed with `_`.
- **Ability never firing?** Confirm the `triggers` match the action and that `conditions` aren't blocking it.
- **Nothing happening on level up?** Make sure a skill grants this effect as a reward, or give it manually with `/ecoskills give`.
:::

<hr/>

## Where to go next

- **Default effects:** the shipped effect configs are [on GitHub](https://github.com/Auxilor/EcoSkills/tree/master/eco-core/core-plugin/src/main/resources/effects); community configs are on [lrcdb](https://lrcdb.auxilor.io/).
- **Grant effects from skills:** hand effects out as level rewards in [How to make a Skill](how-to-make-a-skill).
- **Configure effects:** the effects system is covered in [Configuring an Effect](https://plugins.auxilor.io/effects/configuring-an-effect).