---
title: "PlaceholderAPI"
sidebar_position: 6
---

EcoSkills exposes its skill, stat, and effect data through PlaceholderAPI so you can show it anywhere placeholders are supported. Install PlaceholderAPI, then use any of the placeholders below. Swap `<id>` for a skill, stat, or effect ID.

| Placeholder                                          | Description                                                                       |
| ---------------------------------------------------- | -------------------------------------------------------------------------------- |
| `%ecoskills_average_skill_level%`                    | The player's average skill level                                                 |
| `%ecoskills_total_skill_level%`                      | The player's total skill level: all skill levels added together                  |
| `%ecoskills_<id>%`                                   | The level the player has for a given effect, stat, or skill                       |
| `%ecoskills_<id>_numeral%`                           | The level the player has for a given effect, stat, or skill, as a Roman numeral   |
| `%ecoskills_<effect>_name%`                          | The formatted name (icon and color) of an effect                                  |
| `%ecoskills_<effect>_base%`                          | The base level the player has for an effect, before modifiers                     |
| `%ecoskills_<effect>_bonus%`                         | The bonus levels the player has for an effect, from modifiers                     |
| `%ecoskills_<effect>_description%`                   | The description shown to the player for an effect                                 |
| `%ecoskills_<stat>_name%`                            | The formatted name (icon and color) of a stat                                     |
| `%ecoskills_<stat>_base%`                            | The base level the player has for a stat, before modifiers                        |
| `%ecoskills_<stat>_bonus%`                           | The bonus levels the player has for a stat, from modifiers                        |
| `%ecoskills_<stat>_description%`                     | The description shown to the player for a stat                                    |
| `%ecoskills_<skill>_percentage_progress%`            | The percentage progress to the next skill level                                   |
| `%ecoskills_<skill>_current_xp%`                     | The current skill XP                                                              |
| `%ecoskills_<skill>_required_xp%`                    | The skill XP required for the next level                                          |
| `%ecoskills_top_<id>_<position[0-9]>_<name/amount>%` | Leaderboard placeholder for a skill level, by position                            |

<hr/>

## Where to go next

- **Use these in config:** drop placeholders into lore and messages in [Plugin Config](plugin-config).
- **Make a skill:** define the skills and stats these placeholders read in [How to make a Skill](how-to-make-a-skill).