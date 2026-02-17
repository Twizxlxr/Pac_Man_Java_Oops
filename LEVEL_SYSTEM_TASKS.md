# Level-Based Progression System Implementation Tasks

## Overview
Implement a dynamic level system where ghost speed, PacMan speed, and point values increase with each level progression.

---

## Task 1: Create Level Configuration Class
**Description:** Create a new class to manage level-based configuration and difficulty scaling.

### Steps:
- [ ] Create `src/main/java/com/pacman/core/LevelConfig.java`
- [ ] Add static fields:
  - `int currentLevel = 1`
  - `float ghostSpeedMultiplier = 1.0f` (starts at 1.0, increases by 0.1 per level)
  - `float pacmanSpeedMultiplier = 1.0f` (starts at 1.0, increases by 0.05 per level)
  - `int pacGumPoints = 10` (base points)
  - `int superPacGumPoints = 100` (base points)
  - `int ghostPoints = 500` (base points)
- [ ] Add method: `public static void nextLevel()`
  - Increment currentLevel
  - Update speed multipliers
  - Update point values
- [ ] Add getter methods:
  - `getCurrentLevel()`
  - `getGhostSpeedMultiplier()`
  - `getPacmanSpeedMultiplier()`
  - `getPacGumPoints()`
  - `getSuperPacGumPoints()`
  - `getGhostPoints()`
- [ ] Add method: `public static void resetToLevel1()`
  - Reset all values to initial state when game restarts

---

## Task 2: Modify Ghost Speed System
**Description:** Update ghost speed to use multiplier from LevelConfig.

### Steps:
- [ ] In `Ghost.java`:
  - Modify constructor to apply speed multiplier
  - Store base speed as constant (e.g., `BASE_SPEED = 1`)
  - Apply: `super(32, xPos, yPos, (int)(BASE_SPEED * LevelConfig.getGhostSpeedMultiplier()), ...)`
- [ ] Create method `void updateSpeedForLevel()`
  - Called when level advances
  - Recalculates speed based on new multiplier
- [ ] In `Game.java`:
  - Call ghost speed update after level complete

---

## Task 3: Modify PacMan Speed System
**Description:** Update PacMan speed to use multiplier from LevelConfig.

### Steps:
- [ ] In `PacMan.java`:
  - Store base speed as constant (e.g., `BASE_SPEED = 2`)
  - Modify constructor/speed calculation to apply multiplier
  - Apply: `spd = (int)(BASE_SPEED * LevelConfig.getPacmanSpeedMultiplier())`
- [ ] Create method `void updateSpeedForLevel()`
  - Called when level advances
  - Recalculates speed based on new multiplier

---

## Task 4: Modify Point System
**Description:** Update point values based on current level.

### Steps:
- [ ] In `UIPanel.java`:
  - Modify `updateScore()` methods to use LevelConfig point values
  - Update `updatePacGumEaten()`: `score += LevelConfig.getPacGumPoints()`
  - Update `updateSuperPacGumEaten()`: `score += LevelConfig.getSuperPacGumPoints()`
  - Update `updateGhostCollision()`: `score += LevelConfig.getGhostPoints()`
- [ ] Add method `public void displayCurrentLevel()`
  - Display current level number in UI (optional, for better UX)

---

## Task 5: Implement Level Advancement
**Description:** Setup level progression when player wins a level.

### Steps:
- [ ] In `Game.java`:
  - Modify `nextLevel()` method to:
    1. Call `LevelConfig.nextLevel()`
    2. Update PacMan speed: `pacman.updateSpeedForLevel()`
    3. Update all ghost speeds:
       ```java
       for (Ghost gh : ghosts) {
           gh.updateSpeedForLevel();
       }
       ```
    4. Reload level entities (pellets, etc.)
    5. Reset positions
- [ ] In `UIPanel.java`:
  - Reset points to 0 OR accumulate score across levels
  - Display "Level X" message before level starts

---

## Task 6: Handle Game Restart
**Description:** Ensure level resets when player restarts game.

### Steps:
- [ ] In `GamePanel.java` (`restartGame()` method):
  - Add: `LevelConfig.resetToLevel1()` before creating new Game
- [ ] Verify all speeds and points reset correctly

---

## Task 7: Testing & Tuning
**Description:** Test level progression and balance difficulty scaling.

### Steps:
- [ ] Test Level 1: Verify baseline speeds and points
- [ ] Test Level 2: Verify speed increase (~10% for ghosts, ~5% for PacMan)
- [ ] Test Level 3+: Verify consistent scaling
- [ ] Test Restart: Verify reset to Level 1
- [ ] Tune multiplier values if needed:
  - Adjust `ghostSpeedMultiplier` increment
  - Adjust `pacmanSpeedMultiplier` increment
  - Consider alternative scaling formulas (linear vs exponential)

---

## Task 8: UI Display Improvements (Optional)
**Description:** Enhance UI to show level and difficulty info.

### Steps:
- [ ] Add level display to UIPanel
- [ ] Show current level in top-left or center
- [ ] Display "Level Up!" message on level advancement
- [ ] Show difficulty progression (e.g., "Difficulty: 1.2x")
- [ ] Display next level preview on game over

---

## Implementation Notes

### Speed Calculation Examples:
```
Level 1: Ghost Speed = 1 * 1.0 = 1.0
Level 2: Ghost Speed = 1 * 1.1 ≈ 1.1 (10% faster)
Level 3: Ghost Speed = 1 * 1.2 = 1.2 (20% faster)
Level 4: Ghost Speed = 1 * 1.3 = 1.3 (30% faster)

Level 1: PacMan Speed = 2 * 1.0 = 2.0
Level 2: PacMan Speed = 2 * 1.05 ≈ 2.1 (5% faster)
Level 3: PacMan Speed = 2 * 1.1 = 2.2 (10% faster)
```

### Point Scaling Options:
**Option A - Linear (Simple):**
```
pacGumPoints = 10 * currentLevel
superPacGumPoints = 100 * currentLevel
ghostPoints = 500 * currentLevel
```

**Option B - Moderate (Recommended):**
```
pacGumPoints = 10 + (currentLevel - 1) * 2
superPacGumPoints = 100 + (currentLevel - 1) * 20
ghostPoints = 500 + (currentLevel - 1) * 100
```

**Option C - Exponential:**
```
pacGumPoints = (int)(10 * Math.pow(1.1, currentLevel - 1))
```

---

## File Dependencies

| File | Changes | Depends On |
|------|---------|-----------|
| `LevelConfig.java` | NEW | None |
| `Game.java` | nextLevel() update | LevelConfig |
| `Ghost.java` | Speed multiplier | LevelConfig |
| `PacMan.java` | Speed multiplier | LevelConfig |
| `UIPanel.java` | Point values | LevelConfig |
| `GamePanel.java` | Reset on restart | LevelConfig |

---

## Estimated Complexity
- **Easy:** Tasks 1, 6
- **Medium:** Tasks 2, 3, 4, 5
- **Optional:** Task 7, 8

**Total Implementation Time:** 1-2 hours
