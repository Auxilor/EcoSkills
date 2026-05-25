---
title: How to configure Magic
sidebar_position: 4
---

## Magic
Magic is an optional feature in EcoSkills that provides naturally regenerating resources like Mana, which can then be used in other plugins (for example EcoItems or EcoEnchants).

## How to add magic types
Each magic type is its own config file, placed in the `/magic_types/` folder, and you can add or remove them as you please. There's an example config called `_example.yml` to help you out!

The ID of the Magic is the file name. This is what you use in commands, effects and placeholders.
IDs must be lowercase letters, numbers, and underscores only.

## Example Magic Config

```yaml
name: "&#40ffe6🌊 Mana"

regen-rate: "0.02 * %ecoskills_mana_limit%"

limit: "100 + %ecoskills_wisdom%"

join-on-full: true
```

## Understanding all the sections

### The Magic Info Section

```yaml
name: "&#40ffe6🌊 Mana" # The name shown to players
```

### The Regeneration Section

```yaml
# The rate this magic regenerates per second
regen-rate: "0.02 * %ecoskills_mana_limit%"

# The maximum amount of this magic a player can have
limit: "100 + %ecoskills_wisdom%"
```

### The Join Behavior Section

```yaml
# If players should join with full magic (true) or empty magic (false)
join-on-full: true
```

## Using Magic in Effects
You can use your magic in effects by utilising the [price](https://plugins.auxilor.io/all-plugins/prices) system.

### Optional Effect Arguments
You can use optional effect arguments to consume magic in your effects.

#### `<magic>_cost`

The magic cost (e.g. mana) required to use or activate this effect.

```yaml
args:
  mana_cost: 10
```

#### `price`

The price required to use or activate this effect.

This supports all known prices (money, items, points, second currencies, etc.).
Read more about the system here: [Prices](https://plugins.auxilor.io/all-plugins/prices)

Example config:

```yaml
args:
  price:
    value: 100 * %v%
    type: mana
    display: "&#40ffe6%value% 🌊 Mana"
```

<hr/>

## Default configs
The default configs can be found [here](https://github.com/Auxilor/EcoSkills/tree/master/eco-core/core-plugin/src/main/resources/magic_types).
