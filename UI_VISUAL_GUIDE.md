# CleanCal UI Visual Guide

## Main Screen (Landscape 3840x2160)

### Layout Proportions
```
┌─────────────────────────────────────────────────────────────────┐
│                                                                 │ 95%
│                     CALENDAR VIEW AREA                          │ of
│                  (ViewPager2 - Swipeable)                       │ screen
│                                                                 │
├─────────────────────────────────────────────────────────────────┤
│  [2W]  [M]  [3D]  [1D]          ...           [⚙ Settings]    │ 5%
└─────────────────────────────────────────────────────────────────┘
```

## View Types Visualization

### 1. Two-Week View (2W) - Default
```
┌─────────────────────────────────────────────────────────────────┐
│              January 15, 2024 - January 28, 2024                │
├──┬──┬──┬──┬──┬──┬──┬──┬──┬──┬──┬──┬──┬──────────────────────────┤
│Mo│Tu│We│Th│Fr│Sa│Su│Mo│Tu│We│Th│Fr│Sa│Su                      │
│15│16│17│18│19│20│21│22│23│24│25│26│27│28                      │
├──┼──┼──┼──┼──┼──┼──┼──┼──┼──┼──┼──┼──┼──────────────────────────┤
│  │  │  │  │  │  │  │  │  │  │  │  │  │                        │
│██│██│██│██│██│██│██│██│██│██│██│██│██│██  Legend:             │
││││││││││││││                        │
││││││││││││││                        │  ██ = Work (Blue)        │
││││││││││││││                        │  ██ = Personal (Green)   │
│██│██│██│██│██│  │  │██│██│██│██│██│  │  ██ = Birthday (Orange) │
│  │  │  │  │  │  │  │  │  │  │  │  │  │                        │
└──┴──┴──┴──┴──┴──┴──┴──┴──┴──┴──┴──┴──┴──────────────────────────┘
• Each column shows 1 day
• Events displayed as colored cards
• Swipe left/right moves ±14 days
```

### 2. Month View (M)
```
┌─────────────────────────────────────────────────────────────────┐
│                        January 2024                             │
├───────┬───────┬───────┬───────┬───────┬───────┬───────┬─────────┤
│Sunday │Monday │Tuesday│Wednes.│Thursd.│Friday │Saturd.│         │
├───────┼───────┼───────┼───────┼───────┼───────┼───────┼─────────┤
│       │   1   │   2   │   3   │   4   │   5   │   6   │         │
│       │  ●●   │  ●●   │  ●●●  │  ●●   │  ●●●  │  ●●   │         │
├───────┼───────┼───────┼───────┼───────┼───────┼───────┼─────────┤
│   7   │   8   │   9   │   10  │   11  │   12  │   13  │         │
│  ●●   │  ●●●  │  ●●   │  ●●   │  ●●●  │  ●●   │  ●●   │         │
├───────┼───────┼───────┼───────┼───────┼───────┼───────┼─────────┤
│   14  │   15  │   16  │   17  │   18  │   19  │   20  │         │
│  ●●   │  ●●●● │  ●●   │  ●●●  │  ●●   │  ●●   │  ●●   │         │
├───────┼───────┼───────┼───────┼───────┼───────┼───────┼─────────┤
│   21  │   22  │   23  │   24  │   25  │   26  │   27  │         │
│  ●●   │  ●●●  │  ●●   │  ●●   │  ●●●  │  ●●   │  ●●   │         │
└───────┴───────┴───────┴───────┴───────┴───────┴───────┴─────────┘
• Traditional calendar grid (7 columns)
• Colored dots indicate events (up to 5 shown)
• Swipe left/right moves ±1 month
```

### 3. Three-Day View (3D)
```
┌─────────────────────────────────────────────────────────────────┐
│              January 15 - January 17, 2024                      │
├──────┬───────────────┬───────────────┬───────────────┬──────────┤
│ Time │  Monday 15    │  Tuesday 16   │ Wednesday 17  │          │
├──────┼───────────────┼───────────────┼───────────────┼──────────┤
│00:00 │               │               │               │          │
│01:00 │               │               │               │          │
│  :   │               │               │               │          │
│08:00 │ ┌───────────┐ │               │               │          │
│09:00 │ │Team Meet. │ │ ┌───────────┐ │               │          │
│10:00 │ └───────────┘ │ │Client Call│ │ ┌───────────┐ │          │
│11:00 │               │ └───────────┘ │ │Dept Sync  │ │          │
│12:00 │               │               │ └───────────┘ │          │
│13:00 │               │               │ ┌───────────┐ │          │
│14:00 │ ┌───────────┐ │               │ │Lunch      │ │          │
│15:00 │ │Project    │ │               │ └───────────┘ │          │
│16:00 │ │Review     │ │               │               │          │
│  :   │ └───────────┘ │               │               │          │
│23:00 │               │               │               │          │
└──────┴───────────────┴───────────────┴───────────────┴──────────┘
• Time column on left (00:00 - 23:00)
• 3 day columns
• Events shown as colored blocks in time slots
• Horizontal scrolling enabled
• Swipe left/right moves ±3 days
```

