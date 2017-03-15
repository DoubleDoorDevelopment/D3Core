D3 Core
=======

DoubleDoorDevelopment Core

Download from our [website](http://doubledoordev.net/) or our [jenkins server](https://jenkins.dries007.net/view/D3_mods/)

Big thanks to @mallrat208 for updating to 1.9 / 1.10 / 1.11.2

Materials
---------

If you want to add crafting / repair materials to *existing* tool materials, you can do so by making a file called `materials.json` inside of `config/D3Core`.

The itemstack regex pattern is:`^(?:(?<mod>.*):)?(?<name>.*?) ?(?<meta>\*|\d+)? ?(?<stacksize>\d+)?$`. Example: `[modid:]item_name [meta] [stacksize]`

Due to Vanilla Limitation™ you can't really use MetaData based ingots in your repair materials. I suggest using Mine/Crafttweaker and you can't always use the anvil to repair (combine crafting only).

Document structure example:

```json
{
  "<Material Name>": "<Itemstack>",
  "FluffyClouds": "somemod:clouds 1 1"
}
```

Make sure your names are correctly spelled, everything is case sensitive!

- Default modid is "minecraft",
- Default stacksize is 1,
- Default metadata is 32767, otherwise known as the OreDictionary Wildcard Value,
- If a material does not exist, it is NOT created, the entry is simply skipped.
- If a item is specified from a mod that is not loaded, the entry is also skipped.
