# Resource Provider Mod (FABRIC)

This library is used to dynamically append Skyblock Creator world type(s) since
world presets in 1.19.3 is now controlled completely through datapack.

[You can find the original library's git repository here](https://github.com/CebbysMods/respro-mod)

---

## Information
Resource Provider Mod - Respro. Is a dynamic resource
generation library for minecraft highly inspired by
[Artifice](https://github.com/natanfudge/artifice) by
[natanfudge](https://github.com/natanfudge) and
[ARRP](https://github.com/Devan-Kerman/ARRP) by
[Devan-Kerman](https://github.com/Devan-Kerman)

## API
Mod is published on custom maven repository. You can fetch it and
include it in your mod in following way:
```groovy
repositories {
    maven {
        url = "https://prod-cebbys-repomanager.herokuapp.com"
    }
}
```
Only versions above 0.1.1 are available
```groovy
dependencies {
    // Example include with version template
    include "lv.cebbys.mcmods:respro:${version}"
    
    // Example include of version 0.1.1
    include "lv.cebbys.mcmods:respro:0.1.1"
}
```

## Guide
In progress

---