### 4. Single-Day View (1D)
```
┌─────────────────────────────────────────────────────────────────┐
│                   Monday, January 15, 2024                      │
├──────┬──────────────────────────────────────────────────────────┤
│ Time │                       Events                             │
├──────┼──────────────────────────────────────────────────────────┤
│00:00 │                                                          │
│  :   │                                                          │
│08:00 │ ┌────────────────────────────────────────────────────┐  │
│      │ │ Pay Bills (Reminder)              08:00 - 08:30    │  │
│      │ └────────────────────────────────────────────────────┘  │
│09:00 │ ┌────────────────────────────────────────────────────┐  │
│      │ │ Team Meeting (Work)               09:00 - 10:00    │  │
│10:00 │ └────────────────────────────────────────────────────┘  │
│  :   │                                                          │
│14:00 │ ┌────────────────────────────────────────────────────┐  │
│      │ │ Project Review (Work)             14:00 - 15:30    │  │
│15:00 │ │                                                     │  │
│      │ └────────────────────────────────────────────────────┘  │
│  :   │                                                          │
│23:00 │                                                          │
└──────┴──────────────────────────────────────────────────────────┘
• Time column on left (00:00 - 23:00)
• Large event cards with full details
• Vertical scrolling
• Swipe left/right moves ±1 day
```

## Settings Screen
```
┌─────────────────────────────────────────────────────────────────┐
│  ← Settings                                                     │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  Default View                                                   │
│  Choose which view type to display on startup                   │
│                                                                 │
│  ○ Two week view                                                │
│  ● Month view                    ← Currently selected          │
│  ○ Three day view                                               │
│  ○ One day view                                                 │
│                                                                 │
│  ───────────────────────────────────────────────────────────    │
│                                                                 │
│  Google Account                                                 │
│                                                                 │
│  ┌────────────────────────────────┐                            │
│  │  Connect Google Account       │  (Coming soon - disabled)   │
│  └────────────────────────────────┘                            │
│                                                                 │
│  Google Calendar integration coming soon                        │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

## Control Bar Detail
```
┌─────────────────────────────────────────────────────────────────┐
│  ┌────┐  ┌────┐  ┌────┐  ┌────┐              ┌──────────────┐  │
│  │ 2W │  │ M  │  │ 3D │  │ 1D │              │ ⚙ Settings   │  │
│  └────┘  └────┘  └────┘  └────┘              └──────────────┘  │
│    ▲                                                            │
│ Selected (highlighted)                                          │
│                                                                 │
│ • 64dp × 64dp buttons                                           │
│ • 24sp text size                                                │
│ • Clear, simple labels                                          │
│ • Active view highlighted                                       │
└─────────────────────────────────────────────────────────────────┘
```

## Color Coding Example
```
Event Types and Colors:

┌───────────────────────────────────────────────────┐
│  Work          │ ████████ │ #2196F3 (Blue)       │
│  Personal      │ ████████ │ #4CAF50 (Green)      │
│  Birthday      │ ████████ │ #FF5722 (Orange-Red) │
│  Reminder      │ ████████ │ #FF9800 (Orange)     │
│  Holiday       │ ████████ │ #9C27B0 (Purple)     │
└───────────────────────────────────────────────────┘

All events use these colors consistently across all views.
White text on colored backgrounds ensures readability.
```

## Interaction Examples

### Swipe Navigation
```
Current View: Two-Week (Jan 15-28)

Swipe Left →  : Advance to Jan 29 - Feb 11
Swipe Right ← : Go back to Jan 1-14
```

### View Switching
```
Step 1: User taps [M] button in control bar
Step 2: ViewPager adapter switches to Month view
Step 3: Calendar resets to current month
Step 4: [M] button highlights, others unhighlight
Step 5: User can now swipe monthly instead of bi-weekly
```

### Settings Flow
```
Step 1: User taps [⚙ Settings] in control bar
Step 2: Settings screen opens
Step 3: User selects "Three day view" radio button
Step 4: Preference saved to SharedPreferences
Step 5: User presses back button
Step 6: Returns to calendar
Step 7: Next app launch will default to Three-day view
```

## Event Card Design
```
┌─────────────────────────────────────┐
│ Team Meeting                       │ ← Event title (20sp, bold)
│ 09:00 - 10:00                      │ ← Time (16sp)
│                                     │
│ Background: Event type color        │
│ Text: White                         │
│ Padding: 12dp                       │
│ Corner radius: 8dp                  │
│ Elevation: 2dp                      │
└─────────────────────────────────────┘
```

## Responsive Scaling

The UI is optimized for 3840x2160 but scales appropriately:

- **Text**: Uses sp units (scales with system font size)
- **Spacing**: Uses dp units (density-independent)
- **Touch Targets**: Minimum 64dp (Android guidelines)
- **Layout**: ConstraintLayout adapts to different screen sizes
- **ViewPager**: Fills available space after control bar

## Accessibility Features

- Large text sizes for distance viewing
- High contrast colors
- Minimum 64dp touch targets
- Content descriptions on all interactive elements
- Clear visual hierarchy
- Consistent color coding

## Implementation Notes

All mockups represent the actual implementation in the code:
- MainActivity.kt handles view switching and navigation
- Fragment classes implement each view type
- CalendarPagerAdapter manages ViewPager2 pages
- Layouts match these visual specifications
- Colors and dimensions are defined in resources
- Example data populates all views realistically
