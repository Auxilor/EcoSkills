# `give_skill_xp_naturally`
:::infoRequires:
EcoSkills
:::

:::dangerTriggered Effect
This effect requires a [Trigger](https://plugins.auxilor.io/effects/all-triggers) to activate.
:::

Gives naturally-gained experience points for a certain skill

This will send a message to a player and will include multipliers.
# Effect Syntax
```yaml
- id: give_skill_xp_naturally
  args:
    amount: 100 # The amount of xp to give
    skill: exploration # The skill to give the xp for
  ...other config (eg triggers, filters, mutators, etc)
```