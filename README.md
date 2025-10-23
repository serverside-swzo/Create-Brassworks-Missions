# Create: Brassworks Missions

**Create: Brassworks Missions** is an addon for [Create: Numismatics](https://modrinth.com/mod/create-numismatics) that introduces a fully data-driven missions system for **Minecraft 1.21.1**.  
Originally made for the [**Brassworks SMP**](https://modrinth.com/modpack/brassworks-smp-modpack), but available for anyone to use.

## License

This project is licensed under the [MIT License](./LICENSE).
While credit is **not required**, giving **acknowledgment to swzo** when forking or reusing parts of this project is always appreciated.
Contributions, suggestions, and improvements are welcome, feel free to open a pull request or start a discussion!

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

An example datapack can be found here [Example Datapack](https://github.com/serverside-swzo/Create-Brassworks-Missions/blob/master/brassworks_missions_example_datapack/brassworks_missions_example.zip), and a guide for the datapacks contents can be found below

### Example JSON
data/brassworksmissions/missions/missions.json
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
    "minAmount": 8,
    "maxAmount": 16
  }
}
```
data/brassworksmissions/missions/missions_reward.json
```json
{
  "item": "numismatics:spur" //This can be any item, this dictates what the reward item for the missions will be
}
```

## Notes
- Works in both singleplayer and multiplayer.  
- Developed for the Brassworks SMP but free for use on any server or world.  

## Contributing
Localizations and pull requests are welcome! Feel free to open issues or PRs to help improve the project.  

---
