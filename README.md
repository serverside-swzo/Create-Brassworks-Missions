# Create: Brassworks Missions
[![Modrinth](https://wsrv.nl/?url=https%3A%2F%2Fcdn.bypixel.dev%2Fraw%2F4dlsHJ.png&n=-1)](https://modrinth.com/mod/create-brassworks-missions)
[![Curseforge](https://i.ibb.co/F4MtFnv7/cozy-64h.png)](https://www.curseforge.com/minecraft/mc-mods/create-brassworks-missions)
[![Github](https://i.ibb.co/DPnZdBK5/41dfe80a399c0c8466f8cecc9f35048de5066e11-1.png)](https://github.com/Brassworks-smp/Create-Brassworks-Missions)
[![Discord](https://wsrv.nl/?url=https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/social/discord-plural_vector.svg&h=64)](https://discord.com/invite/nDhkgzAPR2)

[![CodeFactor](https://www.codefactor.io/repository/github/brassworks-smp/create-brassworks-missions/badge)](https://www.codefactor.io/repository/github/brassworks-smp/create-brassworks-missions)
[![GitHub issues](https://img.shields.io/github/issues/Brassworks-smp/Create-Brassworks-Missions.svg)](https://github.com/Brassworks-smp/Create-Brassworks-Missions/issues)
[![GitHub commits (total)](https://img.shields.io/github/commit-activity/t/Brassworks-smp/Create-Brassworks-Missions/master.svg)](https://github.com/Brassworks-smp/Create-Brassworks-Missions/commits)
![Lines of Code](https://img.shields.io/endpoint?url=https://ghloc.vercel.app/api/Brassworks-smp/Create-Brassworks-Missions/badge&color=2ecc71)

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
