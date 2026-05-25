---
title: "Commands and Permissions"
sidebar_position: 7
---

| Command                                             | Description                                            | Permission                  |
|-----------------------------------------------------|--------------------------------------------------------|-----------------------------|
| `/skills`                                           | Opens the skills GUI                                   | `ecoskills.command.skills`  |
| `/stats`                                            | Opens the stats GUI                                    | `ecoskills.command.skills`  |
| `/skills top`                                       | View the leaderboard                                   | `ecoskills.command.top`     |
| `/ecoskills give <player> <skill/stat> <xp/levels>` | Give a player XP or stat levels                        | `ecoskills.command.give`    |
| `/ecoskills reset <player>`                         | Reset a player                                         | `ecoskills.command.reset`   |
| `/ecoskills recount <player>`                       | Recount a player's stat/effect levels                  | `ecoskills.command.recount` |
| `/ecoskills import <id>`                            | Import a skill from [lrcdb](https://lrcdb.auxilor.io/) | `ecoskills.command.import`  |
| `/ecoskills export <id>`                            | Export a skill to [lrcdb](https://lrcdb.auxilor.io/)   | `ecoskills.command.export`  |

### Additional Permissions

| Permission                           | Description                                                                                           |
|--------------------------------------|-------------------------------------------------------------------------------------------------------|
| `ecoskills.xpmultiplier.<%increase>` | Multiply skill XP gain. The math is `1 + (<%increase> / 100)`. Example: `200` = 3x XP, `50` = 1.5x XP |
| `ecoskills.xpmultiplier.50percent`   | Gives 50% more skill XP (1.5x multiplier)                                                             |
| `ecoskills.xpmultiplier.double`      | Gives double skill XP (2x multiplier)                                                                 |
| `ecoskills.xpmultiplier.triple`      | Gives triple skill XP (3x multiplier)                                                                 |
| `ecoskills.xpmultiplier.quadruple`   | Gives quadruple skill XP (4x multiplier)                                                              |
