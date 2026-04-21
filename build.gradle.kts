plugins {
    id("dev.prism")
}

group = "com.leclowndu93150"
version = "1.0.2"

prism {
    metadata {
        modId = "particle_effects"
        name = "Particle Effects"
        description = ""
        license = "MIT"
        author("Leclowndu93150")
    }

    version("1.21.11") {
        parchmentMinecraftVersion = "1.21.11"
        parchmentMappingsVersion = "2025.12.20"

        neoforge {
            loaderVersion = "21.11.38-beta"
            loaderVersionRange = "[4,)"
        }
    }

    version("26.1.2") {
        minecraftVersions("26.1", "26.1.1", "26.1.2")
        neoforge {
            loaderVersion = "26.1.2.22-beta"
            loaderVersionRange = "[4,)"
        }
    }

    publishing {
        type = STABLE

        curseforge {
            accessToken = providers.environmentVariable("CURSEFORGE_TOKEN")
            projectId = "1274497"
        }
    }
}
