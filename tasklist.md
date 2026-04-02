# Task List

## Tasks

### Project Setup
- [ ] Create Android project
  - Use Kotlin + Jetpack Compose
  - Min SDK 24+
  - Enable ViewModel + Compose dependencies
  - Acceptance criteria:
    - App runs successfully on emulator/device
    - Blank Compose screen visible

- [ ] Setup project structure (MVVM)
  - Create packages:
    - ui
    - viewmodel
    - data
    - domain
  - Acceptance criteria:
    - Clean separation of layers
    - No business logic inside UI

### Data Layer
- [ ] Create data models (`Dish`, `Ingredient`)
  - `Dish`: id, name
  - `Ingredient`: dishId, name, perPersonQty, unit
  - Acceptance criteria:
    - Models compile
    - Can create sample objects

- [ ] Create static dataset
  - Add 4 dishes:
    - Chicken Curry
    - Rice
    - Roti
    - Dal
  - Map ingredients to dishes
  - Acceptance criteria:
    - Data available as in-memory list
    - No DB required for MVP

### Core Logic
- [ ] Implement calculation engine
  - Input:
    - selected dish IDs
    - number of people
  - Logic:
    - Multiply per-person qty
    - Merge same ingredients
  - Acceptance criteria:
    - Correct aggregation
    - Example:
      - 100 people + Chicken Curry -> 25kg chicken

- [ ] Add unit conversion logic
  - Convert:
    - grams -> kg
    - ml -> liters
  - Acceptance criteria:
    - Output is human readable (kg/liter)
    - No raw grams shown for large values

### ViewModel
- [ ] Create `MainViewModel`
  - Hold:
    - list of dishes
    - selected dishes
    - result state
  - Functions:
    - `toggleDish()`
    - `calculate()`
  - Acceptance criteria:
    - UI reacts to state changes
    - Calculation updates result

### UI - Input Screen
- [ ] Build input screen layout
  - Field: number of people
  - List: dishes with checkboxes
  - Button: Calculate
  - Acceptance criteria:
    - User can input number
    - User can select/deselect dishes

- [ ] Add validation
  - Prevent:
    - empty input
    - zero people
  - Acceptance criteria:
    - Error message shown
    - Calculation blocked on invalid input

### UI - Result Screen
- [ ] Build result screen
  - Show ingredient list
  - Large readable text
  - Show unit conversion (kg/liter)
  - Acceptance criteria:
    - All ingredients visible
    - Values look correct

- [ ] Improve readability
  - Add spacing
  - Use bigger fonts
  - Simple clean layout
  - Acceptance criteria:
    - Readable from distance
    - Non-technical friendly

### Share Feature
- [ ] Generate share text
  - Format:
    - "Samaan list"
    - ingredient + quantity
  - Acceptance criteria:
    - Text is clean and readable

- [ ] Implement Android share intent
  - Use `ACTION_SEND`
  - Share plain text
  - Acceptance criteria:
    - Opens WhatsApp/share apps
    - Text appears correctly

### Testing
- [ ] Test with sample inputs
  - Example:
    - 50, 100, 200 people
  - Acceptance criteria:
    - No crashes
    - Output scales correctly

- [ ] Manual real-world validation
  - Show to 2-3 caterers
  - Ask for feedback
  - Acceptance criteria:
    - Feedback collected
    - At least 1 improvement identified

### Polish (Optional but Recommended)
- [ ] Add Hindi labels
  - Example:
    - "Kitne log?"
    - "Kya banana hai?"
  - Acceptance criteria:
    - UI understandable without English

- [ ] Add loading/feedback state
  - Button click feedback
  - Acceptance criteria:
    - No confusion on tap

### Release Prep
- [ ] Create app icon + name
  - Simple recognizable branding
  - Acceptance criteria:
    - App installs with proper icon

- [ ] Build APK
  - Generate debug/release build
  - Acceptance criteria:
    - APK installs successfully

### Definition of Done (MVP)
- [ ] User opens app
- [ ] Enters number of people
- [ ] Selects dishes
- [ ] Clicks calculate
- [ ] Gets ingredient list
- [ ] Can share via WhatsApp
- [ ] All within 10 seconds without guidance
