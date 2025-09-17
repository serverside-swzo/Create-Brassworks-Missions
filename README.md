# Create: Brassworks Missions

**Create: Brassworks Missions** is an addon for [Create: Numismatics](https://modrinth.com/mod/create-numismatics) that introduces a fully data-driven missions system for **Minecraft 1.21.1**.  
Originally made for the [**Brassworks SMP**](https://modrinth.com/modpack/brassworks-smp-modpack), but available for anyone to use.

## Features
- Track missions directly on your HUD.  
- Clean, Create-style UI.  
- Open the missions menu with the default keybind **H**.  
- Administrator commands to manage player missions.  
- 24 unique mission types (with more planned).  
- Fully data-driven – add your own missions via datapacks.  

<img width="1408" height="683" alt="image" src="https://github.com/user-attachments/assets/4ae53084-d48a-4847-81cf-da010cfdcfa6" />


## Custom Missions
Custom missions are stored in datapacks. Place your mission definitions in:

```
data/brassworksmissions/missions/missions.json
```

The mod ships with a built-in datapack containing examples.  

### Example JSON
```json
{
  "id": "brassworksmissions:crush_item",
  "weight": 4.0,
  "titles": [
    "Ore Processor",
    "Crushing it!",
    "Wheel of Fortune"
  ],
  "requirement": {
    "requirementType": "item",
    "item": "create:crushed_raw_iron",
    "minAmount": 128,
    "maxAmount": 256
  },
  "reward": {
    "item": "numismatics:spur",
    "minAmount": 8,
    "maxAmount": 16
  }
}
```

## Requirements
- [Create: Numismatics](https://modrinth.com/mod/create-numismatics)  

## Notes
- Works in both singleplayer and multiplayer.  
- Developed for the Brassworks SMP but free for use on any server or world.  

## Contributing
Localizations and pull requests are welcome! Feel free to open issues or PRs to help improve the project.  

---
