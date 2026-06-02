---
title: "Commands and Permissions"
sidebar_position: 7
---

This page lists every EcoSkills command and the permission that gates it, plus the extra permissions you can grant for XP multipliers. Use these to set up who can open the GUIs, manage player data, and earn boosted XP.

| Command                                             | Description                                            | Permission                  |
| --------------------------------------------------- | ------------------------------------------------------ | --------------------------- |
| `/skills`                                           | Opens the skills GUI                                   | `ecoskills.command.skills`  |
| `/stats`                                            | Opens the stats GUI                                    | `ecoskills.command.skills`  |
| `/skills top`                                       | View the leaderboard                                   | `ecoskills.command.top`     |
| `/ecoskills give <player> <skill/stat> <xp/levels>` | Give a player XP or stat levels                        | `ecoskills.command.give`    |
| `/ecoskills reset <player>`                         | Reset a player                                         | `ecoskills.command.reset`   |
| `/ecoskills recount <player>`                       | Recount a player's stat/effect levels                 | `ecoskills.command.recount` |
| `/ecoskills import <id>`                            | Import a skill from [lrcdb](https://lrcdb.auxilor.io/) | `ecoskills.command.import`  |
| `/ecoskills export <id>`                            | Export a skill to [lrcdb](https://lrcdb.auxilor.io/)   | `ecoskills.command.export`  |

### Additional permissions

| Permission                           | Description                                                                                            |
| ------------------------------------ | ----------------------------------------------------------------------------------------------------- |
| `ecoskills.xpmultiplier.<%increase>` | Multiply skill XP gain. The math is `1 + (<%increase> / 100)`, e.g. `200` is 3x XP and `50` is 1.5x XP |
| `ecoskills.xpmultiplier.50percent`   | Gives 50% more skill XP (1.5x multiplier)                                                              |
| `ecoskills.xpmultiplier.double`      | Gives double skill XP (2x multiplier)                                                                  |
| `ecoskills.xpmultiplier.triple`      | Gives triple skill XP (3x multiplier)                                                                  |
| `ecoskills.xpmultiplier.quadruple`   | Gives quadruple skill XP (4x multiplier)                                                               |

<hr/>

## Where to go next

- **Configure the GUIs:** customise what `/skills` and `/stats` open in [Plugin Config](plugin-config).
- **Make a skill:** create the skills these commands grant XP for in [How to make a Skill](how-to-make-a-skill).