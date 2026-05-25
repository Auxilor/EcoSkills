# `magic_regen_multiplier`
:::infoRequires:
EcoSkills
:::

:::dangerPermanent Effect
This effect is permanent and does not require a trigger.
:::

Multiplies magic regeneration
# Effect Syntax
```yaml
- id: magic_regen_multiplier
  args:
    multiplier: 1.5 # The regen multiplier
    types: # The list of magic types to multiply xp for. If removed, it will multiply all types.
      - mana
      - flux 
```
