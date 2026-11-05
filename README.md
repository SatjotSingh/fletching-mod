# Daplow's Fletching Mod By Satjot Singh

A Fabric mod for Minecraft 1.19.2 that 100+ Arrow Types and Fletching Table interface to the game.

---

## 🚀 Features
* **Fletching Table GUI:** A new gui for the Fletching Table block allowing the player to make all 100+ custom arrow times in 1 gui.
* **100+ Arrow Variants:** Using a modular arrow builder, the player can craft 100+ different types of arrows, from water arrows, tnt arrows, to homing arrows.
* **Datapack Driven Arrow Registry:** Allows player/user to edit how some arrow types function through Data Packs.

## 🛠️ Tech Stack & Architecture
* **Language:** Java
* **Mod Loader:** Fabric API
* **Build Tool:** Gradle

## 🧠 Technical Highlights & Challenges
* - Solved client-server synchronization challenges by implementing one custom network packet for the Fletching Table GUI Handler.
* - Using interface classes for both the Arrow Stem and Arrow Feather to add full modular support for making new arrow variants.  Also can be used to make add-on mods to also make custom arrows.
* - Making a modular crafting recipe manager and builder.
