# `add_stat_temporarily`
:::infoRequires:
EcoSkills
:::

:::dangerTriggered Effect
This effect requires a [Trigger](https://plugins.auxilor.io/effects/all-triggers) to activate.
:::

Adds a value to a specific stat
# Effect Syntax
```yaml
- id: add_stat_temporarily
  args:
    stat: strength # The name of the stat
    amount: 10 # The amount to add (or subtract, allows negative values)
    duration: 20 # The duration (in ticks)
  ...other config (eg triggers, filters, mutators, etc)
```
