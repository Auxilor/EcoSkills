# `skill_xp_multiplier`
:::dangerPermanent Effect
This effect is permanent and does not require a trigger.
:::

Multiplies skill xp gain

**Requires EcoSkills / AuraSkills**

# Effect Syntax
```yaml
- id: skill_xp_multiplier
  args:
    multiplier: 1.5 # The experience multiplier
    skills: # The list of skills to multiply xp for. If removed, it will multiply all skills.
      - mining
      - combat 
```
