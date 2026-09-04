---
title: "How to Configure Magic"
sidebar_position: 4
---

A **magic type** is an optional, naturally regenerating resource like **Mana** that other plugins (e.g. EcoItems, EcoEnchants) can spend. Each type defines its own **regen rate** and **limit**. This page takes you from an empty file to a working magic type, then breaks down every part of the config.

## Quick start

1. Open the `/plugins/EcoSkills/magic_types/` folder.
2. Copy `_example.yml` and rename the copy to your magic type's ID, e.g. `mana.yml`. The file name is the ID.
3. Set the `name`, `regen-rate`, and `limit`.
4. Set `join-on-full` to decide how players start.
5. Run `/ecoskills reload`.
6. Rejoin and watch `%ecoskills_mana%` regenerate towards `%ecoskills_mana_limit%`, e.g. in the action bar.

:::tip
`_example.yml` is included as a reference and is **never loaded**, so copy or rename it to make a real magic type. You can also organise magic types into subfolders inside `magic_types/`, and they'll still load.
:::

## Naming and IDs

The file name without `.yml` is the magic type's ID. That ID is what you use in commands, placeholders, and prices. For item textures and icons, see the [Item Lookup System](https://plugins.auxilor.io/the-item-lookup-system).

:::warning ID rules
IDs may only contain lowercase letters, numbers, and underscores (a-z, 0-9, _). No spaces, capitals, or hyphens, or the magic type will not load.
:::

## The structure of a magic type

| Part | What it controls |
| --- | --- |
| **Info** | The name shown to players |
| **Regeneration** | How fast the resource regenerates and its cap |
| **Join behavior** | Whether players join full or empty |

```yaml
# === Info: name ===
name: "&#40ffe6🌊 Mana" # Display name shown to players

# === Regeneration: rate and cap ===
regen-rate: "0.02 * %ecoskills_mana_limit%" # Amount regenerated per second; supports expressions
limit: "100 + %ecoskills_wisdom%" # Maximum a player can hold; supports expressions

# === Join behavior ===
join-on-full: true # true: players join with a full resource; false: empty
```

### Info

The display name players see for the resource.

```yaml
name: "&#40ffe6🌊 Mana" # Display name shown to players
```

### Regeneration

How fast the resource refills and the most a player can hold. Both accept expressions, so you can scale the cap off other stats.

```yaml
regen-rate: "0.02 * %ecoskills_mana_limit%" # Amount regenerated per second
limit: "100 + %ecoskills_wisdom%" # Maximum a player can hold
```

### Join behavior

Whether players come online with the resource full or empty.

```yaml
join-on-full: true # true: join full; false: join empty
```

:::info Spending magic in effects
You don't spend magic in this file; you spend it in effect configs through the shared [Prices](https://plugins.auxilor.io/all-plugins/prices) system. Add a `<id>_cost` arg for a quick deduction, e.g. `mana_cost: 10`, or a full `price` block typed to your magic ID for custom display and currencies.
:::

## Internal placeholders

| Placeholder | Value |
| --- | --- |
| `%ecoskills_<id>%` | The player's current amount of this magic |
| `%ecoskills_<id>_limit%` | The player's maximum for this magic |

:::tip Troubleshooting
- **Magic not loading?** Check the file name: lowercase letters, numbers, and underscores only, and not prefixed with `_`.
- **Resource never regenerates?** Confirm `regen-rate` is above 0 and `limit` resolves to a positive value.
- **Cost not being deducted?** The cost belongs in the effect's config via the price system, not in this file.
:::

<hr/>

## Where to go next

- **Default magic types:** the shipped configs are [on GitHub](https://github.com/Auxilor/EcoSkills/tree/master/eco-core/core-plugin/src/main/resources/magic_types).
- **Spend magic:** wire costs into abilities with the [Prices](https://plugins.auxilor.io/all-plugins/prices) system.
- **Make an effect:** build the abilities that consume magic in [How to make an Effect](how-to-make-an-effect).