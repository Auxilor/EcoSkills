# `multiply_stat_temporarily`
:::infoRequires:
EcoSkills
:::

:::dangerTriggered Effect
This effect requires a [Trigger](https://plugins.auxilor.io/effects/all-triggers) to activate.
:::

Multiplies a stat by a specific value
# Effect Syntax
```yaml
- id: multiply_stat_temporarily
  args:
    stat: strength # The name of the stat
    multiplier: 1.1 # The amount to multiply the stat by
    duration: 20 # The duration (in ticks)
  ...other config (eg triggers, filters, mutators, etc)
```
